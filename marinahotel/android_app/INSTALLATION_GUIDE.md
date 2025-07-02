# 📖 دليل التثبيت الشامل - تطبيق فندق مارينا

## 📱 نظرة عامة
تم تطوير تطبيق Android لنظام إدارة فندق مارينا ليتيح الوصول السهل لجميع وظائف النظام من الهاتف المحمول.

**المميزات:**
- ✅ واجهة سهلة الاستخدام
- ✅ دعم كامل للغة العربية  
- ✅ يعمل مع الخادم المحلي (IP: 10.0.0.57)
- ✅ تحديث تلقائي للمحتوى
- ✅ دعم جميع أحجام الشاشات
- ✅ عمل بدون اتصال محدود

## 🛠️ الإعداد المطلوب

### 1. متطلبات النظام:
- **نظام التشغيل:** Windows 10/11
- **Java JDK:** الإصدار 8 أو أحدث
- **Android Studio:** أحدث إصدار
- **اتصال بالإنترنت:** لتحميل Dependencies

### 2. متطلبات الهاتف:
- **نظام Android:** 5.0 (API 21) أو أحدث
- **مساحة تخزين:** 50 MB على الأقل
- **ذاكرة RAM:** 1 GB على الأقل

## 📋 خطوات التثبيت التفصيلية

### المرحلة الأولى: إعداد بيئة التطوير

#### 1. تثبيت Java JDK
```bash
# تحميل من:
https://www.oracle.com/java/technologies/downloads/

# للتحقق من التثبيت:
java -version
javac -version
```

#### 2. تثبيت Android Studio
```bash
# تحميل من:
https://developer.android.com/studio

# خطوات التثبيت:
1. تشغيل ملف التثبيت
2. اتباع معالج التثبيت
3. تحميل Android SDK المطلوب
4. إعداد AVD (Android Virtual Device) - اختياري
```

### المرحلة الثانية: فتح وإعداد المشروع

#### 1. فتح المشروع في Android Studio
```
1. افتح Android Studio
2. اختر "Open an Existing Project"  
3. انتقل إلى مجلد "android_app"
4. اضغط "OK"
```

#### 2. انتظار تحميل المشروع
```
- سيقوم Android Studio بتحميل Dependencies تلقائياً
- قد يستغرق هذا 10-15 دقيقة في المرة الأولى
- تأكد من اتصال الإنترنت خلال هذه العملية
```

#### 3. حل مشاكل Sync المحتملة
```
إذا ظهرت أخطاء Gradle:
1. اضغط "Try Again"
2. أو اذهب إلى File → Sync Project with Gradle Files
3. أو اضغط على أيقونة "Sync" في شريط الأدوات
```

### المرحلة الثالثة: بناء التطبيق

#### 1. بناء APK للتطوير
```
الطريقة الأولى - عبر القائمة:
Build → Build Bundle(s) / APK(s) → Build APK(s)

الطريقة الثانية - عبر Terminal:
gradlew assembleDebug
```

#### 2. بناء APK للإنتاج (اختياري)
```
# إنشاء keystore أولاً:
keytool -genkey -v -keystore marina-release.keystore -alias marina -keyalg RSA -keysize 2048 -validity 10000

# ثم بناء APK:
Build → Generate Signed Bundle / APK
```

#### 3. العثور على ملف APK
```
مسار APK للتطوير:
app/build/outputs/apk/debug/app-debug.apk

مسار APK للإنتاج:
app/build/outputs/apk/release/app-release.apk
```

## 📱 تثبيت التطبيق على الهاتف

### طريقة 1: التثبيت المباشر (للاختبار)
```
1. وصل الهاتف بالكمبيوتر عبر USB
2. فعل "Developer Options" في الهاتف:
   - إعدادات → حول الهاتف → اضغط على "Build Number" 7 مرات
3. فعل "USB Debugging":
   - إعدادات → Developer Options → USB Debugging
4. في Android Studio اضغط "Run" (أيقونة المثلث الأخضر)
```

### طريقة 2: التثبيت اليدوي
```
1. انسخ ملف "app-debug.apk" إلى الهاتف
2. في إعدادات الهاتف:
   - إعدادات → الأمان → فعل "Install from Unknown Sources"
   - أو: إعدادات → Apps → Special Access → Install Unknown Apps
3. اضغط على ملف APK في الهاتف
4. اتبع تعليمات التثبيت
```

## ⚙️ تخصيص التطبيق

### تغيير اسم التطبيق
```xml
<!-- في app/src/main/res/values/strings.xml -->
<string name="app_name">اسم التطبيق الجديد</string>
```

### تغيير رابط الموقع
```java
// في app/src/main/java/com/marinahotel/app/MainActivity.java
// السطر 28:
private static final String WEBSITE_URL = "http://YOUR_NEW_IP/marina hotel/admin/";
```

