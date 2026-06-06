# Architecture - Retail Ad Creator

## System Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Android App (Kotlin)                     │
│                  - UI/UX for user interaction               │
│                  - Local media management                   │
└──────────────────────┬──────────────────────────────────────┘
                       │ REST API
┌──────────────────────┴──────────────────────────────────────┐
│              Backend API (Python Flask)                     │
│  ┌────────────────────────────────────────────────────┐    │
│  │ Routes:                                            │    │
│  │ - /api/drive/authorize  (OAuth)                   │    │
│  │ - /api/media/fetch      (Google Drive)            │    │
│  │ - /api/ad/generate      (Ad creation)             │    │
│  │ - /api/reel/create      (Reel generation)         │    │
│  └────────────────────────────────────────────────────┘    │
└──────────────┬───────────────────────┬────────────────┬─────┘
               │                       │                │
               ▼                       ▼                ▼
        ┌─────────────┐        ┌─────────────┐   ┌──────────┐
        │ Google API  │        │ Processing  │   │ Database │
        │  - Drive    │        │  - OpenCV   │   │ - Schema │
        │  - Gemini   │        │  - FFmpeg   │   │ - Store  │
        └─────────────┘        │  - PIL      │   └──────────┘
                               │  - ML Model │
                               └─────────────┘
```

## Component Details

### 1. Android Application (Kotlin)
- **Purpose:** User-facing mobile interface
- **Key Components:**
  - Authentication screen
  - Drive folder browser
  - Media preview gallery
  - Ad template selector
  - Download/share manager

### 2. Backend API (Flask)
- **Purpose:** Core processing and business logic
- **Modules:**
  - `google_drive.py` - Google Drive integration
  - `media_processor.py` - Image/video processing
  - `ad_generator.py` - Ad creation engine
  - `reel_creator.py` - Social media reel builder
  - `auth.py` - Authentication handler

### 3. Processing Pipeline

#### Image Processing
```
Raw Image (JPG/PNG)
    ↓
Enhancement (brightness, contrast, saturation)
    ↓
Resize (multiple formats)
    ↓
Add watermark/branding
    ↓
Export (multiple resolutions)
```

#### Video Processing
```
Raw Video (MP4/MOV)
    ↓
Transcode to standard format
    ↓
Extract key frames
    ↓
Apply effects/transitions
    ↓
Add audio/music
    ↓
Export (mobile-optimized)
```

### 4. Data Flow

#### Media Fetch Flow
```
User Request
    ↓
OAuth Authorization
    ↓
Query Google Drive API
    ↓
Download media files
    ↓
Store metadata in DB
    ↓
Return to frontend
```

#### Ad Generation Flow
```
Select Template + Media
    ↓
Process media (resize, enhance)
    ↓
Generate captions (AI)
    ↓
Compile final ad
    ↓
Export to formats
    ↓
Return file link
```

## Database Schema

### Media Table
```sql
- id (PK)
- google_drive_id
- filename
- file_type (image/video)
- url
- processed_status
- created_at
- updated_at
```

### Ad Template Table
```sql
- id (PK)
- name
- layout_config (JSON)
- ai_prompt
- created_at
```

### Generated Ad Table
```sql
- id (PK)
- media_id (FK)
- template_id (FK)
- output_file_path
- format (jpg/mp4/etc)
- status
- created_at
```

## Technology Stack Rationale

| Component | Technology | Why |
|-----------|-----------|-----|
| Mobile | Kotlin | Native Android performance |
| Backend | Python | Best for AI/ML and media processing |
| API | Flask | Lightweight, easy to scale |
| Processing | OpenCV, FFmpeg | Industry-standard, battle-tested |
| AI | TensorFlow, Gemini | State-of-the-art models |
| Database | PostgreSQL | Reliable, JSONB support |

## Security Considerations

1. **OAuth 2.0** for Google Drive authentication
2. **Environment variables** for sensitive keys
3. **Rate limiting** on API endpoints
4. **File validation** before processing
5. **Secure temporary storage** for media files
6. **HTTPS** for all API communications

## Scalability Plan

- Containerize with Docker
- Use message queues (Celery) for async tasks
- CDN for media distribution
- Microservices for processing units
- Kubernetes orchestration (future)

---

See [SETUP.md](./SETUP.md) for implementation details.