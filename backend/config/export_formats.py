"""
Configuration for export formats with crop methods
Defines optimal dimensions, aspect ratios, and processing methods for each platform
"""

# Instagram Specifications
INSTAGRAM_FORMATS = {
    "feed_post_image": {
        "name": "Instagram Feed Post (Image)",
        "dimensions": (1080, 1350),
        "width": 1080,
        "height": 1350,
        "aspect_ratio": "4:5",
        "min_width": 600,
        "format": "jpg",
        "quality": 95,
        "crop_method": "center_crop",
        "description": "Vertical feed post image"
    },
    "feed_post_square": {
        "name": "Instagram Feed Post (Square)",
        "dimensions": (1080, 1080),
        "width": 1080,
        "height": 1080,
        "aspect_ratio": "1:1",
        "min_width": 600,
        "format": "jpg",
        "quality": 95,
        "crop_method": "center_crop",
        "description": "Square feed post image"
    },
    "feed_post_landscape": {
        "name": "Instagram Feed Post (Landscape)",
        "dimensions": (1080, 566),
        "width": 1080,
        "height": 566,
        "aspect_ratio": "1.91:1",
        "min_width": 600,
        "format": "jpg",
        "quality": 95,
        "crop_method": "center_crop",
        "description": "Landscape feed post image"
    },
    "story": {
        "name": "Instagram Story",
        "dimensions": (1080, 1920),
        "width": 1080,
        "height": 1920,
        "aspect_ratio": "9:16",
        "min_width": 600,
        "format": "jpg",
        "quality": 95,
        "crop_method": "letterbox",
        "description": "Full-screen story image"
    },
    "reel": {
        "name": "Instagram Reel",
        "dimensions": (1080, 1920),
        "width": 1080,
        "height": 1920,
        "aspect_ratio": "9:16",
        "frame_rate": 30,
        "duration": "15-90",
        "format": "mp4",
        "codec": "h264",
        "bitrate": "8000k",
        "crop_method": "letterbox",
        "description": "Short-form vertical video (15-90 seconds)"
    },
    "carousel_image": {
        "name": "Instagram Carousel",
        "dimensions": (1080, 1350),
        "width": 1080,
        "height": 1350,
        "aspect_ratio": "4:5",
        "min_width": 600,
        "format": "jpg",
        "quality": 95,
        "max_items": 10,
        "crop_method": "center_crop",
        "description": "Image for carousel posts"
    }
}

# Facebook Specifications
FACEBOOK_FORMATS = {
    "feed_image": {
        "name": "Facebook Feed Image",
        "dimensions": (1200, 628),
        "width": 1200,
        "height": 628,
        "aspect_ratio": "1.91:1",
        "min_width": 600,
        "format": "jpg",
        "quality": 95,
        "crop_method": "center_crop",
        "description": "Standard feed post image"
    },
    "feed_image_vertical": {
        "name": "Facebook Feed Image (Vertical)",
        "dimensions": (1080, 1350),
        "width": 1080,
        "height": 1350,
        "aspect_ratio": "4:5",
        "min_width": 600,
        "format": "jpg",
        "quality": 95,
        "crop_method": "center_crop",
        "description": "Vertical feed post image"
    },
    "feed_image_square": {
        "name": "Facebook Feed Image (Square)",
        "dimensions": (1200, 1200),
        "width": 1200,
        "height": 1200,
        "aspect_ratio": "1:1",
        "min_width": 600,
        "format": "jpg",
        "quality": 95,
        "crop_method": "center_crop",
        "description": "Square feed post image"
    },
    "story": {
        "name": "Facebook Story",
        "dimensions": (1080, 1920),
        "width": 1080,
        "height": 1920,
        "aspect_ratio": "9:16",
        "min_width": 500,
        "format": "jpg",
        "quality": 95,
        "crop_method": "letterbox",
        "description": "Full-screen story image"
    },
    "video_feed": {
        "name": "Facebook Video Feed",
        "dimensions": (1280, 720),
        "width": 1280,
        "height": 720,
        "aspect_ratio": "16:9",
        "frame_rate": 30,
        "max_duration": 600,
        "format": "mp4",
        "codec": "h264",
        "bitrate": "5000k",
        "crop_method": "center_crop",
        "description": "Landscape video for feed (max 10 minutes)"
    },
    "video_vertical": {
        "name": "Facebook Video (Vertical)",
        "dimensions": (1080, 1920),
        "width": 1080,
        "height": 1920,
        "aspect_ratio": "9:16",
        "frame_rate": 30,
        "max_duration": 600,
        "format": "mp4",
        "codec": "h264",
        "bitrate": "5000k",
        "crop_method": "letterbox",
        "description": "Vertical video for feed or story (max 10 minutes)"
    },
    "collection_image": {
        "name": "Facebook Collection Cover",
        "dimensions": (1200, 628),
        "width": 1200,
        "height": 628,
        "aspect_ratio": "1.91:1",
        "format": "jpg",
        "quality": 95,
        "crop_method": "center_crop",
        "description": "Cover image for collections"
    }
}

# Combined format mapping
ALL_FORMATS = {
    "instagram": INSTAGRAM_FORMATS,
    "facebook": FACEBOOK_FORMATS
}

# Quick reference for common exports
RECOMMENDED_EXPORTS = {
    "instagram": [
        "feed_post_image",      # 4:5 vertical
        "story",                # 9:16 full story
        "reel"                  # 9:16 video reel
    ],
    "facebook": [
        "feed_image",           # 1.91:1 landscape
        "feed_image_vertical",  # 4:5 vertical
        "video_feed"            # 16:9 landscape video
    ]
}

def get_format_specs(platform, format_type):
    """
    Get specifications for a specific platform and format
    
    Args:
        platform (str): 'instagram' or 'facebook'
        format_type (str): specific format type (e.g., 'feed_post_image')
    
    Returns:
        dict: Format specifications or None if not found
    """
    if platform not in ALL_FORMATS:
        return None
    
    return ALL_FORMATS[platform].get(format_type)


def get_all_platform_formats(platform):
    """
    Get all available formats for a platform
    
    Args:
        platform (str): 'instagram' or 'facebook'
    
    Returns:
        dict: All format specifications for the platform
    """
    return ALL_FORMATS.get(platform, {})
