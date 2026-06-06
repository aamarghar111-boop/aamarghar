"""
Retail Ad Creator - Backend API
Main Flask application for media processing and ad generation
"""

from flask import Flask, jsonify, request
from flask_cors import CORS
from dotenv import load_dotenv
import os

# Load environment variables
load_dotenv()

# Initialize Flask app
app = Flask(__name__)
CORS(app)

# Configuration
app.config['DEBUG'] = os.getenv('FLASK_ENV') == 'development'
app.config['MAX_CONTENT_LENGTH'] = 500 * 1024 * 1024  # 500MB max file size


@app.route('/api/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'status': 'healthy',
        'service': 'Retail Ad Creator Backend',
        'version': '0.2.0'
    }), 200


@app.route('/api/drive/authorize', methods=['POST'])
def authorize_drive():
    """
    Initialize Google Drive API authorization
    Triggers OAuth flow if credentials not saved
    
    Returns:
        dict: Authorization status and next steps
    """
    try:
        from src.google_drive import GoogleDriveManager
        
        manager = GoogleDriveManager()
        
        if manager.authenticate():
            return jsonify({
                'status': 'authorized',
                'message': 'Successfully authenticated with Google Drive',
                'ready': True
            }), 200
        else:
            return jsonify({
                'status': 'failed',
                'message': 'Failed to authenticate. Ensure credentials.json exists.',
                'ready': False
            }), 401
    
    except Exception as e:
        return jsonify({
            'status': 'error',
            'message': str(e),
            'ready': False
        }), 500


@app.route('/api/drive/folders/search', methods=['GET'])
def search_drive_folders():
    """
    Search for retail folders in Google Drive
    
    Query Parameters:
        - query: Search keyword (default: 'retail')
    
    Returns:
        list: Matching folders
    """
    try:
        from src.google_drive import GoogleDriveManager
        
        query = request.args.get('query', 'retail')
        manager = GoogleDriveManager()
        
        if not manager.authenticate():
            return jsonify({'error': 'Failed to authenticate with Google Drive'}), 401
        
        folders = manager.search_retail_folders(query)
        
        return jsonify({
            'status': 'success',
            'query': query,
            'count': len(folders),
            'folders': folders
        }), 200
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/api/drive/folder/<folder_id>', methods=['GET'])
def get_folder_info(folder_id):
    """
    Get information about a Google Drive folder
    
    Path Parameters:
        - folder_id: Google Drive folder ID
    
    Returns:
        dict: Folder info with media counts
    """
    try:
        from src.google_drive import GoogleDriveManager
        
        manager = GoogleDriveManager()
        
        if not manager.authenticate():
            return jsonify({'error': 'Failed to authenticate with Google Drive'}), 401
        
        folder_info = manager.get_folder_info(folder_id)
        
        if not folder_info:
            return jsonify({'error': 'Folder not found'}), 404
        
        return jsonify({
            'status': 'success',
            'folder': folder_info
        }), 200
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/api/drive/media/<folder_id>', methods=['GET'])
def list_folder_media(folder_id):
    """
    List all media in a Google Drive folder
    
    Path Parameters:
        - folder_id: Google Drive folder ID
    
    Query Parameters:
        - type: 'image', 'video', or 'all' (default: 'all')
    
    Returns:
        list: Media files with metadata
    """
    try:
        from src.google_drive import GoogleDriveManager
        
        media_type = request.args.get('type', 'all')
        manager = GoogleDriveManager()
        
        if not manager.authenticate():
            return jsonify({'error': 'Failed to authenticate with Google Drive'}), 401
        
        media_list = manager.list_media_in_folder(folder_id, media_type)
        
        return jsonify({
            'status': 'success',
            'folder_id': folder_id,
            'media_type': media_type,
            'count': len(media_list),
            'media': media_list
        }), 200
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/api/drive/download/<folder_id>', methods=['POST'])
def download_folder_media(folder_id):
    """
    Download all media from a Google Drive folder
    
    Request body:
    {
        "output_dir": "path/to/save",
        "media_type": "image|video|all"
    }
    """
    try:
        from src.google_drive import GoogleDriveManager
        
        data = request.get_json()
        output_dir = data.get('output_dir', './media/downloaded')
        media_type = data.get('media_type', 'all')
        
        manager = GoogleDriveManager()
        
        if not manager.authenticate():
            return jsonify({'error': 'Failed to authenticate with Google Drive'}), 401
        
        downloaded = manager.download_media_batch(folder_id, output_dir, media_type)
        
        return jsonify({
            'status': 'success',
            'folder_id': folder_id,
            'output_dir': output_dir,
            'downloaded_count': len(downloaded),
            'files': downloaded
        }), 200
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/api/media/fetch', methods=['POST'])
def fetch_media():
    """
    Fetch media files from Google Drive
    
    Request body:
    {
        "folder_id": "google_drive_folder_id",
        "output_dir": "path/to/save",
        "media_type": "image|video|all"
    }
    """
    try:
        data = request.get_json()
        folder_id = data.get('folder_id')
        
        if not folder_id:
            return jsonify({'error': 'folder_id is required'}), 400
        
        return download_folder_media(folder_id)
    
    except Exception as e:
        return jsonify({'error': str(e)}), 400


