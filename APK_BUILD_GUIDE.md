# 📱 دليل شامل لبناء APK - تطبيق فندق مارينا

## 🎯 الخيارات المتاحة

### 1. 🌐 الطريقة الأسرع: WebView APK (مُوصى بها)

#### المميزات:
- ✅ لا يحتاج Android Studio
- ✅ يعمل خلال دقائق
- ✅ يعرض موقعك بشكل مثالي
- ✅ حجم صغير (5-10 MB)
- ✅ سهل التحديث

#### الخطوات:
1. **افتح الملف:** `create_web_apk.html`
2. **اضغط "إنشاء APK الآن"**
3. **انتظر 2-3 دقائق**
4. **حمل APK واختبره**

---

### 2. ⚡ Progressive Web App (PWA)

#### المميزات:
- ✅ يعمل بدون إنترنت
- ✅ تحديثات تلقائية
- ✅ أداء ممتاز
- ✅ يمكن تثبيته من المتصفح

#### الخطوات:
```bash
# تشغيل الإعداد
powershell -File "setup_pwa.ps1"
```

---

### 3. 🌍 البناء عبر GitHub Actions (تطبيق Kotlin Marina الأصلي)

#### الملف المعتمد
- `.github/workflows/kotlin-marina-android.yml` يتابع كل التغييرات داخل `kotlin-marina/` ويشغّل بناء Android تلقائيًا.
- يقوم بتشغيل فحوص `lintDebug` و`testDebugUnitTest` قبل إنشاء ملفات APK.

#### أهم المدخلات
```yaml
workflow_dispatch:
  inputs:
    build-type:
      description: Select the build variant to assemble
      default: debug
      options:
        - debug
        - release
```

#### كيفية الاستخدام
1. ادفع أي تعديل داخل `kotlin-marina/` أو على ملف الـworkflow للحصول على بناء Debug تلقائي، وسيتم رفع الـAPK وتقارير الجودة في تبويب **Actions**.
2. للبناء اليدوي من التبويب **Actions → Kotlin Marina Android Build → Run workflow**:
   - اختر `release` من قائمة **Build type** إذا رغبت في نسخة موقعة.
   - أضف القيم إلى أسرار المستودع (Settings → Secrets → Actions) بالأسماء: `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
   - عند توفر كل الأسرار يتم إنشاء `keystore.jks` و`signing.properties` تلقائيًا وتشغيل `assembleRelease` لتوليد ملف APK موقّع.
3. إذا كانت الأسرار ناقصة فسيظهر تنبيه في السجل ويعود البناء إلى نسخة Debug بشكل تلقائي.

#### المخرجات
- ملف APK باسم يحتوي على اسم الفرع ونوع البناء (`debug` أو `release`) مع حفظ لمدة 7 أيام.
- تقارير lint واختبارات `testDebugUnitTest` مرفوعة كـArtifact مستقل لسهولة التحليل.

---

### 4. 🔧 الحل المحلي (إصلاح Gradle)

```batch
# تشغيل ملف الإصلاح
.\fix_gradle_build.bat
```

#### خطوات الإصلاح اليدوي:
1. احذف مجلد `C:\Users\NASSAR\.gradle`
2. احذف مجلد `.gradle` في المشروع
3. أعد تشغيل الكمبيوتر
4. شغل `.\build_apk_simple.bat`

---

## 🛠️ الأدوات المُنشأة

### ملفات البناء:
- `build_simple_apk.bat` - مشروع APK مبسط
- `build_apk_simple.bat` - بناء مع معالجة الأخطاء
- `manual_build.bat` - بناء يدوي مع تنظيف

### الواجهات:
- `create_web_apk.html` - واجهة إنشاء APK
- `quick_apk_builder.html` - أداة البناء السريع

### المشاريع:
- `simple_webview_apk/` - مشروع WebView مبسط
- `simple_apk_project/` - مشروع APK جاهز

---

## 🎯 التوصيات

### للمبتدئين:
👉 **استخدم WebView APK** - الأسرع والأسهل

### للمطورين:
👉 **استخدم GitHub Actions** - احترافي ومجاني

### للاستخدام الفوري:
👉 **استخدم PWA** - يعمل مباشرة

---

## 🔍 استكشاف الأخطاء

### مشكلة Gradle Timeout:
```batch
# حذف cache
rmdir /s /q "%USERPROFILE%\.gradle"
rmdir /s /q ".gradle"

# إعادة البناء
.\gradlew clean
.\gradlew assembleDebug --no-daemon
```

### مشكلة Android SDK:
```batch
# تحديد مسار SDK
set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
echo sdk.dir=%ANDROID_HOME:\=/% > local.properties
```

### مشكلة Java:
```batch
# التحقق من Java
java -version
javac -version

# إذا لم يعمل، حمل من:
# https://www.oracle.com/java/technologies/javase-downloads.html
```

---

## 📦 مواصفات APK المُنتج

### Debug APK:
- **الحجم:** 8-15 MB
- **الميزات:** جميع الميزات + أدوات التطوير
- **الأداء:** جيد للاختبار

### Release APK:
- **الحجم:** 5-10 MB
- **الميزات:** محسنة للإنتاج
- **الأداء:** أفضل أداء

---

## 🔐 إعدادات الأمان

### الشبكة:
- يدعم HTTP و HTTPS
- يسمح بالاتصال المحلي
- حماية من XSS

### الصلاحيات:
- الإنترنت
- التخزين
- الكاميرا (للرفع)
- الموقع (اختياري)

---

## 🚀 النشر والتوزيع

### للاختبار:
1. أرسل APK عبر WhatsApp/Email
2. فعل "مصادر غير معروفة" في الهاتف
3. ثبت وجرب

### للنشر الرسمي:
1. أنشئ Release APK موقع
2. ارفع على Google Play Store
3. أو وزع مباشرة للعملاء

---

## 📞 الدعم الفني

### المشاكل الشائعة:
- ❌ Gradle timeout → احذف .gradle وأعد المحاولة
- ❌ Java not found → ثبت JDK وحدد PATH
- ❌ SDK missing → ثبت Android Studio أو SDK tools

### نصائح الأداء:
- ✅ استخدم WiFi سريع للبناء الأول
- ✅ أغلق البرامج الأخرى أثناء البناء
- ✅ استخدم SSD إن أمكن

---

## 🎉 الخلاصة

**أسرع طريقة:** WebView APK (5 دقائق)
**أفضل جودة:** GitHub Actions (15 دقيقة)
**أسهل استخدام:** PWA (فوري)

اختر الطريقة التي تناسبك وابدأ! 🚀