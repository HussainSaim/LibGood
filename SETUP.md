# Lib Good — Setup Guide

This walks you (or a marker) through getting Lib Good running from a fresh clone. Total time: about 25 minutes, most of which is the one-time Firebase setup.

## What you'll need

- **Android Studio** (Hedgehog 2023.1.1 or newer)
- A **Google account** (for Firebase)
- An **Android emulator** or a physical device with USB debugging enabled
- ~5 GB free disk space (Android SDK, Gradle caches)

---

## Step 1 — Open the project

1. Unzip the project (or `git clone` it).
2. Open Android Studio → **File → Open** → select the `LibGood` folder.
3. Wait for Gradle sync. It will fail with a `google-services.json` error — that's expected. Continue to Step 2.

## Step 2 — Create a Firebase project

1. Go to [console.firebase.google.com](https://console.firebase.google.com).
2. Click **Add project**, name it `LibGood` (or anything), and click through the steps. Disable Google Analytics — it's not needed.
3. Once the project is ready, click the **Android icon** to add an Android app.
4. **Android package name:** `com.libgood.app` (this must match exactly).
5. Nickname: `LibGood`. Skip the SHA-1 (not needed for email/password auth).
6. Click **Register app**.
7. **Download `google-services.json`** when prompted.
8. Copy that file into the `app/` folder of this project, so it sits at:

   ```
   LibGood/app/google-services.json
   ```

9. You can skip the rest of the Firebase wizard pages — the SDK is already wired into the Gradle files.

## Step 3 — Enable Authentication

1. In the Firebase console, open **Build → Authentication → Get started**.
2. Under **Sign-in method**, enable **Email/Password**. Save.

## Step 4 — Create the Firestore database

1. In the Firebase console, open **Build → Firestore Database → Create database**.
2. Choose **Start in test mode** (this allows reads and writes for 30 days — fine for assessment).
3. Pick the location closest to you (e.g. `eur3` for Europe). Click **Enable**.

> **Why test mode?** Test-mode rules let any authenticated request read and write. For a public production app you would lock this down with rules that require `request.auth.uid == resource.data.userId`. The artefact is a prototype, so test mode is acceptable and matches the assessment scope.

## Step 5 — Sync Gradle

1. Back in Android Studio, click **File → Sync Project with Gradle Files**.
2. The first sync will download Firebase libraries (~2 minutes on a decent connection).
3. When the build finishes successfully, the green hammer icon ("Build") will be enabled.

## Step 6 — Run the app

1. Start an emulator (**Device Manager → Create Device** if you don't have one — a Pixel 6, API 34 image works well).
2. Click the green **Run** ▶ button.
3. The app installs and launches the **Login** screen.

## Step 7 — Smoke test (the assessment-critical path)

This proves the end-to-end loop works:

1. Tap **Sign up** → enter any email, a 6+ char password, and a username → **Create Account**.
2. You land on the **Dashboard**. It should show Level 1, 0 XP, 0-day streak.
3. Tap **Add a Book** → enter `The Hobbit`, `J.R.R. Tolkien`, `310` pages → **Save Book**.
4. Tap **Log Reading Session** → select The Hobbit → enter `25` pages → **Log Session**.
5. A dialog confirms `Read 25 pages and earned 50 XP`. It should also list the **First Steps** achievement as unlocked.
6. Back on the dashboard: Level 1 should show `50/100 XP`, `1-day streak`.
7. Tap **Achievements** → you should see ✅ next to **First Steps**.

If all of that works, the artefact is fully operational.

---

## Troubleshooting

**`google-services.json` not found**
The file isn't in `app/`. Double-check the location — it must be `LibGood/app/google-services.json`, not the project root.

**`Default FirebaseApp is not initialized`**
Gradle didn't apply the google-services plugin. Re-sync Gradle (**File → Sync Project with Gradle Files**).

**`PERMISSION_DENIED: Missing or insufficient permissions` in Logcat**
Firestore is in production mode rather than test mode. Go to **Firestore → Rules** and confirm rules allow access. The default test-mode ruleset looks like:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2026, 5, 1);
    }
  }
}
```

You can replace the date with one a few weeks out.

**Gradle sync fails: `Could not find com.google.firebase:firebase-bom`**
Your machine can't reach Maven Central / Google's Maven repo. Check internet, then **File → Invalidate Caches → Invalidate and Restart**.

**App crashes on launch with `ClassNotFoundException: LoginActivity`**
The package name in `AndroidManifest.xml` doesn't match the actual code package. Make sure both are `com.libgood.app`.

**`minSdk` errors**
Open `app/build.gradle.kts` and confirm `minSdk = 24`. Lower SDKs aren't supported by current Firebase versions.
