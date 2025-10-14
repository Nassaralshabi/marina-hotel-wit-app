# Changelog

## 2025-10-13

- Android (API 29+): Disable forced dark mode by adding values-v29/themes.xml where Theme.MarinaHotel inherits from Base.Theme.MarinaHotel and sets `android:forceDarkAllowed` to `false`.
- Themes: Introduce `Base.Theme.MarinaHotel` in values/themes.xml and make `Theme.MarinaHotel` extend it to avoid duplication across API-qualified resources.
- Lint (AppCompat):
  - Menus: replace `android:showAsAction` with `app:showAsAction` and ensure `xmlns:app` is declared.
  - Layouts: replace `android:tint` with `app:tint` and ensure `xmlns:app` is declared where needed.
