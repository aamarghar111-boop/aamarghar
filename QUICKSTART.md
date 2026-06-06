# Quick Start Guide - Running the Project Locally

## 🚀 Prerequisites

Before running the project, ensure you have installed:

- **Python 3.8+** ([download](https://www.python.org/downloads/))
- **Git** ([download](https://git-scm.com/))
- **FFmpeg** (for video processing)
  - **macOS:** `brew install ffmpeg`
  - **Ubuntu/Debian:** `sudo apt-get install ffmpeg`
  - **Windows:** `choco install ffmpeg` or download from [ffmpeg.org](https://ffmpeg.org/download.html)
- **Android Studio** (for mobile development, optional)

## 📦 Backend Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/aamarghar111-boop/aamarghar.git
cd aamarghar
git checkout develop
```

### Step 2: Create Virtual Environment

```bash
# Create virtual environment
python -m venv venv

# Activate virtual environment
# On macOS/Linux:
source venv/bin/activate

# On Windows:
venv\Scripts\activate
```

### Step 3: Install Dependencies

```bash
cd backend
pip install -r requirements.txt
```

**Expected output:**
```
Successfully installed Flask==3.0.0 Flask-CORS==4.0.0 Pillow==10.1.0 opencv-python==4.8.1.78 ...
```

### Step 4: Configure Environment Variables

```bash
# Copy environment template
cp .env.example .env

# Edit .env file with your values
nano .env  # macOS/Linux
# or
notepad .env  # Windows
```

**Update these fields in `.env`:**
```
FLASK_ENV=development
PORT=5000
GOOGLE_CLIENT_ID=your_client_id_here
GOOGLE_CLIENT_SECRET=your_client_secret_here
GEMINI_API_KEY=your_api_key_here
```

### Step 5: Run Backend Server

```bash
# From the backend directory
python main.py
```

**Expected output:**
```
 * Serving Flask app 'main'
 * Debug mode: on
 * Running on http://127.0.0.1:5000
 * Press CTRL+C to quit
```

✅ Backend is now running on `http://localhost:5000`

## ✅ Test Backend Health

Open a new terminal and test the API:

```bash
# Test health endpoint
curl http://localhost:5000/api/health
```

**Expected response:**
```json
{
  "status": "healthy",
  "service": "Retail Ad Creator Backend",
  "version": "0.1.0"
}
```

## 🎨 Mobile App Setup (Optional)

### Step 1: Open Android Project

```bash
cd android
```

### Step 2: Open in Android Studio

```bash
# macOS
open build.gradle

# Or manually open Android Studio and select the android/ folder
```

### Step 3: Build APK

```bash
./gradlew build
```

### Step 4: Run on Emulator/Device

```bash
./gradlew installDebug
```

## 🧪 Testing

### Run Backend Tests

```bash
cd backend
pytest
```

## 📋 Project Structure Overview

```
aamarghar/
├── backend/                    # Python Flask backend
│   ├── main.py                # Main Flask app
│   ├── src/
│   │   ├── export_manager.py   # Instagram/Facebook export
│   │   ├── media_processor.py  # Image/video processing (TODO)
│   │   ├── ad_generator.py     # Ad creation (TODO)
│   │   └── google_drive.py     # Google Drive integration (TODO)
│   ├── config/
│   │   └── export_formats.py   # Platform format specs
│   ├── requirements.txt        # Python dependencies
│   ├── .env.example            # Environment template
│   └── main.py
├── android/                    # Kotlin Android app
│   ├── app/
│   ├── build.gradle
│   └── settings.gradle
├── docs/                       # Documentation
│   ├── SETUP.md               # Setup guide
│   ├── ARCHITECTURE.md        # System architecture
│   ├── INSTAGRAM_FACEBOOK_SPECS.md  # Export specs
│   └── API.md                 # API documentation (TODO)
├── README.md
└── .gitignore
```

## 🔗 API Endpoints (Available)

### Health Check
```bash
GET http://localhost:5000/api/health
```

### Get Export Formats
```bash
GET http://localhost:5000/api/export/formats?platform=all
```

**Response:**
```json
{
  "instagram": {
    "formats": ["feed_post_image", "story", "reel", ...],
    "recommended": ["feed_post_image", "story", "reel"]
  },
  "facebook": {
    "formats": ["feed_image", "feed_image_vertical", ...],
    "recommended": ["feed_image", "video_feed"]
  }
}
```

## 🐛 Troubleshooting

### Issue: `ModuleNotFoundError: No module named 'flask'`
**Solution:**
```bash
pip install -r requirements.txt
```

### Issue: `FFmpeg not found`
**Solution:**
```bash
# macOS
brew install ffmpeg

# Ubuntu
sudo apt-get install ffmpeg

# Windows
choco install ffmpeg
```

### Issue: `Permission denied` on Linux/Mac
**Solution:**
```bash
chmod +x venv/bin/activate
source venv/bin/activate
```

### Issue: Port 5000 already in use
**Solution:**
```bash
# Change PORT in .env file
PORT=5001

# Or kill the process using port 5000
# macOS/Linux:
lsof -ti:5000 | xargs kill -9

# Windows:
netstat -ano | findstr :5000
taskkill /PID <PID> /F
```

## 📱 Testing the Export Manager

Once the backend is running, test the export functionality:

```python
# From backend directory, in Python shell
from src.export_manager import ExportManager

manager = ExportManager()

# Test with a sample image
result = manager.export_image_to_platform(
    "test_image.jpg",
    "instagram",
    "feed_post_image"
)

print(result)
```

## 🚀 Next Steps

1. ✅ Backend running on localhost:5000
2. 📝 Implement Google Drive integration (#8)
3. 🖼️ Complete image enhancement pipeline (#7)
4. 🎥 Add video processing (#6)
5. 🤖 Integrate AI captions (#5)
6. 🎬 Build reel creator (#4)
7. 📱 Develop mobile UI (#3)
8. 🔌 Complete API endpoints (#2)
9. ✅ Add tests (#1)

## 📞 Support

For issues or questions:
- Check [docs/SETUP.md](./docs/SETUP.md)
- Review [docs/ARCHITECTURE.md](./docs/ARCHITECTURE.md)
- Check GitHub issues for solutions

---

**Happy coding! 🎉**
