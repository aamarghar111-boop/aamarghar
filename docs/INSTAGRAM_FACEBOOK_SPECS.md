# Instagram & Facebook Export Specifications

Complete guide for exporting ads and reels to Instagram and Facebook platforms with optimal dimensions and specifications.

## 📱 Instagram Format Specifications

### Feed Posts

#### Image (Vertical) - 4:5 Aspect Ratio
- **Dimensions:** 1080 x 1350 px
- **Aspect Ratio:** 4:5
- **Minimum Width:** 600 px
- **Format:** JPG
- **Quality:** 95%
- **Use Case:** Primary feed post format, best engagement

#### Image (Square) - 1:1 Aspect Ratio
- **Dimensions:** 1080 x 1080 px
- **Aspect Ratio:** 1:1
- **Minimum Width:** 600 px
- **Format:** JPG
- **Quality:** 95%
- **Use Case:** Carousel posts, classic feed look

#### Image (Landscape) - 1.91:1 Aspect Ratio
- **Dimensions:** 1080 x 566 px
- **Aspect Ratio:** 1.91:1
- **Minimum Width:** 600 px
- **Format:** JPG
- **Quality:** 95%
- **Use Case:** Wide format feed posts

### Stories

#### Story Image/Video - 9:16 Aspect Ratio
- **Dimensions:** 1080 x 1920 px
- **Aspect Ratio:** 9:16
- **Minimum Width:** 600 px
- **Format:** JPG (image) or MP4 (video)
- **Quality:** 95% (for images)
- **Duration:** 5 seconds (default)
- **Use Case:** Full-screen story content

### Reels

#### Reel Video - 9:16 Aspect Ratio
- **Dimensions:** 1080 x 1920 px
- **Aspect Ratio:** 9:16
- **Frame Rate:** 30 fps
- **Duration:** 15-90 seconds
- **Format:** MP4
- **Video Codec:** H.264
- **Audio Codec:** AAC
- **Bitrate:** 8000 kbps (video), 128 kbps (audio)
- **Use Case:** Short-form vertical videos, high engagement

### Carousel

#### Carousel Image
- **Dimensions:** 1080 x 1350 px
- **Aspect Ratio:** 4:5
- **Minimum Width:** 600 px
- **Format:** JPG
- **Quality:** 95%
- **Max Items:** 10 images per carousel
- **Use Case:** Multi-image posts with swipe interaction

---

## 👥 Facebook Format Specifications

### Feed Posts

#### Feed Image (Landscape) - 1.91:1 Aspect Ratio
- **Dimensions:** 1200 x 628 px
- **Aspect Ratio:** 1.91:1
- **Minimum Width:** 600 px
- **Format:** JPG
- **Quality:** 95%
- **Use Case:** Standard landscape feed posts

#### Feed Image (Vertical) - 4:5 Aspect Ratio
- **Dimensions:** 1080 x 1350 px
- **Aspect Ratio:** 4:5
- **Minimum Width:** 600 px
- **Format:** JPG
- **Quality:** 95%
- **Use Case:** Vertical feed posts for mobile

#### Feed Image (Square) - 1:1 Aspect Ratio
- **Dimensions:** 1200 x 1200 px
- **Aspect Ratio:** 1:1
- **Minimum Width:** 600 px
- **Format:** JPG
- **Quality:** 95%
- **Use Case:** Square feed posts

### Videos

#### Video (Landscape) - 16:9 Aspect Ratio
- **Dimensions:** 1280 x 720 px
- **Aspect Ratio:** 16:9
- **Frame Rate:** 30 fps
- **Maximum Duration:** 10 minutes
- **Format:** MP4
- **Video Codec:** H.264
- **Audio Codec:** AAC
- **Bitrate:** 5000 kbps (video), 128 kbps (audio)
- **Use Case:** Standard horizontal videos for feeds

#### Video (Vertical) - 9:16 Aspect Ratio
- **Dimensions:** 1080 x 1920 px
- **Aspect Ratio:** 9:16
- **Frame Rate:** 30 fps
- **Maximum Duration:** 10 minutes
- **Format:** MP4
- **Video Codec:** H.264
- **Audio Codec:** AAC
- **Bitrate:** 5000 kbps (video), 128 kbps (audio)
- **Use Case:** Vertical videos for feeds and stories

---

## 🎯 Recommended Export Combinations

### For Retail/E-commerce

**Instagram Priority:**
- Feed Post (4:5) - 1080x1350
- Story (9:16) - 1080x1920
- Reel (9:16) - 1080x1920

**Facebook Priority:**
- Feed Image (1.91:1) - 1200x628
- Feed Image Vertical (4:5) - 1080x1350
- Video Feed (16:9) - 1280x720
- Video Vertical (9:16) - 1080x1920

---

## 📊 Format Comparison

| Aspect | Instagram | Facebook |
|--------|-----------|----------|
| Primary Feed | 1080x1350 (4:5) | 1200x628 (1.91:1) |
| Vertical | 1080x1920 (9:16) | 1080x1920 (9:16) |
| Video Landscape | N/A | 1280x720 (16:9) |
| Reel/Short Video | 1080x1920 (9:16) | 1080x1920 (9:16) |
| Max Video Duration | 90 seconds | 10 minutes |

---

## 🔧 Export API Usage

### Export to Instagram
```python
from export_manager import ExportManager

manager = ExportManager()

# Export single image
result = manager.export_image_to_platform(
    "photo.jpg", 
    "instagram", 
    "feed_post_image"
)

# Export all Instagram formats
results = manager.export_to_all_instagram_formats("photo.jpg", "image")

# Export video reel
reel_result = manager.export_video_to_platform(
    "video.mp4",
    "instagram",
    "reel"
)
```

### Export to Facebook
```python
# Export all Facebook formats
results = manager.export_to_all_facebook_formats("photo.jpg", "image")

# Export video for feed
video_result = manager.export_video_to_platform(
    "video.mp4",
    "facebook",
    "video_feed"
)
```

### Export to Both Platforms
```python
# Single call exports to all recommended formats
all_exports = manager.export_to_both_platforms("photo.jpg", "image")
```

---

## 📝 Implementation Status

- ✅ Format specifications defined
- ✅ ExportManager class created
- ✅ Image resizing with aspect ratio
- ✅ FFmpeg video processing
- ⏳ API endpoints (in progress)
- ⏳ Batch export
- ⏳ Progress tracking
- ⏳ CDN integration

---

For API endpoints, see main.py
