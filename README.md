# Lib Good - Gamified Reading Tracker

Lib Good is a gamified reading tracker application that aims to promote consistent reading through motivational techniques such as XP, Levels, daily streaks and unlockable achievements. It is built for the Android operating system using Kotlin with a Firebase database. 


---

## What it does

- **Authentication** - Register and log in with email + password (Firebase Auth)
- **Book management** - Add books manually with their title, author, and total pages
- **Reading session logging** - The core function of the application where you select a book, enter pages read, submit
- **XP & levels** - Ever page read counts for 2 XP and every level is worth 100 XP
- **Streaks** - Track consecutive days of reading, resets if you skip a day
- **Achievements** - 7 milestones that automatically unlock as you read
- **Leaderboard** - A ranked list of all users sorted by XP, with medals for top 3 and your row highlighted
- **Dashboard** - Home page that details your information and allows for you to navigate to the seperate sections of the app

## Architecture

Three-tier:

```

Presentation Layer                      
7 Activities + XML layouts              
Login / Register / Dashboard /         
AddBook / LogReading / Achievements / Leaderboard 
            ↓
Application Logic Layer                 
FirebaseManager (singleton)             
auth, CRUD, XP, streak, achievements  
                ↓
Data Layer                              
Firebase Auth + Cloud Firestore        
```

`FirebaseManager` is the only class that talks to Firebase. Activities call typed methods on it and receive typed model callbacks, keeping UI code clean.

## Data model (Firestore)

| Collection | Fields |
|---|---|
| `users/{uid}` | email, username, xp, streak, lastReadDate, createdAt |
| `userBooks/{id}` | userId, title, author, totalPages, currentPage, addedAt |
| `readingSessions/{id}` | userId, bookId, bookTitle, pagesRead, xpEarned, date |
| `achievements/{id}` | userId, type, earnedAt |

## The core loop

When a user logs a reading session, `FirebaseManager.logReadingSession()` runs the full cascade:

1. Save the current session document
2. Advance the book's `currentPage` by number of pages read
3. Award XP (`pagesRead × 2`)
4. Update the streak based on `lastReadDate`
5. Check every achievement type and write any newly-unlocked ones
6. Return the updated user to the UI

This single end-to-end loop is the core feature of the application.

## Tech stack

- **Language:** Kotlin
- **UI:** Android XML layouts + Material Components
- **Backend:** Firebase Authentication + Cloud Firestore
- **Build:** Gradle (Kotlin DSL), AGP 8.2, JDK 17
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34

## Getting started

- **Email:** marker@libgood.app
- **Password:** Marker123

The pre-built APK is included in this submission at `app/build/outputs/apk/debug/app-debug.apk`. Install it on an Android emulator or phone. Alternatively create your own account using the register function.

## Project structure

```
LibGood/
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json          ← you add this from Firebase console
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/libgood/app/
│       │   ├── FirebaseManager.kt    ← all backend logic lives here
│       │   ├── LoginActivity.kt
│       │   ├── RegisterActivity.kt
│       │   ├── DashboardActivity.kt
│       │   ├── AddBookActivity.kt
│       │   ├── LogReadingActivity.kt
│       │   ├── AchievementsActivity.kt
│       │   ├── LeaderboardActivity.kt
│       │   └── models/
│       │       ├── User.kt
│       │       ├── Book.kt
│       │       ├── ReadingSession.kt
│       │       ├── Achievement.kt
│       │       └── AchievementType.kt
│       └── res/
│           ├── layout/               ← 7 layout files
│           ├── values/               ← colours, strings, themes
│           └── drawable/             ← card backgrounds
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
├── SETUP.md
├── USER_GUIDE.md
└── DEMO_SCRIPT.md
```

## Known limitations

The submitted artefact intentionally focuses on the core gamification loop rather than the full feature set described in the design document. The following are **not** implemented:

- Google Books API integration (manual book entry only)
- Accessibility options
- Advanced UI implementation
- Friends / social system
- Push notifications and reminders
- Charts or detailed statistics views
- Offline support - all reads/writes go to Firestore
- The streak logic uses local device time rather than a server timestamp

These were deprioritised in order to focus on the mandatory features of the application.

## Author

Saim Hussain
