# PhantomFiles Pro

**Har file. Har raaz. Har jagah.**

The world's most advanced Android file manager with Shizuku integration, disguised media detection, encrypted vault, AI assistant, and complete file operations.

## Features

### 1. Three Access Modes
- **Normal Mode** — MANAGE_EXTERNAL_STORAGE for full internal storage
- **Shizuku Mode** — Access /Android/data, /Android/obb, and all app private folders without root
- **Root Mode** — Auto-detected, full system partition access

### 2. Full File Browser
- Browse all storage: Internal, SD Card, /Android/data (Shizuku), /Android/obb (Shizuku)
- Grid/List toggle, Sort (name/size/date/type), Filter by type
- Breadcrumb navigation, Bookmarks, Recent files
- Color-coded file type icons, Hidden files toggle
- Long-press multi-select, Select All

### 3. Complete File Operations
- Copy, Cut/Move, Paste with progress bar
- Delete → Recycle Bin (soft delete), Permanent Delete
- Rename (inline), Share, Create folder/file
- Compress ZIP, Extract ZIP/RAR/TAR/GZ
- Properties (size, path, permissions, MD5 hash)
- Install APK, Set as wallpaper, Copy path
- Batch operations on 100+ files

### 4. Recycle Bin (Android's First Proper One)
- Deleted files go here first with original path saved
- Restore to original location
- Auto-empty after 30 days (configurable)
- Size limit configurable (default 500MB)
- Size shown on dashboard, Search inside recycle bin

### 5. Disguised Media Finder (Unique Feature)
- Scan entire storage for hidden/disguised files
- Detect extension vs magic bytes mismatch
- Find .nomedia hidden content, double extensions, 0 KB dummy files
- Show fake extension vs real type with preview
- Deep scan via Shizuku for /Android/data

### 6. AI File Assistant
- **Offline**: Natural language commands
  - "Cache delete karo", "WhatsApp videos dhundho"
  - "1 GB se badi files dikha", "Duplicate photos hata do"
  - "Disguised files scan karo", "Storage report dikha"
- **Online** (optional): Groq API for complex commands

### 7. Deep Scanner
- Large files finder (100MB+, 500MB+, 1GB+)
- Duplicate finder with MD5 hash comparison
- Cache cleaner with app-by-app breakdown
- Junk files: temp, log, thumbnails, empty folders
- Old APK finder

### 8. Private Vault
- AES-256-CBC encryption with PBKDF2 key derivation
- PIN lock (4-6 digits)
- Import → auto encrypt, View → decrypt on fly
- Export → decrypt to storage

### 9. App Manager
- List all installed apps with size breakdown
- Sort by name, size, install date, last used
- Extract APK backup, View permissions

### 10. Storage Health Dashboard
- Animated circular storage chart
- Category breakdown: Photos, Videos, Audio, Docs, APKs
- Quick access folders, Recent files, Large files
- Recycle bin size indicator

### 11. Network Transfer
- WiFi FTP Server on port 2121
- Connect from PC browser: ftp://IP:2121
- Password protected, No cable needed

### 12. In-App File Viewers
- Images: pinch-zoom, swipe gallery
- Videos: full player controls
- Audio: player with async prepare
- Text/Code: monospace, syntax color
- APK: manifest, permissions, icon preview
- Share and Open With for all types

### 13. Scheduled Operations (WorkManager)
- Auto clean cache: daily/weekly
- Auto empty recycle bin: 30 days
- Scheduled scans

## Architecture

