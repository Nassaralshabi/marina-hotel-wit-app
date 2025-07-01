# 🚀 دليل البناء السريع - تطبيق فندق مارينا

## ⚡ خطوات سريعة لبناء APK

### 1️⃣ **التحضير (5 دقائق)**
```bash
# تحميل Android Studio من:
# https://developer.android.com/studio

# تثبيت Java JDK 8+ من:
# https://www.oracle.com/java/technologies/downloads/
```

### 2️⃣ **فتح المشروع (2 دقيقة)**
1. افتح Android Studio
2. اختر "Open an Existing Project"
3. اختر مجلد `android_app`
4. انتظر تحميل المشروع

### 3️⃣ **تحديث الرابط (1 دقيقة)**
```java
// في ملف: app/src/main/java/com/marinahotel/app/MainActivity.java
// السطر 25 - غير الرابط:

private static final String WEBSITE_URL = "http://YOUR_SERVER_IP/marina-hotel/admin/";

// مثال:
private static final String WEBSITE_URL = "http://192.168.1.100/marina-hotel/admin/";
```

### 4️⃣ **بناء APK (3 دقائق)**

#### الطريقة الأولى - من Android Studio:
1. اضغط `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
2. انتظر انتهاء البناء
3. اضغط `locate` لفتح مجلد APK

#### الطريقة الثانية - من Terminal:
```bash
# في مجلد android_app
./gradlew assembleDebug

# أو استخدم الملف المساعد:
build_apk.bat
```

### 5️⃣ **العثور على APK**
```
📁 android_app/
  📁 app/
    📁 build/
      📁 outputs/
        📁 apk/
          📁 debug/
            📄 app-debug.apk  ← هذا هو ملف APK
```

### 6️⃣ **تثبيت على الهاتف**
1. انسخ `app-debug.apk` إلى الهاتف
2. فعل "تثبيت من مصادر غير معروفة" في إعدادات الهاتف
3. اضغط على ملف APK لتثبيته

---

## 🔧 حل المشاكل السريع

### ❌ خطأ: "SDK not found"
```bash
# في Android Studio:
File → Project Structure → SDK Location
# تأكد من مسار Android SDK
```

### ❌ خطأ: "Gradle sync failed"
```bash
# في Terminal:
./gradlew clean
./gradlew build
```

### ❌ التطبيق لا يحمل الموقع
1. تأكد من أن الرابط صحيح
2. تأكد من أن الخادم يعمل
3. جرب الرابط في متصفح الهاتف أولاً

### ❌ مشكلة في الشبكة
```xml
<!-- في AndroidManifest.xml أضف: -->
android:usesCleartextTraffic="true"
```

---

## 📱 اختبار سريع

### على الكمبيوتر:
1. وصل الهاتف بـ USB
2. فعل "USB Debugging"
3. اضغط "Run" في Android Studio

### على الهاتف مباشرة:
1. ثبت APK
2. افتح التطبيق
3. تأكد من تحميل الموقع

---

## ⚙️ تخصيص سريع

### تغيير اسم التطبيق:
```xml
<!-- app/src/main/res/values/strings.xml -->
<string name="app_name">اسم جديد</string>
```

### تغيير الألوان:
```xml
<!-- app/src/main/res/values/colors.xml -->
<color name="primary_color">#YOUR_COLOR</color>
```

### تغيير الأيقونة:
1. اضغط يمين على `res/mipmap`
2. اختر `New` → `Image Asset`
3. اختر الصورة الجديدة

---

## 🎯 نصائح مهمة

✅ **اختبر الرابط في المتصفح أولاً**
✅ **تأكد من أن الخادم يعمل**
✅ **استخدم IP بدلاً من localhost**
✅ **فعل Developer Options في الهاتف للاختبار**
✅ **احتفظ بنسخة من keystore للإنتاج**

---

## 📞 مساعدة سريعة

إذا واجهت مشاكل:
1. تأكد من أن Android Studio محدث
2. تأكد من أن Java JDK مثبت
3. جرب `./gradlew clean` ثم `./gradlew build`
4. تأكد من أن الرابط يعمل في المتصفح

**وقت البناء المتوقع: 10-15 دقيقة للمرة الأولى**