### تغيير الألوان
```xml
<!-- في app/src/main/res/values/colors.xml -->
<color name="primary_color">#667eea</color>
<color name="primary_dark">#764ba2</color>
```

### تغيير الأيقونة
```
1. اضغط يمين على app/src/main/res/mipmap
2. اختر New → Image Asset
3. اختر Icon Type: Launcher Icons
4. اختر الصورة الجديدة
5. اضغط Finish
```

## 🔧 استكشاف الأخطاء وحلها

### خطأ: "SDK not found"
```
الحل:
1. في Android Studio: File → Project Structure
2. اختر "SDK Location" من القائمة اليسرى
3. تأكد من أن مسار Android SDK صحيح
4. إذا لم يكن محدد، اضغط "..." واختر مسار SDK
```

### خطأ: "Gradle sync failed"
```
الحل:
1. File → Invalidate Caches and Restart
2. أو في Terminal:
   gradlew clean
   gradlew build
```

### خطأ: "Build failed"
```
الحل:
1. تأكد من اتصال الإنترنت
2. Build → Clean Project
3. Build → Rebuild Project
4. تأكد من أن Java مثبت بشكل صحيح
```

### التطبيق لا يحمل الموقع
```
الحل:
1. تأكد من أن XAMPP يعمل
2. تأكد من أن الموقع يعمل على http://10.0.0.57
3. جرب فتح الرابط في متصفح الهاتف أولاً
4. تأكد من أن الهاتف متصل بنفس الشبكة
```

### مشكلة في الشبكة
```
إضافة هذا السطر في AndroidManifest.xml إذا لزم الأمر:
android:usesCleartextTraffic="true"
```

## 📊 معلومات فنية

### مواصفات التطبيق
```
Package Name: com.marinahotel.app
Version Code: 1
Version Name: 1.0
Min SDK Version: 21 (Android 5.0)
Target SDK Version: 34 (Android 14)
Compile SDK Version: 34
```

### المكتبات المستخدمة
```
- androidx.appcompat:appcompat:1.6.1
- com.google.android.material:material:1.10.0
- androidx.constraintlayout:constraintlayout:2.1.4
- androidx.swiperefreshlayout:swiperefreshlayout:1.1.0
```

### الصلاحيات المطلوبة
```
- INTERNET: للاتصال بالموقع
- ACCESS_NETWORK_STATE: للتحقق من حالة الشبكة
- ACCESS_WIFI_STATE: للتحقق من WiFi
- WRITE_EXTERNAL_STORAGE: لحفظ الملفات
- READ_EXTERNAL_STORAGE: لقراءة الملفات
- CAMERA: لالتقاط الصور
- CALL_PHONE: لإجراء المكالمات
```

## 🔐 ملاحظات الأمان

### للاستخدام الداخلي
- التطبيق مُعد للاستخدام مع IP محلي
- يدعم HTTP للشبكة المحلية
- مناسب للاستخدام داخل الفندق

### للاستخدام العام (إذا لزم الأمر)
- استخدم HTTPS بدلاً من HTTP
- وقع التطبيق بـ keystore آمن
- فعل ProGuard للحماية
- اختبر التطبيق جيداً قبل التوزيع

## 📞 الدعم والمساعدة

### الملفات المرجعية
```
- README.md: شرح عام
- QUICK_START.md: دليل سريع
- BUILD_INSTRUCTIONS.md: تعليمات البناء
- دليل_البناء_السريع.txt: دليل سريع بالعربية
```

### ملفات المساعدة
```
- build_simple.bat: بناء التطبيق تلقائياً
- test_app.bat: اختبار الاتصال بالخادم
```

### استكشاف المشاكل
1. راجع ملفات الـ log في Android Studio
2. تأكد من جميع المتطلبات
3. جرب إعادة تشغيل Android Studio
4. تأكد من تحديث جميع المكونات

## 🎯 نصائح للنجاح

1. **تأكد من متطلبات النظام** قبل البدء
2. **احتفظ بنسخة احتياطية** من keystore للإنتاج
3. **اختبر التطبيق** على أجهزة مختلفة
4. **تأكد من عمل الخادم** قبل اختبار التطبيق
5. **استخدم شبكة WiFi مستقرة** أثناء التطوير

## ⏱️ الأوقات المتوقعة

- **تحميل وتثبيت Android Studio:** 30-60 دقيقة
- **فتح المشروع لأول مرة:** 10-15 دقيقة  
- **بناء APK الأول:** 5-10 دقائق
- **البناء اللاحق:** 2-5 دقائق
- **تثبيت التطبيق:** 1-2 دقيقة

---
**تاريخ آخر تحديث:** يوليو 2025  
**إصدار التطبيق:** 1.0  
**حالة التطبيق:** جاهز للاستخدام ✅