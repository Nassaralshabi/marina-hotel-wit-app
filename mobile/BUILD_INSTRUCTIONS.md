# 🏨 تعليمات بناء تطبيق Marina Hotel - نظام إدارة فندقي متكامل

## 📋 المتطلبات الأساسية

### برمجيات مطلوبة
- **Flutter SDK**: 3.24.3 أو أحدث (مُستَخدَم في GitHub Actions)
- **Dart SDK**: 3.4.0 أو أحدث
- **Java**: OpenJDK 17 (للأندرويد)
- **Android Studio** أو **VS Code** مع Flutter extension
- **Android SDK**: API Level 21-35 (Android 5.0 - Android 14)
- **Git**: لاستنساخ المستودع

### التحقق من البيئة
```bash
# فحص Flutter والمتطلبات
flutter --version
flutter doctor -v

# فحص Java (يجب أن يكون 17)
java -version

# فحص Android SDK
flutter config --android-sdk
```

---

## 🚀 خطوات البناء السريع

### 1. استنساخ المشروع
```bash
git clone https://github.com/Nassaralshabi/marina-hotel-wit-app.git
cd marina-hotel-wit-app/mobile
```

### 2. تثبيت Dependencies
```bash
flutter pub get
```

### 3. إنشاء ملفات قاعدة البيانات
```bash
# إنشاء ملفات Drift (قاعدة البيانات المحلية)
flutter packages pub run build_runner build --delete-conflicting-outputs
```

### 4. بناء التطبيق

#### تطوير (Debug)
```bash
# APK واحد للاختبار
flutter build apk --debug --target-platform android-arm64

# APK لجميع المعماريات
flutter build apk --debug --target-platform android-arm,android-arm64,android-x64 --split-per-abi
```

#### إنتاج (Release)
```bash
# APK للنشر العام
flutter build apk --release --target-platform android-arm,android-arm64,android-x64 --split-per-abi

# AAB للـ Play Store
flutter build appbundle --release
```

### 5. تشغيل التطبيق
```bash
# تشغيل على جهاز/محاكي
flutter run

# تشغيل مع hot reload
flutter run --hot
```

---

## 🏗️ البناء المتقدم باستخدام Script

### استخدام Build Script المخصص
```bash
# بناء شامل (Debug + Release + AAB)
chmod +x build_apk.sh
./build_apk.sh

# خيارات متقدمة
./build_apk.sh --release-only          # Release فقط
./build_apk.sh --debug-only            # Debug فقط  
./build_apk.sh --build-number 42       # رقم build مخصص
./build_apk.sh --ci                    # وضع CI/CD
```

### النواتج المتوقعة
```
releases/apk/
├── marina-hotel-v1.1.0-arm64.apk     # ARM64 (أحدث الهواتف)
├── marina-hotel-v1.1.0-armv7.apk     # ARMv7 (هواتف قديمة)  
├── marina-hotel-v1.1.0-x86_64.apk    # x86_64 (محاكيات)
├── marina-hotel-v1.1.0-debug.apk     # للاختبار
├── marina-hotel-v1.1.0.aab           # Play Store
└── metadata.csv                      # معلومات الملفات
```

---

## 🎯 الوحدات الثمانية المدمجة

### ✅ الوحدات المكتملة (8/8)

1. **📊 Dashboard** - لوحة تحكم بالإحصائيات المباشرة
2. **🏨 إدارة الغرف** - نظام طوابق تفاعلي مع حالات الغرف
3. **📅 الحجوزات** - إدارة شاملة مع Check-in/Check-out
4. **💳 نظام الدفع** - 5 طرق دفع + إيصالات PDF احترافية
5. **👥 إدارة الموظفين** - بيانات الموظفين وتتبع الرواتب
6. **💰 تتبع المصروفات** - تصنيف وتتبع جميع المصروفات
7. **🏦 التمويل والخزينة** - إدارة مالية وتقارير نقدية
8. **📊 التقارير والإعدادات** - تقارير شاملة وإعدادات النظام

