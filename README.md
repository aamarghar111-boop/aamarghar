# Retail Ad Creator - Auto Advertisement Generator

An AI-powered application that gathers photos and videos from Google Drive retail shops and automatically creates advertisement photos and reels.

## 🎯 Project Overview

This application automates the creation of professional advertising content by:
- Fetching media from Google Drive
- Processing images and videos
- Generating AI-enhanced promotional content
- Creating reels for social media platforms

## 📚 Tech Stack

### Backend
- **Python** - AI/ML pipeline and core processing
- **Google Drive API** - Media retrieval
- **FFmpeg** - Video processing
- **OpenCV/PIL** - Image processing
- **TensorFlow/PyTorch** - AI enhancements

### Frontend/Mobile
- **Kotlin** - Android native app
- **Jetpack Compose** - Modern UI

### AI Services
- Google Gemini API - Content generation
- Custom ML models - Image/video enhancement

## 📁 Project Structure

```
aamarghar/
├── android/                 # Kotlin Android app
│   ├── app/
│   ├── build.gradle
│   └── settings.gradle
├── backend/                 # Python backend
│   ├── src/
│   ├── requirements.txt
│   └── main.py
├── docs/                    # Documentation
├── README.md
└── .gitignore
```

## 🚀 Development Phases

### Phase 1: Setup & Architecture
- Project initialization
- Google Drive API setup
- Database schema design
- Authentication system

### Phase 2: Media Processing
- Image enhancement pipeline
- Video processing (cut, trim, compile)
- Metadata extraction

### Phase 3: AI-Powered Ad Creation
- Template system
- Automated caption generation
- Reel compilation with effects

### Phase 4: Output & Distribution
- Multi-format export
- Social media optimization
- Direct platform uploads

## 🔧 Getting Started

### Prerequisites
- Python 3.8+
- Android Studio
- Google Cloud Account
- Google Drive API credentials

### Installation

1. Clone the repository
```bash
git clone https://github.com/aamarghar111-boop/aamarghar.git
cd aamarghar
```

2. Backend setup
```bash
cd backend
pip install -r requirements.txt
```

3. Android setup
```bash
cd android
./gradlew build
```

## 📖 Documentation

- [API Documentation](./docs/API.md)
- [Setup Guide](./docs/SETUP.md)
- [Architecture](./docs/ARCHITECTURE.md)

## 🤝 Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) for guidelines.

## 📝 License

This project is open source under the MIT License.

---

**Status:** 🚧 In Development