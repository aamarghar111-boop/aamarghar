"""
Google Drive Integration Module
Handles authentication, media fetching, and metadata storage
"""

import os
import json
import pickle
from typing import List, Dict, Optional
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from google.api_core.exceptions import GoogleAPIError
from googleapiclient.discovery import build
from googleapiclient.http import MediaIoBaseDownload
import io


class GoogleDriveManager:
    """Manages Google Drive API interactions"""
    
    # Scopes required for accessing Google Drive
    SCOPES = ['https://www.googleapis.com/auth/drive.readonly']
    
    # Supported media types
    SUPPORTED_MEDIA_TYPES = {
        'image': ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/bmp'],
        'video': ['video/mp4', 'video/mpeg', 'video/quicktime', 'video/x-msvideo', 'video/webm']
    }
    
    MIME_TYPE_TO_EXT = {
        'image/jpeg': '.jpg',
        'image/png': '.png',
        'image/gif': '.gif',
        'image/webp': '.webp',
        'image/bmp': '.bmp',
        'video/mp4': '.mp4',
        'video/mpeg': '.mpeg',
        'video/quicktime': '.mov',
        'video/x-msvideo': '.avi',
        'video/webm': '.webm'
    }
    
    def __init__(self, credentials_file: str = 'credentials.json', token_file: str = 'token.pickle'):
        """
        Initialize Google Drive Manager
        
        Args:
            credentials_file (str): Path to OAuth credentials JSON
            token_file (str): Path to store authentication token
        """
        self.credentials_file = credentials_file
        self.token_file = token_file
        self.service = None
        self.creds = None
    
    def authenticate(self) -> bool:
        """
        Authenticate with Google Drive API
        
        Returns:
            bool: True if authentication successful, False otherwise
        """
        try:
            # Check if token already exists
            if os.path.exists(self.token_file):
                with open(self.token_file, 'rb') as token:
                    self.creds = pickle.load(token)
            
            # If credentials expired or don't exist, create new ones
            if not self.creds or not self.creds.valid:
                if self.creds and self.creds.expired and self.creds.refresh_token:
                    self.creds.refresh(Request())
                else:
                    if not os.path.exists(self.credentials_file):
                        raise FileNotFoundError(
                            f"Credentials file not found: {self.credentials_file}\n"
                            "Download from Google Cloud Console and save as 'credentials.json'"
                        )
                    
                    flow = InstalledAppFlow.from_client_secrets_file(
                        self.credentials_file,
                        self.SCOPES
                    )
                    self.creds = flow.run_local_server(port=0)
                
                # Save token for future use
                with open(self.token_file, 'wb') as token:
                    pickle.dump(self.creds, token)
            
            # Build the Drive API service
            self.service = build('drive', 'v3', credentials=self.creds)
            return True
        
        except Exception as e:
            print(f"Authentication error: {str(e)}")
            return False
    
    def list_media_in_folder(self, folder_id: str, media_type: str = 'image') -> List[Dict]:
        """
        List all media files in a Google Drive folder
        
        Args:
            folder_id (str): Google Drive folder ID
            media_type (str): 'image', 'video', or 'all'
        
        Returns:
            list: List of media file metadata
        """
        if not self.service:
            raise RuntimeError("Not authenticated. Call authenticate() first.")
        
        try:
            media_list = []
            
            # Build MIME type query
            if media_type == 'all':
                all_mimes = self.SUPPORTED_MEDIA_TYPES['image'] + self.SUPPORTED_MEDIA_TYPES['video']
                mime_query = ' or '.join([f"mimeType='{mime}'" for mime in all_mimes])
            else:
                mimes = self.SUPPORTED_MEDIA_TYPES.get(media_type, [])
                mime_query = ' or '.join([f"mimeType='{mime}'" for mime in mimes])
            
            # Query for media files in folder
            query = f"'{folder_id}' in parents and ({mime_query}) and trashed=false"
            
            results = self.service.files().list(
                q=query,
                spaces='drive',
                fields='files(id, name, mimeType, size, createdTime, modifiedTime, webViewLink)',
                pageSize=100
            ).execute()
            
            files = results.get('files', [])
            
            for file in files:
                file_type = 'image' if file['mimeType'] in self.SUPPORTED_MEDIA_TYPES['image'] else 'video'
                
                media_list.append({
                    'id': file['id'],
                    'name': file['name'],
                    'mime_type': file['mimeType'],
                    'extension': self.MIME_TYPE_TO_EXT.get(file['mimeType'], ''),
                    'size': int(file.get('size', 0)),
                    'type': file_type,
                    'created_time': file.get('createdTime'),
                    'modified_time': file.get('modifiedTime'),
                    'web_view_link': file.get('webViewLink')
                })
            
            return media_list
        
        except GoogleAPIError as e:
            print(f"Error listing media: {str(e)}")
            return []
    
    def download_file(self, file_id: str, output_path: str) -> bool:
        """
        Download a file from Google Drive
        
        Args:
            file_id (str): Google Drive file ID
            output_path (str): Local path to save file
        
        Returns:
            bool: True if successful, False otherwise
        """
        if not self.service:
            raise RuntimeError("Not authenticated. Call authenticate() first.")
        
        try:
            # Create output directory if it doesn't exist
            os.makedirs(os.path.dirname(output_path), exist_ok=True)
            
            # Download file
            request = self.service.files().get_media(fileId=file_id)
            file = io.BytesIO()
            downloader = MediaIoBaseDownload(file, request)
            
            done = False
            while not done:
                status, done = downloader.next_chunk()
            
            # Write to disk
            with open(output_path, 'wb') as f:
                f.write(file.getvalue())
            
            return True
        
        except Exception as e:
            print(f"Error downloading file: {str(e)}")
            return False
    
    def download_media_batch(self, folder_id: str, output_dir: str, media_type: str = 'all') -> List[Dict]:
        """
        Download all media from a folder
        
        Args:
            folder_id (str): Google Drive folder ID
            output_dir (str): Directory to save files
            media_type (str): 'image', 'video', or 'all'
        
        Returns:
            list: List of downloaded files with metadata
        """
        media_list = self.list_media_in_folder(folder_id, media_type)
        downloaded = []
        
        for i, media in enumerate(media_list, 1):
            file_name = media['name']
            output_path = os.path.join(output_dir, file_name)
            
            print(f"[{i}/{len(media_list)}] Downloading: {file_name}...", end=' ')
            
            if self.download_file(media['id'], output_path):
                print("✓")
                downloaded.append({
                    **media,
                    'local_path': output_path
                })
            else:
                print("✗")
        
        return downloaded
    
    def search_retail_folders(self, query: str = 'retail') -> List[Dict]:
        """
        Search for retail shop folders in Google Drive
        
        Args:
            query (str): Search query for folder names
        
        Returns:
            list: List of matching folders
        """
        if not self.service:
            raise RuntimeError("Not authenticated. Call authenticate() first.")
        
        try:
            results = self.service.files().list(
                q=f"name contains '{query}' and mimeType='application/vnd.google-apps.folder' and trashed=false",
                spaces='drive',
                fields='files(id, name, createdTime, modifiedTime)',
                pageSize=50
            ).execute()
            
            folders = results.get('files', [])
            return [
                {
                    'id': folder['id'],
                    'name': folder['name'],
                    'created_time': folder.get('createdTime'),
                    'modified_time': folder.get('modifiedTime')
                }
                for folder in folders
            ]
        
        except GoogleAPIError as e:
            print(f"Error searching folders: {str(e)}")
            return []
    
    def get_folder_info(self, folder_id: str) -> Dict:
        """
        Get information about a folder
        
        Args:
            folder_id (str): Google Drive folder ID
        
        Returns:
            dict: Folder metadata
        """
        if not self.service:
            raise RuntimeError("Not authenticated. Call authenticate() first.")
        
        try:
            folder = self.service.files().get(
                fileId=folder_id,
                fields='id, name, createdTime, modifiedTime, description'
            ).execute()
            
            # Count media files
            media_list = self.list_media_in_folder(folder_id, 'all')
            image_count = sum(1 for m in media_list if m['type'] == 'image')
            video_count = sum(1 for m in media_list if m['type'] == 'video')
            total_size = sum(m['size'] for m in media_list)
            
            return {
                'id': folder['id'],
                'name': folder['name'],
                'description': folder.get('description', ''),
                'created_time': folder.get('createdTime'),
                'modified_time': folder.get('modifiedTime'),
                'image_count': image_count,
                'video_count': video_count,
                'total_files': len(media_list),
                'total_size_mb': round(total_size / (1024 * 1024), 2)
            }
        
        except GoogleAPIError as e:
            print(f"Error getting folder info: {str(e)}")
            return {}
