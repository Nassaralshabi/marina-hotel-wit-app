# 📱 دليل بناء تطبيق فندق مارينا Android APK

## ✅ تم تحديث التطبيق
- ✅ تم تحديث رابط الموقع إلى: `http://10.0.0.57/marina hotel/admin/`
- ✅ تم إعداد جميع الملفات المطلوبة
- ✅ التطبيق جاهز للبناء

## 🛠️ طرق البناء

### الطريقة الأولى: باستخدام Android Studio (مُوصى بها)

#### 1. تحميل وتثبيت Android Studio
```
رابط التحميل: https://developer.android.com/studio
```

#### 2. فتح المشروع
1. افتح Android Studio
2. اختر "Open an Existing Project"
3. اختر مجلد `android_app`
4. انتظر حتى يتم تحميل المشروع وتحميل Dependencies

#### 3. بناء APK
1. من القائمة: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
2. انتظر انتهاء عملية البناء
3. اضغط `locate` لفتح مجلد APK

### الطريقة الثانية: استخدام سطر الأوامر

#### متطلبات:
- Java JDK 8 أو أحدث
- Android SDK
- متغير البيئة ANDROID_HOME

#### خطوات البناء:
```bash
# في مجلد android_app
.\build_simple.bat
```

## 📁 مكان ملف APK
بعد البناء الناجح، ستجد ملف APK في:
```
android_app/app/build/outputs/apk/debug/app-debug.apk
```

## 📱 تثبيت التطبيق على الهاتف

### للاختبار المباشر:
1. وصل الهاتف بالكمبيوتر عبر USB
2. فعل "Developer Options" و "USB Debugging"
3. في Android Studio اضغط "Run"

### للتوزيع:
1. انسخ ملف `app-debug.apk` إلى الهاتف
2. في إعدادات الهاتف، فعل "Install from Unknown Sources"
3. اضغط على ملف APK لتثبيته

## ⚙️ تخصيص التطبيق

### تغيير اسم التطبيق:
```xml
<!-- في app/src/main/res/values/strings.xml -->
<string name="app_name">الاسم الجديد</string>
```

### تغيير رابط الموقع:
```java
// في MainActivity.java السطر 28
private static final String WEBSITE_URL = "http://your-new-url/";
```

### تغيير الألوان:
```xml
<!-- في app/src/main/res/values/colors.xml -->
<color name="primary_color">#YOUR_COLOR</color>
```

## 🔧 حل المشاكل الشائعة

### مشكلة: "SDK not found"
**الحل:**
1. تأكد من تثبيت Android Studio
2. في Android Studio: `File` → `Project Structure` → `SDK Location`
3. تأكد من مسار Android SDK

### مشكلة: "Gradle sync failed"
**الحل:**
```bash
.\gradlew.bat clean
.\gradlew.bat build
```

### مشكلة: التطبيق لا يحمل الموقع
**الحل:**
1. تأكد من أن الخادم يعمل على `http://10.0.0.57`
2. جرب فتح الرابط في متصفح الهاتف أولاً
3. تأكد من أن الهاتف متصل بنفس الشبكة

### مشكلة: "Network Security Error"
**الحل:** التطبيق مُعد للعمل مع HTTP. إذا استمرت المشكلة، تأكد من:
```xml
<!-- في AndroidManifest.xml -->
android:usesCleartextTraffic="true"
```

## 📋 مواصفات التطبيق

- **اسم التطبيق:** فندق مارينا
- **Package Name:** com.marinahotel.app
- **الحد الأدنى لنظام Android:** API 21 (Android 5.0)
- **الهدف:** API 34 (Android 14)
- **رابط الموقع:** http://10.0.0.57/marina hotel/admin/

## 🔐 ملاحظات الأمان

- التطبيق مُعد للاستخدام الداخلي مع IP محلي
- للإنتاج، يُنصح باستخدام HTTPS
- للتوزيع العام، يجب توقيع التطبيق بـ keystore

## 📞 الدعم

إذا واجهت مشاكل في البناء:
1. تأكد من تثبيت Java JDK
2. تأكد من تثبيت Android Studio
3. تأكد من اتصال الإنترنت لتحميل Dependencies
4. جرب تنظيف المشروع وإعادة البناء

## 🎯 خطوات سريعة للمبتدئين

1. **ثبت Android Studio** من الموقع الرسمي
2. **افتح مجلد android_app** في Android Studio
3. **انتظر تحميل المشروع** (قد يستغرق وقتاً في المرة الأولى)
4. **اضغط Build → Build APK**
5. **ابحث عن ملف APK** في app/build/outputs/apk/debug/
6. **انسخ APK إلى الهاتف** وثبته

**الوقت المتوقع:** 15-30 دقيقة للمرة الأولى