@app.route('/api/ad/generate', methods=['POST'])
def generate_ad():
    """
    Generate advertisement from media
    Supports export to Instagram and Facebook formats
    
    Request body:
    {
        "media_path": "path/to/media",
        "media_type": "image|video",
        "platforms": ["instagram", "facebook"],
        "formats": ["feed_post_image", "reel", "video_feed"]
    }
    """
    try:
        data = request.get_json()
        media_path = data.get('media_path')
        media_type = data.get('media_type', 'image')
        platforms = data.get('platforms', ['instagram', 'facebook'])
        
        return jsonify({
            'message': 'Ad generation pipeline',
            'platforms': platforms,
            'media_type': media_type,
            'status': 'processing'
        }), 501
    except Exception as e:
        return jsonify({'error': str(e)}), 400


@app.route('/api/export/formats', methods=['GET'])
def get_export_formats():
    """
    Get available export formats for Instagram and Facebook
    
    Query parameters:
    - platform: 'instagram', 'facebook', or 'all' (default: all)
    """
    from config.export_formats import ALL_FORMATS, RECOMMENDED_EXPORTS
    
    platform = request.args.get('platform', 'all')
    
    if platform == 'all':
        return jsonify({
            'instagram': {
                'formats': list(ALL_FORMATS['instagram'].keys()),
                'recommended': RECOMMENDED_EXPORTS['instagram'],
                'description': 'Instagram export formats'
            },
            'facebook': {
                'formats': list(ALL_FORMATS['facebook'].keys()),
                'recommended': RECOMMENDED_EXPORTS['facebook'],
                'description': 'Facebook export formats'
            }
        }), 200
    
    elif platform in ALL_FORMATS:
        return jsonify({
            platform: {
                'formats': list(ALL_FORMATS[platform].keys()),
                'recommended': RECOMMENDED_EXPORTS[platform],
                'specs': ALL_FORMATS[platform]
            }
        }), 200
    
    else:
        return jsonify({'error': f'Unknown platform: {platform}'}), 400


@app.route('/api/export/image', methods=['POST'])
def export_image():
    """
    Export image to platform-specific formats
    
    Request body:
    {
        "image_path": "path/to/image",
        "platform": "instagram|facebook",
        "format": "feed_post_image|reel|video_feed",
        "output_filename": "optional_custom_name"
    }
    """
    try:
        data = request.get_json()
        image_path = data.get('image_path')
        platform = data.get('platform')
        format_type = data.get('format')
        output_filename = data.get('output_filename')
        
        if not all([image_path, platform, format_type]):
            return jsonify({'error': 'Missing required fields: image_path, platform, format'}), 400
        
        return jsonify({
            'message': 'Image export endpoint',
            'platform': platform,
            'format': format_type,
            'status': 'pending'
        }), 501
    
    except Exception as e:
        return jsonify({'error': str(e)}), 400


@app.route('/api/export/video', methods=['POST'])
def export_video():
    """
    Export video to platform-specific formats
    
    Request body:
    {
        "video_path": "path/to/video",
        "platform": "instagram|facebook",
        "format": "reel|video_feed|video_vertical",
        "output_filename": "optional_custom_name"
    }
    """
    try:
        data = request.get_json()
        video_path = data.get('video_path')
        platform = data.get('platform')
        format_type = data.get('format')
        output_filename = data.get('output_filename')
        
        if not all([video_path, platform, format_type]):
            return jsonify({'error': 'Missing required fields: video_path, platform, format'}), 400
        
        return jsonify({
            'message': 'Video export endpoint',
            'platform': platform,
            'format': format_type,
            'status': 'pending'
        }), 501
    
    except Exception as e:
        return jsonify({'error': str(e)}), 400


@app.route('/api/reel/create', methods=['POST'])
def create_reel():
    """
    Create social media reel from media
    
    Request body:
    {
        "media_files": ["path/to/file1", "path/to/file2"],
        "platform": "instagram|facebook",
        "duration": 15-90 (seconds),
        "music": "optional_music_path",
        "effects": ["transition1", "transition2"]
    }
    """
    try:
        data = request.get_json()
        return jsonify({
            'message': 'Reel creation endpoint',
            'platform': data.get('platform'),
            'status': 'pending'
        }), 501
    
    except Exception as e:
        return jsonify({'error': str(e)}), 400


@app.errorhandler(404)
def not_found(error):
    return jsonify({'error': 'Endpoint not found'}), 404


@app.errorhandler(500)
def internal_error(error):
    return jsonify({'error': 'Internal server error'}), 500


if __name__ == '__main__':
    port = int(os.getenv('PORT', 5000))
    app.run(host='0.0.0.0', port=port, debug=app.config['DEBUG'])
