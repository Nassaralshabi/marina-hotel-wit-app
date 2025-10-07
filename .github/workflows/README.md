# GitHub Actions للبناء التلقائي

## نظرة عامة
يحتوي هذا المشروع على GitHub Actions لبناء تطبيق Marina Hotel Flutter تلقائياً.

## Workflows المتاحة

### 1. Flutter Build APK (الأساسي)
**الملف**: `flutter-build.yml`

#### متى يعمل:
- ✅ عند كل Push للفروع الرئيسية أو فروع Capy
- ✅ عند فتح Pull Request
- ✅ يمكن تشغيله يدوياً من GitHub Actions

#### ما يقوم به:
1. **إعداد البيئة**: Java 17 + Flutter 3.19.0
2. **التحليل**: فحص جودة الكود
3. **البناء**: 
   - Debug APK للاختبار
   - Release APK للإنتاج
   - App Bundle لـ Play Store
4. **الحفظ**: حفظ APKs كـ artifacts
5. **التعليق**: إضافة تعليق على PR بروابط التحميل
6. **الإصدار**: إنشاء Release تلقائي عند الدمج للـ main

#### المخرجات:
- 📱 Debug APK (للاختبار)
- 🚀 Release APK (للإنتاج)
- 📦 App Bundle (اختياري)

### 2. Quick Build APK (السريع)
**الملف**: `flutter-quick-build.yml`

#### متى يعمل:
- ✋ تشغيل يدوي فقط من GitHub Actions

#### خيارات البناء:
- `debug` - بناء نسخة Debug فقط
- `release` - بناء نسخة Release فقط  
- `both` - بناء كلا النسختين

#### الاستخدام:
1. اذهب إلى **Actions** في GitHub
2. اختر **Quick Build APK**
3. اضغط **Run workflow**
4. اختر نوع البناء
5. اضغط **Run workflow**

## كيفية الاستخدام

### تشغيل البناء التلقائي
```bash
# عند push للكود
git push origin capy/your-branch

# سيبدأ البناء تلقائياً
```

### تشغيل البناء اليدوي
1. اذهب إلى: `https://github.com/Nassaralshabi/marina-hotel-wit-app/actions`
2. اختر الـ workflow المطلوب
3. اضغط **Run workflow**
4. اختر الخيارات المطلوبة
5. اضغط **Run workflow** الأخضر

### تحميل APK
1. افتح صفحة Actions
2. اختر آخر بناء ناجح
3. في الأسفل، ابحث عن **Artifacts**
4. حمل الملف المطلوب:
   - `debug-apk` للاختبار
   - `release-apk` للإنتاج

## متطلبات المشروع

### ملف `mobile/pubspec.yaml`
تأكد من وجود المعلومات الأساسية:
```yaml
name: marina_hotel
description: Marina Hotel Management App
version: 1.0.0+1
```

### البنية المطلوبة
```
marina-hotel-wit-app/
├── .github/
│   └── workflows/
│       ├── flutter-build.yml
│       └── flutter-quick-build.yml
└── mobile/
    ├── pubspec.yaml
    └── lib/
        └── main.dart
```

## إعدادات إضافية (اختياري)

### لإضافة التوقيع للـ Release APK
1. أنشئ keystore:
```bash
keytool -genkey -v -keystore marina-hotel.keystore -alias marina -keyalg RSA -keysize 2048 -validity 10000
```

2. أضف الـ secrets في GitHub:
- `ANDROID_KEYSTORE` - الـ keystore مشفر بـ base64
- `ANDROID_KEYSTORE_PASSWORD` - كلمة مرور الـ keystore
- `ANDROID_KEY_ALIAS` - اسم المفتاح
- `ANDROID_KEY_PASSWORD` - كلمة مرور المفتاح

3. عدّل الـ workflow لاستخدام التوقيع.

## حل المشاكل

### إذا فشل البناء
1. تحقق من logs في GitHub Actions
2. تأكد من أن `flutter pub get` يعمل محلياً
3. تحقق من أن `flutter analyze` لا يُظهر أخطاء حرجة

### إذا كان APK كبير الحجم
- استخدم `--split-per-abi` لإنشاء APKs منفصلة لكل معمارية
- فعّل ProGuard/R8 للـ minification

### إذا احتجت مساعدة
- افتح Issue في المشروع
- راجع [Flutter Build Documentation](https://docs.flutter.dev/deployment/android)

## الميزات القادمة
- [ ] بناء iOS IPA
- [ ] نشر تلقائي لـ Play Store
- [ ] اختبارات تلقائية
- [ ] تقارير الأداء

---
آخر تحديث: ديسمبر 2024