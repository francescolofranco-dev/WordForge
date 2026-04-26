# WordForge

A small Android app for learning vocabulary with spaced repetition. Add words you want to remember; they come back for review at growing intervals — short at first, longer as they stick.

## Features

- 9-tier spaced-repetition curve (1 hour → 30 days), with ±10% jitter so reviews don't pile up at the same minute
- Hidden meaning by default — tap to reveal during review or in the detail screen
- Local notifications when a word is due
- Built-in tier reference (help icon → "How it works")
- Material 3 design, light/dark, edge-to-edge

## Build

Open the project in Android Studio (it bundles a compatible JDK and Android SDK) and let it sync.

From the command line:

```bash
./gradlew :app:assembleDebug   # APK lands in app/build/outputs/apk/debug/
./gradlew :app:installDebug    # install on a connected device or running emulator
```

Requires JDK 21 and Android SDK 36 (both bundled with Android Studio).

## Installing on your phone without losing existing words

Android refuses to update an APK signed with a different key from the one already installed, and uninstalling wipes the database. So keeping your words depends on signing the new APK with the **same keystore** as the one on the phone. Pick the row that matches you:

| Situation | What to do |
|---|---|
| Same machine that produced the previous install | `./gradlew :app:installDebug` over USB. Your local `~/.android/debug.keystore` already matches — Android updates in place and the database is kept. |
| New machine | Copy the previous machine's `~/.android/debug.keystore` to this laptop's `~/.android/debug.keystore`, then `./gradlew :app:installDebug`. |
| Phone has a release build (Play Store / hand-signed APK) | Configure `signingConfigs.release` in `app/build.gradle.kts` with your release keystore, then `./gradlew :app:installRelease`. |
| No matching keystore at all | Data rescue is only possible if the installed app is a debug build — see below. Otherwise the words can't be recovered. |

### Data rescue (debug build, no matching keystore)

With USB debugging enabled on the phone:

```bash
# 1. Pull the database out of the old install (works because debug apps are run-as-able)
adb shell run-as com.wordforge tar -cf - databases > wordforge-backup.tar

# 2. Replace the app (signature mismatch forces an uninstall, which wipes data)
adb uninstall com.wordforge
./gradlew :app:installDebug

# 3. Push the database back in
adb push wordforge-backup.tar /data/local/tmp/
adb shell run-as com.wordforge tar -xf /data/local/tmp/wordforge-backup.tar
adb shell am force-stop com.wordforge
```

Open the app — your words and tier progress should be back.
