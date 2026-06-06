# Setup Guide - Retail Ad Creator

## Prerequisites

- Python 3.8 or higher
- Node.js/npm (optional, for frontend)
- Android Studio (for mobile app development)
- Git
- Google Cloud Account

## Backend Setup

### 1. Clone Repository
```bash
git clone https://github.com/aamarghar111-boop/aamarghar.git
cd aamarghar
```

### 2. Create Virtual Environment
```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

### 3. Install Dependencies
```bash
cd backend
pip install -r requirements.txt
```

### 4. Google Drive API Setup

#### 4a. Create Google Cloud Project
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project
3. Enable Google Drive API
4. Create OAuth 2.0 credentials (Desktop application)
5. Download credentials as JSON

#### 4b. Configure Credentials
```bash
# Copy the credentials to backend
cp ~/Downloads/credentials.json ./credentials.json
```

### 5. Environment Configuration
```bash
cp .env.example .env
# Edit .env with your actual values
nano .env
```

### 6. Run Backend Server
```bash
python main.py
```

Server will be available at `http://localhost:5000`

## Android Setup

### 1. Open Android Project
```bash
cd android
open build.gradle  # or use Android Studio
```

### 2. Build APK
```bash
./gradlew build
```

### 3. Run on Emulator/Device
```bash
./gradlew installDebug
```

## Testing

### Backend Tests
```bash
cd backend
pytest
```

### API Testing
```bash
curl http://localhost:5000/api/health
```

## Troubleshooting

### Issue: Google API Authentication Failed
**Solution:**
- Verify API keys in `.env` file
- Check Google Cloud project has Drive API enabled
- Ensure credentials.json is in correct location

### Issue: FFmpeg not found
**Solution:**
```bash
# Mac
brew install ffmpeg

# Ubuntu/Debian
sudo apt-get install ffmpeg

# Windows (using chocolatey)
choco install ffmpeg
```

### Issue: Python package conflicts
**Solution:**
```bash
pip install --upgrade pip
pip cache purge
pip install -r requirements.txt
```

## Next Steps

1. Run backend health check: `curl http://localhost:5000/api/health`
2. Test Google Drive connection
3. Start developing media processing pipeline
4. Build Android UI components

---

For detailed API documentation, see [API.md](./API.md)