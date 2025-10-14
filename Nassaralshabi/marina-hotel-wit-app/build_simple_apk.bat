@echo off
chcp 65001 > nul
echo ========================================
echo    بناء APK لمشروع فندق مارينا Kotlin
echo ========================================
echo.

echo التحقق من مشروع Kotlin...

REM التحقق من وجود ملفات Gradle
if not exist "gradlew.bat" (
    echo ❌ خطأ: ملف gradlew.bat غير موجود!
    echo تأكد من وجود مشروع Android صحيح
    pause
    exit /b 1
)

if not exist "build.gradle.kts" (
    echo ❌ خطأ: ملف build.gradle.kts غير موجود!
    echo تأكد من وجود مشروع Android صحيح
    pause
    exit /b 1
)

echo ✅ تم العثور على مشروع Kotlin صحيح

echo.
echo بدء بناء المشروع...
echo.

REM تنظيف البناء السابق
echo 🧹 تنظيف البناء السابق...
call gradlew.bat clean

REM بناء APK
echo 🔨 بناء APK...
call gradlew.bat assembleDebug

REM التحقق من نجاح البناء
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo ✅ تم بناء APK بنجاح!
    echo 📁 مكان الملف: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 📱 معلومات التطبيق:
    echo    - Package ID: com.marinahotel.kotlin
    echo    - Version: 1.1.0 ^(Code: 2^)
    echo    - Target SDK: 34
    echo.
) else (
    echo.
    echo ❌ فشل في بناء APK!
    echo تحقق من رسائل الخطأ أعلاه
    echo.
)

echo ========================================
echo 🏁 انتهى بناء المشروع
echo.
echo للاستخدام:
echo 1. انسخ ملف APK إلى جهاز Android
echo 2. فعّل "المصادر غير المعروفة" في إعدادات الجهاز
echo 3. قم بتثبيت التطبيق
echo.
echo ملاحظات إضافية:
echo - التطبيق مبني باستخدام Kotlin
echo - يدعم Android 7.0+ ^(API 24^)
echo - حجم APK محسّن للأداء
echo ========================================

pause