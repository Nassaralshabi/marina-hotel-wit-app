# CI Workflows — Flutter Android APKs

This repository ships two GitHub Actions workflows for building the Flutter Android app under `mobile/`.

Badges:

- Debug: [![Android Debug APK](https://github.com/Nassaralshabi/marina-hotel-wit-app/actions/workflows/android-debug.yml/badge.svg?branch=main)](https://github.com/Nassaralshabi/marina-hotel-wit-app/actions/workflows/android-debug.yml)
- Release: [![Android Release APK](https://github.com/Nassaralshabi/marina-hotel-wit-app/actions/workflows/android.yml/badge.svg)](https://github.com/Nassaralshabi/marina-hotel-wit-app/actions/workflows/android.yml)

## Workflows

1) android-debug.yml — Debug builds
- Triggers: push to `main`, `develop`, `feature/**`, `capy/**`; PRs into `main`/`develop`; manual dispatch.
- Uses Flutter 3.22.x in `mobile/`.
- Runs `flutter pub get` and codegen (`build_runner`).
- Builds `app-debug.apk` with optional `BASE_API_URL` passed via `--dart-define`.
- Uploads artifact named `marina-hotel-android-debug-<version>-<short-sha>.zip` containing the APK.

Manual run: Actions → Android Debug APK (Flutter) → Run workflow → (optional) set Base API URL.

2) android.yml — Release builds
- Triggers: push tags `v*` (preferred) or manual dispatch.
- Requires signing secrets to produce a signed APK.
- Builds `app-release.apk` and uploads artifact. On tags, attaches APK to the GitHub Release automatically.

Manual run: Actions → Android Release APK (Flutter) → Run workflow → (optional) set Base API URL.

## Required repository secrets (Release builds)

- KEYSTORE_BASE64 — Base64 of the keystore file (e.g. `upload-keystore.jks`).
- KEYSTORE_PASSWORD — Password for the keystore.
- KEY_ALIAS — Alias of the key inside the keystore.
- KEY_PASSWORD — Password for the alias.
- BASE_API_URL — Optional; will be passed to the app via `--dart-define` as `BASE_API_URL`.

To generate KEYSTORE_BASE64:

```bash
# Create (or reuse) a release keystore, then encode:
base64 -w 0 upload-keystore.jks > keystore.b64
# Copy the contents of keystore.b64 into the KEYSTORE_BASE64 secret
```

## Conventions & optimizations

- Workflows always build inside `mobile/`.
- Aggressive caching is configured for Pub and Gradle to speed up builds.
- Clear artifact names include app version from `pubspec.yaml` and short commit SHA.
- Concurrency is enabled to auto-cancel redundant runs on the same ref.

## Troubleshooting

- Keystore errors: ensure all four signing secrets are present and correct. The workflow skips signing if secrets are missing, resulting in a debug-signed release.
- Codegen failures: re-run after fixing model annotations; the workflow runs `build_runner` with `--delete-conflicting-outputs`.
- API URL: if neither input nor secret is provided, the app uses the default in `lib/utils/env.dart`.
