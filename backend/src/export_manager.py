"""
Export Manager for multi-platform ad generation
Handles image and video export to Facebook and Instagram formats
"""

import os
from pathlib import Path
from typing import Tuple, Dict, List
import cv2
from PIL import Image
import subprocess
from config.export_formats import get_format_specs, INSTAGRAM_FORMATS, FACEBOOK_FORMATS


class ExportManager:
    """Manages exporting processed media to platform-specific formats"""
    
    def __init__(self, output_base_path: str = "./media/exports"):
        """
        Initialize ExportManager
        
        Args:
            output_base_path (str): Base directory for exports
        """
        self.output_base_path = output_base_path
        self.ensure_directories()
    
    def ensure_directories(self):
        """Create necessary output directories"""
        platforms = ["instagram", "facebook"]
        for platform in platforms:
            platform_dir = os.path.join(self.output_base_path, platform)
            os.makedirs(platform_dir, exist_ok=True)
            
            # Create subdirectories for each media type
            for subdir in ["images", "videos", "reels"]:
                os.makedirs(os.path.join(platform_dir, subdir), exist_ok=True)
    
    def export_image_to_platform(self, 
                                 input_image_path: str,
                                 platform: str,
                                 format_type: str,
                                 output_filename: str = None) -> Dict:
        """
        Export image to platform-specific format
        
        Args:
            input_image_path (str): Path to input image
            platform (str): 'instagram' or 'facebook'
            format_type (str): Format type (e.g., 'feed_post_image')
            output_filename (str): Custom output filename
        
        Returns:
            dict: Export result with path and metadata
        """
        specs = get_format_specs(platform, format_type)
        
        if not specs:
            return {
                "success": False,
                "error": f"Format {format_type} not found for {platform}"
            }
        
        try:
            # Load image
            image = Image.open(input_image_path)
            
            # Get target dimensions
            target_width, target_height = specs["dimensions"]
            
            # Resize image while maintaining aspect ratio (with letterboxing if needed)
            image_resized = self._resize_with_aspect_ratio(
                image, 
                target_width, 
                target_height
            )
            
            # Generate output path
            if not output_filename:
                output_filename = f"{platform}_{format_type}_{os.path.basename(input_image_path)}"
            
            output_dir = os.path.join(self.output_base_path, platform, "images")
            output_path = os.path.join(output_dir, output_filename)
            
            # Save image with quality settings
            quality = specs.get("quality", 95)
            image_resized.save(output_path, quality=quality)
            
            return {
                "success": True,
                "platform": platform,
                "format": format_type,
                "output_path": output_path,
                "dimensions": specs["dimensions"],
                "aspect_ratio": specs["aspect_ratio"],
                "file_size": os.path.getsize(output_path),
                "format_specs": specs
            }
        
        except Exception as e:
            return {
                "success": False,
                "error": str(e),
                "platform": platform,
                "format": format_type
            }
    
    def export_video_to_platform(self,
                                 input_video_path: str,
                                 platform: str,
                                 format_type: str,
                                 output_filename: str = None) -> Dict:
        """
        Export video to platform-specific format
        
        Args:
            input_video_path (str): Path to input video
            platform (str): 'instagram' or 'facebook'
            format_type (str): Format type (e.g., 'reel' for Instagram)
            output_filename (str): Custom output filename
        
        Returns:
            dict: Export result with path and metadata
        """
        specs = get_format_specs(platform, format_type)
        
        if not specs or specs.get("format") != "mp4":
            return {
                "success": False,
                "error": f"Video format {format_type} not found for {platform}"
            }
        
        try:
            # Generate output path
            if not output_filename:
                base_name = os.path.splitext(os.path.basename(input_video_path))[0]
                output_filename = f"{platform}_{format_type}_{base_name}.mp4"
            
            output_dir = os.path.join(self.output_base_path, platform, "videos")
            output_path = os.path.join(output_dir, output_filename)
            
            # Build FFmpeg command
            width, height = specs["dimensions"]
            bitrate = specs.get("bitrate", "8000k")
            frame_rate = specs.get("frame_rate", 30)
            
            cmd = [
                "ffmpeg",
                "-i", input_video_path,
                "-vf", f"scale={width}:{height}:force_original_aspect_ratio=decrease,pad={width}:{height}:(ow-iw)/2:(oh-ih)/2",
                "-r", str(frame_rate),
                "-b:v", bitrate,
                "-c:v", "libx264",
                "-c:a", "aac",
                "-b:a", "128k",
                "-movflags", "+faststart",
                output_path
            ]
            
            # Execute FFmpeg
            result = subprocess.run(cmd, capture_output=True, text=True)
            
            if result.returncode != 0:
                return {
                    "success": False,
                    "error": result.stderr,
                    "platform": platform,
                    "format": format_type
                }
            
            return {
                "success": True,
                "platform": platform,
                "format": format_type,
                "output_path": output_path,
                "dimensions": specs["dimensions"],
                "aspect_ratio": specs["aspect_ratio"],
                "file_size": os.path.getsize(output_path),
                "format_specs": specs
            }
        
        except Exception as e:
            return {
                "success": False,
                "error": str(e),
                "platform": platform,
                "format": format_type
            }
    
    def export_to_all_instagram_formats(self,
                                       input_path: str,
                                       media_type: str = "image") -> List[Dict]:
        """
        Export media to all recommended Instagram formats
        
        Args:
            input_path (str): Path to input media
            media_type (str): 'image' or 'video'
        
        Returns:
            list: List of export results
        """
        results = []
        
        if media_type == "image":
            formats = ["feed_post_image", "story", "carousel_image"]
            for fmt in formats:
                result = self.export_image_to_platform(input_path, "instagram", fmt)
                results.append(result)
        
        elif media_type == "video":
            result = self.export_video_to_platform(input_path, "instagram", "reel")
            results.append(result)
        
        return results
    
    def export_to_all_facebook_formats(self,
                                      input_path: str,
                                      media_type: str = "image") -> List[Dict]:
        """
        Export media to all recommended Facebook formats
        
        Args:
            input_path (str): Path to input media
            media_type (str): 'image' or 'video'
        
        Returns:
            list: List of export results
        """
        results = []
        
        if media_type == "image":
            formats = ["feed_image", "feed_image_vertical", "feed_image_square"]
            for fmt in formats:
                result = self.export_image_to_platform(input_path, "facebook", fmt)
                results.append(result)
        
        elif media_type == "video":
            formats = ["video_feed", "video_vertical"]
            for fmt in formats:
                result = self.export_video_to_platform(input_path, "facebook", fmt)
                results.append(result)
        
        return results
    
    def export_to_both_platforms(self,
                                input_path: str,
                                media_type: str = "image") -> Dict:
        """
        Export media to both Instagram and Facebook recommended formats
        
        Args:
            input_path (str): Path to input media
            media_type (str): 'image' or 'video'
        
        Returns:
            dict: Combined results from both platforms
        """
        return {
            "instagram": self.export_to_all_instagram_formats(input_path, media_type),
            "facebook": self.export_to_all_facebook_formats(input_path, media_type)
        }
    
    @staticmethod
    def _resize_with_aspect_ratio(image: Image.Image,
                                  target_width: int,
                                  target_height: int,
                                  bg_color: Tuple = (255, 255, 255)) -> Image.Image:
        """
        Resize image to target dimensions while maintaining aspect ratio
        Uses letterboxing if needed
        
        Args:
            image (PIL.Image): Input image
            target_width (int): Target width
            target_height (int): Target height
            bg_color (tuple): Background color for letterboxing (RGB)
        
        Returns:
            PIL.Image: Resized image with aspect ratio maintained
        """
        # Calculate aspect ratios
        img_aspect = image.width / image.height
        target_aspect = target_width / target_height
        
        if img_aspect > target_aspect:
            # Image is wider, scale by height
            new_height = target_height
            new_width = int(new_height * img_aspect)
        else:
            # Image is taller, scale by width
            new_width = target_width
            new_height = int(new_width / img_aspect)
        
        # Resize image
        image_resized = image.resize((new_width, new_height), Image.Resampling.LANCZOS)
        
        # Create canvas with target dimensions
        canvas = Image.new('RGB', (target_width, target_height), bg_color)
        
        # Calculate position to center image
        x = (target_width - new_width) // 2
        y = (target_height - new_height) // 2
        
        # Paste resized image onto canvas
        canvas.paste(image_resized, (x, y))
        
        return canvas
