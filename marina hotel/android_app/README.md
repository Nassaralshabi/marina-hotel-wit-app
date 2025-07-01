# تطبيق فندق مارينا - Android APK

## 📱 وصف التطبيق
تطبيق أندرويد لنظام إدارة فندق مارينا يتيح الوصول السهل لجميع وظائف النظام من الهاتف المحمول.

## 🚀 المميزات
- ✅ واجهة سهلة الاستخدام
- ✅ دعم اللغة العربية
- ✅ تحديث تلقائي للمحتوى
- ✅ عمل بدون اتصال محدود
- ✅ دعم جميع أحجام الشاشات
- ✅ شاشة ترحيب جميلة
- ✅ معالجة أخطاء الاتصال

## 🛠️ متطلبات البناء

### البرامج المطلوبة:
1. **Android Studio** (أحدث إصدار)
2. **Java JDK 8** أو أحدث
3. **Android SDK** (API Level 21+)

### خطوات البناء:

#### 1. إعداد البيئة:
```bash
# تحميل وتثبيت Android Studio
# https://developer.android.com/studio

# تحميل وتثبيت Java JDK
# https://www.oracle.com/java/technologies/downloads/
```

#### 2. فتح المشروع:
1. افتح Android Studio
2. اختر "Open an Existing Project"
3. اختر مجلد `android_app`
4. انتظر حتى يتم تحميل المشروع

#### 3. تحديث رابط الموقع:
```java
// في ملف MainActivity.java
// غير هذا السطر:
private static final String WEBSITE_URL = "http://your-domain.com/marina-hotel/admin/";

// إلى رابط موقعك الفعلي:
private static final String WEBSITE_URL = "http://yourserver.com/marina-hotel/admin/";
```

#### 4. بناء التطبيق:

##### للتطوير (Debug):
```bash
# في Terminal داخل Android Studio
./gradlew assembleDebug
```

##### للإنتاج (Release):
```bash
# إنشاء keystore أولاً
keytool -genkey -v -keystore marina-hotel-key.keystore -alias marina-hotel -keyalg RSA -keysize 2048 -validity 10000

# بناء APK للإنتاج
./gradlew assembleRelease
```

#### 5. العثور على ملف APK:
```
app/build/outputs/apk/debug/app-debug.apk          # للتطوير
app/build/outputs/apk/release/app-release.apk      # للإنتاج
```

## 📋 إعدادات إضافية

### تخصيص التطبيق:

#### 1. تغيير اسم التطبيق:
```xml
<!-- في app/src/main/res/values/strings.xml -->
<string name="app_name">اسم التطبيق الجديد</string>
```

#### 2. تغيير الألوان:
```xml
<!-- في app/src/main/res/values/colors.xml -->
<color name="primary_color">#667eea</color>
<color name="primary_dark">#764ba2</color>
```

#### 3. تغيير الأيقونة:
- استبدل الملفات في `app/src/main/res/mipmap/`
- استخدم أداة Image Asset في Android Studio

#### 4. إضافة خطوط عربية:
```xml
<!-- إنشاء مجلد app/src/main/res/font/ -->
<!-- إضافة ملفات الخطوط .ttf -->
<!-- استخدامها في layout files -->
android:fontFamily="@font/cairo_regular"
```

## 🔧 استكشاف الأخطاء

### مشاكل شائعة:

#### 1. خطأ في البناء:
```bash
# تنظيف المشروع
./gradlew clean

# إعادة البناء
./gradlew build
```

#### 2. مشكلة في الاتصال:
- تأكد من أن الرابط صحيح
- تأكد من أن الخادم يدعم HTTPS
- أضف `android:usesCleartextTraffic="true"` في AndroidManifest.xml للـ HTTP

#### 3. مشكلة في العرض:
- تأكد من أن الموقع متجاوب
- اختبر الموقع في متصفح الهاتف أولاً

## 📱 التثبيت على الهاتف

### للتطوير:
1. فعل "Developer Options" في الهاتف
2. فعل "USB Debugging"
3. وصل الهاتف بالكمبيوتر
4. اضغط "Run" في Android Studio

### للتوزيع:
1. انسخ ملف APK إلى الهاتف
2. فعل "Install from Unknown Sources"
3. اضغط على ملف APK لتثبيته

## 🔐 الأمان

### للإنتاج:
- استخدم HTTPS فقط
- وقع التطبيق بـ keystore آمن
- فعل ProGuard للحماية
- اختبر التطبيق جيداً قبل التوزيع

## 📞 الدعم
للمساعدة في بناء التطبيق أو حل المشاكل، يرجى التواصل مع فريق التطوير.

## 📄 الترخيص
هذا التطبيق مخصص لفندق مارينا ولا يجوز استخدامه لأغراض أخرى بدون إذن.
