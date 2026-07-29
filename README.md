[logo](assets/logo_icon.png)

# Offline Wiki — Native Android App

> A fully native Android encyclopedia application with Material Design 3, dark mode, offline reading, folder organization, batch Wikipedia downloads, and SD card storage support.

## Features

- **Offline Wikipedia Articles** — Download and read articles without internet
- **Markdown Rendering** — Rich dark-mode Markdown display with syntax highlighting
- **Folder Organization** — Create folders and organize content hierarchically
- **Batch Download Queue** — Background processing with retry logic (max depth 2 for linked articles)
- **Local Search** — Full-text search across saved articles
- **Image Caching** — Download and cache images with internal/SD card storage selection
- **Seamless Navigation** — Click links inside articles to navigate saved content
- **Material 3 Design** — Clean, responsive UI optimized for OLED screens
- **Responsive Performance** — Virtualized lists handle 10,000+ items smoothly

## Tech Stack

- Kotlin + Jetpack Compose
- Room Database (SQLite)
- Material Design 3
- OkHttp for network requests
- Coroutines + Flow for reactive UI

## Project Structure

```
app/src/main/java/com/offlinewiki/
├── WikiApplication.kt
├── MainActivity.kt
├── data/            # Room entities, DAOs, Repository
├── ui/              # Compose screens and components
└── service/         # DownloadBatchService
```

## Setup

1. Open `app/` in Android Studio
2. Sync Gradle
3. Build and run on Android 8.0+ (API 26+)

## Storage Settings

The app supports saving articles and images to either internal storage or an external SD card (via Storage Access Framework). Configure in **Settings** (`/settings`).

## License

MIT — see `LICENSE`
