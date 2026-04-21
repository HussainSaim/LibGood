# Lib Good — 5-Minute Demo Recording Script

This is the shot-by-shot script for the assessment demo video. The demo is worth a significant portion of the artefact mark, so the priority is showing **the system actually working end-to-end**, not narrating every feature.

**Total target time:** 4:30–5:00
**Tools you'll need:**
- Android emulator (or device + screen mirroring)
- Screen recorder (OBS, the built-in macOS recorder, or Android Studio's Logcat → Screen Record)
- A microphone

**Before you start recording:**
- Have Firestore open in a browser tab so you can switch to it briefly
- Pre-create one test account to use for "logging in" so you don't waste time typing
- Have a second clean account ready to demo registration
- Close any notifications

---

## 0:00–0:20 — Intro

> "Hi, I'm Saim. This is Lib Good, my final-year project — a gamified reading tracker for Android, built with Kotlin and Firebase. The idea is to use the same kind of XP and streak mechanics that fitness and language apps use to help people build a daily reading habit."

[Shot: app icon on the emulator home screen, then tap to launch]

## 0:20–0:50 — Authentication

> "When the app launches, you hit the login screen. I'll register a fresh account to show the flow."

[Tap **Sign up** → type username `demo`, email `demo@libgood.app`, password `demo123` → tap **Create Account**]

> "That hit Firebase Authentication, created a user record in Firestore, and logged me straight into the dashboard."

## 0:50–1:30 — Dashboard tour

> "This is the dashboard. Right now I'm Level 1, 0 XP, and my streak is at zero because I haven't logged anything. The progress bar fills as I earn XP toward the next level. Every 100 XP is one level."

[Briefly hover the cursor over the level, XP, and streak readouts]

> "There are three main actions: log a reading session, add a book, and view achievements."

## 1:30–2:15 — Add a book

> "I need a book to read first. I'll add one manually."

[Tap **Add a Book** → enter `The Hobbit`, `J.R.R. Tolkien`, `310` → tap **Save Book**]

> "That writes a new document into the `userBooks` collection in Firestore, tagged with my user ID. I'll show that at the end."

## 2:15–3:30 — The core loop (the most important part)

> "Now the core feature — logging a reading session. This is the loop the entire app is built around."

[Tap **Log Reading Session** → select The Hobbit → type `50` pages → tap **Log Session**]

> "When I submit, a single function in FirebaseManager runs the full cascade: it saves the session, advances the book's current page, awards XP — 2 per page, so 100 XP here — updates my streak, and checks every achievement to see if I've unlocked any."

[The confirmation dialog appears]

> "And here's the result: 50 pages, 100 XP earned, I'm now Level 2, on a 1-day streak, and I just unlocked the **First Steps** achievement and the **Centurion** achievement for hitting 100 pages."

[Tap OK to dismiss]

> "Back on the dashboard, you can see everything has updated. Level 2, the XP bar reset for the new level, and the streak counter is live."

## 3:30–4:00 — Achievements screen

> "Let's check the achievements screen."

[Tap **Achievements**]

> "All seven achievements are listed. The two I just earned have green ticks; the locked ones still show the padlock with the unlock condition."

[Brief pause to let the screen be visible]

## 4:00–4:30 — Show Firestore (the proof)

> "And just to prove this is actually persisting to a real backend — here's the Firestore console for this project."

[Switch to browser tab → click into the `users` collection, then `userBooks`, then `readingSessions`, then `achievements`]

> "Four collections, one document for the user with the updated XP and streak, one for the book with currentPage now at 50, the reading session record, and two achievement records. This all happened in the last 30 seconds."

## 4:30–5:00 — Wrap

> "So the end-to-end loop works: register, add a book, log a session, get rewarded, persist to Firestore. The known limitations and the next steps are written up in the README. Thanks for watching."

[Stop recording]

---

## Editing checklist

- [ ] Trim dead air from the start and end
- [ ] Make sure the dialog with XP and achievements is clearly visible (pause on it for 2+ seconds)
- [ ] Keep audio levels consistent
- [ ] Export as MP4, 1080p if possible
- [ ] Final length: 4:30–5:00

## Backup plan

If something fails on the day of recording:

- **Login fails:** make sure your device has internet and `google-services.json` is in `app/`
- **The session dialog doesn't show achievements:** check the existing user already has them — use a fresh account
- **Firestore browser tab doesn't refresh:** click the collection name to force a reload

If everything else fails, fall back to recording the smoke test path from `SETUP.md` — that proves the same loop works.