```
com.phantomfiles.pro/
├── PhantomApp.kt              (@HiltAndroidApp)
├── MainActivity.kt            (@AndroidEntryPoint)
├── di/
│   └── AppModule.kt           (Room, Retrofit, DAOs)
├── data/
│   ├── local/                 (Room DB, 6 DAOs)
│   ├── model/                 (FileItem, RecycleBinItem, VaultFile, etc.)
│   ├── remote/                (Groq API)
│   └── repository/            (File, RecycleBin, Vault, Scan, Settings, ShizukuRepo)
├── domain/usecase/            (AICommandUseCase)
├── presentation/
│   ├── navigation/            (5-tab bottom nav)
│   ├── home/                  (Dashboard + storage chart)
│   ├── files/                 (File browser + operations)
│   ├── scanner/               (Deep scan + radar animation)
│   ├── ai/                    (Chat interface)
│   ├── vault/                 (PIN lock + encrypted files)
│   ├── recycle/               (Recycle bin manager)
│   ├── appmanager/            (Installed apps)
│   ├── settings/              (All preferences)
│   ├── network/               (FTP server)
│   ├── viewer/                (Image/Video/Audio/Text/APK)
│   ├── permission/            (Storage access flow)
│   └── theme/                 (Cyberpunk dark theme)
├── shizuku/                   (AIDL + service)
├── util/                      (MagicBytes, AES, FTP, Hash, Format)
└── worker/                    (CacheClean, RecycleBin, FileOp service)
```

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Dagger Hilt 2.51.1 |
| Database | Room 2.6.1 |
| Preferences | DataStore 1.1.1 |
| Networking | Retrofit + OkHttp |
| Images | Coil 2.7.0 |
| Background | WorkManager 2.10.0 |
| Encryption | AES-256-CBC + PBKDF2 |
| Privileged Access | Shizuku API 13.1.5 |
| Min SDK | 30 (Android 11) |
| Target SDK | 36 (Android 16) |

## Theme

| Color | Hex | Usage |
|-------|-----|-------|
| Deep Black | #050505 | Background |
| Electric Cyan | #00E5FF | Primary, accents |
| Phantom Purple | #7C4DFF | Secondary, vault |
| Neon Green | #00E676 | Success, audio |
| Amber | #FFB300 | Warnings |
| Danger Red | #FF1744 | Delete, errors |

## Shizuku Setup Guide

PhantomFiles uses [Shizuku](https://shizuku.rikka.app/) to access /Android/data and /Android/obb without root.

### Steps:
1. **Install Shizuku** from [Google Play Store](https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api)
2. **Enable Developer Options**: Settings → About Phone → Tap Build Number 7 times
3. **Enable Wireless Debugging**: Settings → Developer Options → Wireless Debugging → ON
4. **Start Shizuku**: Open Shizuku app → Start via Wireless Debugging
5. **Grant Permission**: Return to PhantomFiles → Settings → Tap Shizuku to grant

### Why Shizuku?
- Access all app private folders (/Android/data/*)
- No root required
- Works on Android 11+ (API 30+)
- Uses ADB-level permissions safely

## Permissions

| Permission | Purpose |
|-----------|---------|
| MANAGE_EXTERNAL_STORAGE | Full file browsing |
| READ_MEDIA_* | Media access (API 33+) |
| USE_BIOMETRIC | Vault fingerprint lock |
| FOREGROUND_SERVICE | File copy/move progress |
| POST_NOTIFICATIONS | Scan results, alerts |
| INTERNET | Groq AI API (optional) |
| CAMERA | Intruder photo (vault) |
| QUERY_ALL_PACKAGES | App manager |
| REQUEST_INSTALL_PACKAGES | APK installer |

## Build

```bash
# Clone
git clone https://github.com/gecew79810-bit/PhantomFilesPro.git
cd PhantomFilesPro

# Build debug APK
./gradlew assembleDebug

# APK location
app/build/outputs/apk/debug/app-debug.apk
```

Requires: JDK 17, Gradle 8.9, Android SDK 36

## Database Schema

| Table | Columns |
|-------|---------|
| files_cache | path (PK), name, size, type, modified, hash, partition |
| recycle_bin | id, originalPath, recyclePath, fileName, deletedAt, fileSize, mimeType |
| vault_files | id, encryptedName, originalName, originalPath, fileSize, mimeType, addedAt |
| scan_results | id, scanType, foundCount, sizeBytes, scannedAt |
| bookmarks | id, path, name, iconColor |
| operations_log | id, operation, sourcePath, destPath, timestamp, status |

## License

Private — All rights reserved.