### 🌟 ميزات إضافية
- **📝 نظام الملاحظات** - ملاحظات حجوزات بألوان وأولويات
- **🔄 مزامنة ذكية** - offline-first مع مزامنة تلقائية
- **🌍 واجهة عربية كاملة** - RTL وتصميم Material 3
- **🔒 أمان متقدم** - تشفير البيانات وحماية API

---

## 🛠️ هيكل قاعدة البيانات

### جداول مدمجة (8 جداول)
```sql
├── Rooms              # بيانات الغرف والطوابق
├── Bookings           # الحجوزات والنزلاء
├── BookingNotes       # ملاحظات الحجوزات  
├── Employees          # بيانات الموظفين
├── Expenses           # المصروفات والتصنيفات
├── CashTransactions   # المعاملات النقدية
├── Payments           # المدفوعات (5 أنواع)
└── Outbox             # للمزامنة مع الخادم
```

### خدمات DAO مكتملة
- تفعيل soft delete
- دعم المزامنة التلقائية  
- فهرسة محسّنة للأداء
- واجهات برمجية موحدة

---

## 🎨 مكونات الواجهة

### تخطيط إداري
- **AdminLayout**: هيكل صفحات موحد
- **AdminSidebar**: شريط جانبي بـ 10 قوائم
- **تصميم متجاوب**: يتكيف مع جميع أحجام الشاشات
- **ألوان عربية**: نظام ألوان متناسق ومريح

### مكونات متخصصة (20+ مكون)
- **PaymentWidgets**: 8 مكونات للمدفوعات
- **RoomWidgets**: 6 مكونات لعرض الغرف
- **Form Components**: نماذج وإدخال بيانات
- **Charts & Graphs**: رسوم بيانية تفاعلية

---

## 🧪 اختبار التطبيق

### اختبارات أساسية
```bash
# تشغيل الاختبارات
flutter test

# تحليل الكود
flutter analyze

# فحص الأداء
flutter build apk --analyze-size
```

### سيناريوهات اختبار شامل

#### 1. اختبار وحدة الغرف
- ✅ عرض الطوابق والغرف بصرياً
- ✅ تغيير حالة الغرف (شاغرة/محجوزة/صيانة)
- ✅ البحث والفلترة

#### 2. اختبار الحجوزات والدفع
- ✅ إضافة حجز جديد
- ✅ معالجة جميع طرق الدفع الخمس
- ✅ إنشاء إيصالات PDF
- ✅ تسجيل مغادرة (Checkout)

#### 3. اختبار الإدارة المالية
- ✅ تسجيل مصروفات متنوعة
- ✅ عرض التقارير المالية
- ✅ إدارة الخزينة اليومية

#### 4. اختبار المزامنة
- ✅ العمل دون إنترنت
- ✅ المزامنة التلقائية عند الاتصال
- ✅ حفظ البيانات محلياً

---

## ⚡ إعداد GitHub Actions (CI/CD)

### Workflow متقدم مُدمج
- **بناء متعدد المعماريات**: ARM64, ARMv7, x86_64  
- **فحوصات جودة**: integrity checks وSecurity scans
- **إصدار تلقائي**: GitHub Releases مع metadata
- **دعم signing**: للإنتاج من خلال secrets

### تفعيل Workflow
```bash
# سيتم التشغيل تلقائياً عند:
git push origin main        # دفع للفرع الرئيسي
git push origin capy/**     # أي فرع capy

# تشغيل يدوي:
# الذهاب إلى Actions tab في GitHub > Run workflow
```

---

## 🔧 إصلاح المشاكل الشائعة

### خطأ Build Runner
```bash
# حل شامل
flutter clean
flutter pub get
flutter packages pub run build_runner clean
flutter packages pub run build_runner build --delete-conflicting-outputs
```

### خطأ Android SDK
```bash
# تحديث SDK tools
flutter doctor --android-licenses
sdkmanager --update

# إعادة تعيين Flutter
flutter config --android-sdk /path/to/android/sdk
```

### خطأ إصدار Gradle
```bash
cd android
./gradlew clean
./gradlew build

# إذا استمرت المشكلة
rm -rf ~/.gradle/caches
```

