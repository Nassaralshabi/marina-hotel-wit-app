# 🏨 Marina Hotel - Android Management App

[![Build APK](https://github.com/Nassaralshabi/marina-hotel-wit-app/actions/workflows/build-apk.yml/badge.svg)](https://github.com/Nassaralshabi/marina-hotel-wit-app/actions/workflows/build-apk.yml)
[![Code Quality](https://github.com/Nassaralshabi/marina-hotel-wit-app/actions/workflows/quality-check.yml/badge.svg)](https://github.com/Nassaralshabi/marina-hotel-wit-app/actions/workflows/quality-check.yml)
[![Android](https://img.shields.io/badge/Android-7.0%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg)](https://kotlinlang.org)

تطبيق إدارة فندق Marina Hotel - نظام شامل لإدارة الحجوزات والضيوف والمالية والموظفين مع دعم كامل للغة العربية.

---

## 🚀 **المزايا الرئيسية**

### 📱 **إدارة الحجوزات**
- إنشاء وتعديل الحجوزات
- متابعة حالة الغرف (متاحة/محجوزة/صيانة)
- نظام الدفع والفواتير
- تأكيد الحجوزات تلقائياً

### 👥 **إدارة الضيوف**  
- ملفات شخصية للضيوف
- سجل الحجوزات السابقة
- تفضيلات الضيوف
- نظام نقاط الولاء

### 💰 **النظام المالي**
- تتبع الدخل والمصروفات
- تقارير مالية تفصيلية
- نظام الخزينة
- إدارة رواتب الموظفين

### 👨‍💼 **إدارة الموظفين**
- ملفات الموظفين
- إدارة المناوبات
- سجلات الحضور والغياب
- نظام الأذونات والإجازات

### 📊 **التقارير والإحصائيات**
- تقارير الإشغال
- تحليل الإيرادات
- إحصائيات الأداء
- تصدير PDF وExcel

---

## 📱 **لقطات الشاشة**

<table>
  <tr>
    <td><img src="screenshots/dashboard.jpg" width="200"/></td>
    <td><img src="screenshots/bookings.jpg" width="200"/></td>
    <td><img src="screenshots/rooms.jpg" width="200"/></td>
    <td><img src="screenshots/reports.jpg" width="200"/></td>
  </tr>
  <tr>
    <td align="center">الشاشة الرئيسية</td>
    <td align="center">إدارة الحجوزات</td>
    <td align="center">إدارة الغرف</td>
    <td align="center">التقارير</td>
  </tr>
</table>

---

## 🛠 **التقنيات المُستخدمة**

### **📱 Android Development**
- **Kotlin** - اللغة الأساسية
- **Android SDK** - API 24+ (Android 7.0+)
- **Material Design** - واجهة مستخدم حديثة
- **ViewBinding** - ربط آمن للواجهات

### **🗄 البيانات**
- **Room Database** - قاعدة بيانات محلية
- **SQLite** - تخزين البيانات
- **Kotlin Coroutines** - العمليات غير المتزامنة

### **🎨 واجهة المستخدم**
- **Material Design Components**
- **RecyclerView** - القوائم المُحسّنة
- **ViewPager2** - التنقل بين الشاشات
- **Navigation Component** - تنقل آمن

### **🔧 البناء والأدوات**
- **Gradle Kotlin DSL** - نظام البناء
- **ProGuard** - تشويش وضغط الكود
- **GitHub Actions** - CI/CD
- **Android Studio** - بيئة التطوير

---

## 📦 **تحميل وتثبيت**

### **📥 تحميل APK**
```bash
# أحدث إصدار مستقر
https://github.com/Nassaralshabi/marina-hotel-wit-app/releases/latest

# إصدارات تطويرية  
https://github.com/Nassaralshabi/marina-hotel-wit-app/actions
```

### **🔧 متطلبات النظام**
| المتطلب | القيمة |
|---------|-------|
| **نظام التشغيل** | Android 7.0+ (API 24) |
| **ذاكرة التخزين** | 50 MB مساحة فارغة |
| **ذاكرة RAM** | 2 GB (يُفضل 4 GB) |
| **الأذونات** | الإنترنت، التخزين، الكاميرا |

### **📲 خطوات التثبيت**
1. حمل APK من [الإصدارات](https://github.com/Nassaralshabi/marina-hotel-wit-app/releases)
2. فعّل "التثبيت من مصادر غير معروفة" في إعدادات الجهاز
3. اضغط على ملف APK لتثبيته
4. افتح التطبيق وابدأ الاستخدام!

---

## 💻 **تطوير المشروع**

### **🔧 إعداد البيئة**
```bash
# استنساخ المشروع
git clone https://github.com/Nassaralshabi/marina-hotel-wit-app.git
cd marina-hotel-wit-app

# إعداد Keystore (للإصدارات الرسمية)
./setup_keystore.sh

# بناء Debug APK
./gradlew assembleDebug

# بناء Release APK (يحتاج keystore)
./gradlew assembleRelease
```

### **📋 متطلبات التطوير**
- **Java 17** أو أحدث
- **Android Studio** Hedgehog أو أحدث  
- **Android SDK** API 34
- **Gradle** 8.7+
- **Git** للتحكم في الإصدارات

### **🏗 أنواع البناء**
```kotlin
debug {
    applicationIdSuffix = ".debug"
    // للتطوير والاختبار
}

staging {
    applicationIdSuffix = ".staging"
    // للاختبار قبل الإنتاج
}

release {
    // الإصدار النهائي للإنتاج
    isMinifyEnabled = true
    proguardFiles(...)
}
```

---

## 🤖 **GitHub Actions CI/CD**

### **🔨 Build Workflows**
- **Build APK** - بناء تلقائي عند كل push/PR
- **Code Quality** - فحص جودة الكود والأمان
- **Release** - إنشاء إصدارات تلقائية

### **⚡ الميزات المتقدمة**
- **Automatic APK signing** مع keystore آمن
- **ProGuard obfuscation** للحماية
- **Multiple build types** (debug/staging/release)
- **Security scanning** مع dependency check
- **Automated releases** عند إنشاء tags

### **🎯 كيفية الاستخدام**
```bash
# Push للـ main = Release APK
git push origin main

# Push للـ develop = Staging APK
git push origin develop  

# Pull Request = Debug APK
# يبنى تلقائياً

# إصدار رسمي
git tag v1.2.0
git push origin v1.2.0
# يُنشئ GitHub Release تلقائياً!
```

---

## 🔒 **الأمان والخصوصية**

### **🛡 ميزات الأمان**
- **ProGuard Code Obfuscation** - حماية الكود
- **Network Security Config** - حماية الاتصالات
- **Certificate Pinning** - منع MITM attacks
- **Secure KeyStore Management** - حفظ آمن للمفاتيح
- **No Hardcoded Secrets** - عدم وجود كلمات مرور في الكود

### **🔐 حماية البيانات**
- **Local SQLite Encryption** - تشفير قاعدة البيانات
- **Secure Backup Handling** - حماية النسخ الاحتياطية  
- **Privacy Controls** - إعدادات الخصوصية
- **GDPR Compliance** - متوافق مع قوانين الخصوصية

---

## 🤝 **المساهمة في المشروع**

نرحب بمساهماتكم! إليكم كيفية المساعدة:

### **🐛 تقرير المشاكل**
1. استخدم [Bug Report Template](https://github.com/Nassaralshabi/marina-hotel-wit-app/issues/new?template=bug_report.yml)
2. أضف تفاصيل كاملة
3. أرفق screenshots إن أمكن

### **✨ اقتراح ميزات جديدة**  
1. استخدم [Feature Request Template](https://github.com/Nassaralshabi/marina-hotel-wit-app/issues/new?template=feature_request.yml)
2. اشرح الفائدة المتوقعة
3. أضف تصاميم إن وُجدت

### **💻 المساهمة في الكود**
1. Fork المشروع
2. أنشئ feature branch: `git checkout -b feature/amazing-feature`
3. Commit التغييرات: `git commit -m 'Add amazing feature'`
4. Push للـ branch: `git push origin feature/amazing-feature`  
5. أنشئ Pull Request

---

## 📖 **الدعم والتوثيق**

### **📚 الموارد المفيدة**
- [📖 Wiki](https://github.com/Nassaralshabi/marina-hotel-wit-app/wiki) - دليل المستخدم
- [🚀 GitHub Actions Guide](GITHUB_ACTIONS_GUIDE.md) - دليل CI/CD
- [🔐 Build Security Guide](BUILD_SECURITY_GUIDE.md) - دليل الأمان
- [🔑 Keystore Setup Guide](KEYSTORE_SETUP_GUIDE.md) - إعداد التوقيع

### **💬 الحصول على المساعدة**
- [💭 Discussions](https://github.com/Nassaralshabi/marina-hotel-wit-app/discussions) - أسئلة عامة
- [🐛 Issues](https://github.com/Nassaralshabi/marina-hotel-wit-app/issues) - مشاكل وأخطاء
- [📧 Email](mailto:nassaralshabi@marina-hotel.com) - دعم مباشر

---

## 📄 **الترخيص**

هذا المشروع مُرخّص تحت [MIT License](LICENSE) - انظر ملف الترخيص لمزيد من التفاصيل.

---

## 🙏 **شكر وتقدير**

- **المطور الرئيسي**: [Nassar Alshabi](https://github.com/Nassaralshabi)
- **التصميم**: فريق Marina Hotel Design  
- **الاختبار**: مجتمع Marina Hotel
- **المكتبات مفتوحة المصدر**: شكر خاص لجميع المطورين

---

<div align="center">

### 🏨 **صُنع بـ ❤️ لفندق Marina Hotel**

**إذا أعجبك المشروع، لا تنس إضافة ⭐!**

[![GitHub stars](https://img.shields.io/github/stars/Nassaralshabi/marina-hotel-wit-app.svg?style=social&label=Star)](https://github.com/Nassaralshabi/marina-hotel-wit-app)
[![GitHub forks](https://img.shields.io/github/forks/Nassaralshabi/marina-hotel-wit-app.svg?style=social&label=Fork)](https://github.com/Nassaralshabi/marina-hotel-wit-app/fork)
[![GitHub watchers](https://img.shields.io/github/watchers/Nassaralshabi/marina-hotel-wit-app.svg?style=social&label=Watch)](https://github.com/Nassaralshabi/marina-hotel-wit-app)

</div>