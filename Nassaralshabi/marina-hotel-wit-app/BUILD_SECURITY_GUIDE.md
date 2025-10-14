# Marina Hotel Kotlin - Build Security Guide

## 🔐 إعدادات الأمان المُحسّنة

تم تطبيق عدة تحسينات أمنية على مشروع Marina Hotel Kotlin لضمان أقصى حماية وأداء.

---

## 📋 **التحسينات المُطبّقة**

### 1. **ProGuard والتشويش المتقدم**
- ✅ تفعيل ProGuard في الإصدارات النهائية
- ✅ قواعد تشويش شاملة لحماية الكود
- ✅ إزالة debug logs من الإصدارات النهائية
- ✅ تحسين وضغط الموارد

### 2. **إعدادات التوقيع الآمنة**
- ✅ keystore منفصل للـ debug والـ release
- ✅ استخدام متغيرات البيئة للمعلومات الحساسة
- ✅ template لإعداد keystore الإنتاجي
- ✅ حماية من رفع المفاتيح الخاصة للـ Git

### 3. **أمان الشبكة**
- ✅ Network Security Config للحماية من MITM
- ✅ منع HTTP traffic في الإصدارات النهائية
- ✅ Certificate pinning للـ APIs الرسمية
- ✅ إعدادات مرنة للتطوير والإنتاج

### 4. **حماية البيانات**
- ✅ منع backup للبيانات الحساسة
- ✅ إعدادات Data Extraction Rules لـ Android 12+
- ✅ File Provider آمن للتعامل مع الملفات
- ✅ حماية قاعدة البيانات والملفات الحساسة

---

## 🛠 **كيفية الاستخدام**

### **للتطوير (Debug Build)**
```bash
./gradlew assembleDebug
```

### **للاختبار (Staging Build)**  
```bash
./gradlew assembleStaging
```

### **للإنتاج (Release Build)**
```bash
# تأكد من إعداد keystore.properties أولاً
./gradlew assembleRelease
```

---

## 🔑 **إعداد Keystore للإنتاج**

### 1. **إنشاء Release Keystore**
```bash
keytool -genkeypair -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 25000 \
  -alias marina-hotel-release \
  -dname "CN=Marina Hotel, OU=Mobile Development, O=Marina Hotel, L=Jeddah, ST=Makkah, C=SA"
```

### 2. **إعداد keystore.properties**
```properties
storeFile=release.keystore  
storePassword=your_secure_password
keyAlias=marina-hotel-release
keyPassword=your_key_password
```

### 3. **متغيرات البيئة (للـ CI/CD)**
```bash
export MARINA_KEYSTORE_FILE=path/to/release.keystore
export MARINA_KEYSTORE_PASSWORD=your_password
export MARINA_KEY_ALIAS=marina-hotel-release  
export MARINA_KEY_PASSWORD=your_key_password
```

---

## 🏗 **Build Types المُحسّنة**

### **Debug**
- Application ID: `com.marinahotel.kotlin.debug`
- منع التشويش لسهولة debugging
- شهادة تطوير تلقائية
- API endpoint للتطوير

### **Staging**  
- Application ID: `com.marinahotel.kotlin.staging`
- تشويش مُفعّل للاختبار
- API endpoint للـ staging
- تحسينات متوسطة

### **Release**
- Application ID: `com.marinahotel.kotlin`  
- تشويش وضغط كامل
- شهادة رسمية
- API endpoint الرسمي
- أقصى تحسينات

---

## ⚡ **تحسينات الأداء**

### **Gradle Optimization**
- Build cache مُفعّل
- Parallel builds
- Configuration cache
- R8 full mode

### **Kotlin Optimization**  
- Incremental compilation
- IR backend
- JVM optimizations

### **Android Optimization**
- Resource shrinking
- PNG crunching  
- Vector drawables
- MultiDex support

---

## 🔒 **إرشادات الأمان**

### **⚠️ لا ترفع هذه الملفات إلى Git:**
- `*.keystore` - ملفات المفاتيح
- `keystore.properties` - إعدادات التوقيع
- `local.properties` - إعدادات محلية
- `google-services.json` - مفاتيح Firebase

### **✅ آمن للرفع:**
- `keystore.properties.template` - نموذج الإعدادات
- `proguard-rules.pro` - قواعد التشويش
- `network_security_config.xml` - إعدادات الشبكة
- جميع ملفات `.gradle.kts`

---

## 📱 **اختبار الـ Build**

### **التحقق من التوقيع**
```bash
# للتأكد من توقيع APK
jarsigner -verify -verbose -certs app-release.apk

# معلومات التوقيع
keytool -printcert -jarfile app-release.apk
```

### **فحص ProGuard**
```bash  
# التأكد من التشويش
unzip -l app-release.apk | grep classes.dex
```

### **فحص الأمان**
```bash
# التأكد من إعدادات الأمان
aapt dump xmltree app-release.apk AndroidManifest.xml
```

---

## 🆘 **استكشاف الأخطاء**

### **مشاكل التوقيع**
- تأكد من صحة مسارات keystore
- تحقق من كلمات المرور
- تأكد من وجود alias صحيح

### **مشاكل ProGuard**
- راجع mapping.txt لفهم التشويش
- أضف قواعد keep للكلاسات المطلوبة
- تحقق من logs البناء

### **مشاكل الشبكة**  
- تأكد من إعدادات network security config
- تحقق من certificate pinning
- راجع cleartext traffic permissions

---

## 📞 **الدعم التقني**

للحصول على مساعدة إضافية في إعداد البناء والأمان:

- مراجعة logs البناء: `./gradlew build --info`
- فحص التبعيات: `./gradlew dependencies`
- تحليل الأمان: `./gradlew lint`

---

**تم إعداد هذا الدليل لضمان أقصى أمان وأداء لتطبيق Marina Hotel** 🏨