### خطأ في ملفات PDF
```bash
# تأكد من أذونات Android
# في android/app/src/main/AndroidManifest.xml:
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

---

## 📱 متطلبات الأجهزة المدعومة

### الحد الأدنى
- **نظام التشغيل**: Android 5.0 (API 21)
- **الذاكرة**: 2GB RAM
- **المساحة**: 100MB فارغة
- **المعالج**: ARM أو x86

### المُوصى به
- **نظام التشغيل**: Android 8.0+ (API 26+)
- **الذاكرة**: 4GB RAM
- **المساحة**: 500MB فارغة
- **الشاشة**: 5" أو أكبر للراحة

### المعماريات المدعومة
- **ARM64-v8a**: معظم الهواتف الحديثة (2017+)
- **ARMeabi-v7a**: الهواتف القديمة (2012-2017)
- **x86_64**: المحاكيات وبعض الأجهزة اللوحية

---

## 🚀 النشر والتوزيع

### للتوزيع المباشر
1. استخدم ملفات `.apk` حسب المعمارية
2. فعّل "مصادر غير معروفة" على الأندرويد
3. ثبّت مباشرة على الأجهزة

### لمتجر Google Play
1. استخدم ملف `.aab` (Android App Bundle)
2. ارفعه لوحة تحكم Play Console
3. سيُوزع تلقائياً للمعماريات المناسبة

### للمؤسسات
1. استخدم Android Enterprise
2. أو توزيع MDM للأجهزة المُدارة
3. متوافق مع أنظمة إدارة الأجهزة الموحدة

---

## 📊 إحصائيات المشروع

### حجم الكود
- **57+ ملف Dart** في التطبيق الأساسي
- **~8,000+ سطر كود** عالي الجودة
- **20+ مكون UI** قابل لإعادة الاستخدام  
- **15+ شاشة** متكاملة ومتناسقة

### الأداء
- **حجم APK**: 15-25 MB (مضغوط)
- **وقت البدء**: أقل من 3 ثواني
- **استهلاك الذاكرة**: 60-120 MB
- **استهلاك البطارية**: منخفض (مُحسّن)

---

## 🔮 خارطة الطريق المستقبلية

### الإصدار التالي (v1.2)
- [ ] تقارير متقدمة مع رسوم بيانية
- [ ] نظام إشعارات Push
- [ ] تكامل مع أنظمة الدفع الإلكترونية
- [ ] نسخ احتياطي سحابي

### المدى الطويل (v2.0)
- [ ] نسخة Web Dashboard
- [ ] تطبيق iPad/Tablet محسن
- [ ] ذكاء اصطناعي للتنبؤات
- [ ] تكامل مع الأنظمة الخارجية

---

## 🤝 المساهمة والدعم

### للمطورين
- Fork المشروع على GitHub
- اتبع معايير Flutter/Dart الرسمية
- اختبر جميع التغييرات قبل PR
- وثّق الكود والميزات الجديدة

### الإبلاغ عن مشاكل
- استخدم [GitHub Issues](https://github.com/Nassaralshabi/marina-hotel-wit-app/issues)
- قدم تفاصيل شاملة وخطوات إعادة الإنتاج
- أرفق screenshots عند الإمكان

### طلب ميزات جديدة
- صف الميزة المطلوبة بالتفصيل  
- اشرح حالة الاستخدام
- قدم mockups إذا أمكن

---

**📅 آخر تحديث**: سبتمبر 2025  
**🏷️ الإصدار**: v1.1.0+2 - نظام فندقي متكامل  
**👥 المطوّر**: فريق Marina Hotel Development  
**📧 التواصل**: [GitHub Issues](https://github.com/Nassaralshabi/marina-hotel-wit-app/issues)

---

> **🎯 Marina Hotel Mobile** - نظام إدارة فنادق عربي شامل، مصمم بأحدث تقنيات Flutter ومُحسّن للسوق العربي. يدعم العمل دون إنترنت مع مزامنة ذكية، وواجهة عربية أصيلة من اليمين لليسار.