# Lib Good — User Guide

## What the software does

Lib Good is an Android app that turns reading into a game. You log books, log the pages you read, and earn XP, levels, streaks, and achievements as you go. The goal is to build a daily reading habit by giving you the same kind of feedback that fitness and language apps use.

## Core features

- Email/password account creation and login
- Add books manually (title, author, total pages)
- Log reading sessions per book (pages read)
- Automatic XP, level, streak, and achievement updates after each session
- Dashboard showing current level, XP progress to next level, and current streak
- Achievements screen showing all 7 milestones, with unlocked ones marked

## How to install and run

See [`SETUP.md`](SETUP.md). In short:

1. Open the project in Android Studio.
2. Create a Firebase project, drop `google-services.json` into `app/`.
3. Enable Email/Password auth and Firestore (test mode).
4. Run on an emulator or device.

## Test credentials

There are no pre-seeded accounts because Firebase Authentication is unique per project. **Create your own account on first launch** — sign-up takes about 10 seconds. Suggested test account:

- Email: `tester@libgood.app`
- Password: `test123`

## Walkthrough

### 1. Sign up
On the launch screen, tap **Don't have an account? Sign up**. Enter a username, email, and a 6+ character password. You'll land on the dashboard automatically.

### 2. Add your first book
On the dashboard, tap **Add a Book**. Enter:

- Title — anything, e.g. *The Hobbit*
- Author — e.g. *J.R.R. Tolkien*
- Total pages — e.g. *310*

Tap **Save Book**. You'll return to the dashboard.

### 3. Log a reading session
Tap **Log Reading Session**. Pick your book from the dropdown, enter how many pages you read (e.g. *25*), and tap **Log Session**.

A dialog will confirm:
- Pages read and XP earned (`pages × 2`)
- Your new level and total XP
- Your current streak
- Any newly-unlocked achievements

### 4. Watch your stats update
Back on the dashboard, your level, XP bar, and streak now reflect the session. Log more sessions to climb levels and unlock more achievements.

### 5. Browse achievements
Tap **Achievements** to see all 7 milestones. ✅ marks ones you've unlocked, 🔒 marks ones still locked. They're:

| Achievement | Unlocked when |
|---|---|
| First Steps | You log your first reading session |
| Centurion | You read 100 pages total |
| Page Turner | You read 500 pages total |
| On a Roll | You hit a 3-day streak |
| Bookworm | You hit a 7-day streak |
| Rising Reader | You reach level 5 |
| The End | You finish a book (currentPage ≥ totalPages) |

### 6. Log out
Tap **Log Out** at the bottom of the dashboard. You'll return to the login screen. Your data persists in Firestore — log back in any time.

## How XP, levels, and streaks work

- **XP per page:** 2
- **Level:** `floor(XP / 100) + 1` — every 100 XP is one level
- **Streaks:**
  - First ever session → streak = 1
  - Same-day session → streak unchanged
  - Next-day session → streak + 1
  - Skip a day → streak resets to 1

## Known limitations

- No Google Books API integration — books are entered manually
- No friends or leaderboard
- No notifications/reminders
- No charts or detailed stats
- Streak uses local device time, not server time, so changing your device clock can affect it
- Requires an internet connection — there's no offline cache

## Where the data lives

Everything is stored in your Firebase project's Firestore database. You can inspect it live in the Firebase console under **Firestore Database → Data**, where you'll see four collections: `users`, `userBooks`, `readingSessions`, and `achievements`.
