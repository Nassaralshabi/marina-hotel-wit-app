# Marina Hotel Android Build Pipeline (GitHub Actions)

## Overview
This repository now includes a complete GitHub Actions workflow that automatically builds the Kotlin Android app (`android_app/`) into release APK and AAB (App Bundle) formats on every push to main or pull request, plus manual trigger support.

## Quick Start
1. Push a change to `android_app/` or manually trigger the workflow from the Actions tab.
2. Download built APK/AAB files from the workflow run's artifacts in the browser.

## Features
- **Dual Build**: APK + AAB automatically for both Release and Debug variants
- **Code Quality**: Integrated unit tests and lint checks
- **Caching**: Reuses Gradle cache to speed up subsequent builds
- **Signing**: Optional automatic APK/AAB signing using GitHub repository secrets
- **Artifacts**: Built files are retained for 14 days (release) or 7 days (debug)
- **Manual Trigger**: Run with custom version numbers from the Actions tab

## Files Added
- `.github/workflows/build-kotlin-marina.yml` – Production pipeline (APK + AAB + testing + signing)
- `.github/workflows/build-android.yml` – Lightweight APK-only workflow

## Secrets (Optional – for automatic signing)
In GitHub repository → Settings → Secrets and variables → Actions, add:

| Secret | Description | How to obtain |
|--------|-------------|---------------|
| `KEYSTORE_BASE64` | Base64-encoded contents of your `.jks/.keystore` that signs the APK/AAB | Base64 encode your keystore file |
| `KEY_ALIAS` | The alias used when generating that keystore | e.g., `key0` (whatever you typed) |
| `KEYSTORE_PASSWORD` | The keystore password | e.g., `android` (do not use `android` in prod!) |
| `KEY_PASSWORD` | The private key password (often identical) | 

Generate base64: `base64 -w 0 your-key.keystore`

## Usage
### Automated
Simply push a commit touching `android_app/` and the workflow runs automatically. On success you'll see:

- `.github/workflows/build-kotlin-marina.yml` executes
- APK/AAB artifacts uploaded (download from Actions tab)

### Manual Release
GitHub Actions → Choose workflow *Build & Release Marina Hotel (Kotlin Android)* → Run workflow. Supply optional:
- Version name (e.g., 1.0.0)
- Version code (integer)

### CI Checks for PRs
Each pull request updating the Kotlin app is tested, linted and built (APK) so reviewers can check that compilation passes before merging.

## Next Steps
- Customize app package name / signing keys for production releases
- Publish the AAB to Google Play using *r0adkll/upload-google-play* Action
- Optionally enable R8 minification by switching `minifyEnabled true`
- Add instrumented tests job (requires hardware or emulator service)