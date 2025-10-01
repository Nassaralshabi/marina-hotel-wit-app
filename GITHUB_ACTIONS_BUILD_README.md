# 🤖 GitHub Actions - Android APK Build

هذا الـ workflow يقوم ببناء APK للتطبيق تلقائياً عند كل push أو pull request.

## 🚀 المشغلات التلقائية (Triggers)

### 1. عند Push إلى البرانشات:
- `main` - البرانش الرئيسي
- `develop` - برانش التطوير  
- `feature/**` - جميع برانشات الميزات
- `bugfix/**` - جميع برانشات إصلاح الأخطاء

### 2. عند Pull Request إلى:
- `main`
- `develop`

### 3. تشغيل يدوي:
- يمكن تشغيل الـ workflow يدوياً من تبويب Actions

## 🛠️ بيئة البناء

- **نظام التشغيل**: Ubuntu Latest
- **Java**: الإصدار 17 (Temurin Distribution)
- **Android SDK**: API Level 33
- **أدوات البناء**: Gradle مع التخزين المؤقت المُحسن

## 📱 مخرجات البناء

### APK Debug Files:
- **الموقع**: `mobile/android/app/build/outputs/apk/debug/`
- **النوع**: Debug APK (غير موقع)
- **اسم الـ Artifact**: `android-debug`
- **مدة الحفظ**: 30 يوم

## 📥 كيفية تحميل الـ APK

1. اذهب إلى تبويب **Actions** في الريبوزتوري
2. اضغط على آخر workflow run ناجح
3. انتقل إلى قسم **Artifacts** في أسفل الصفحة
4. اضغط على **android-debug** لتحميل الملف

## 🔄 خطوات البناء

### المرحلة الأولى: الإعداد
1. **Checkout Code**: سحب أحدث كود من الريبوزتوري
2. **Setup Java 17**: تثبيت Java 17
3. **Setup Android SDK**: تثبيت Android SDK API 33
4. **Accept Licenses**: قبول تراخيص Android SDK
5. **Install SDK Tools**: تثبيت أدوات SDK الضرورية

### المرحلة الثانية: التخزين المؤقت
6. **Setup Gradle Cache**: تفعيل التخزين المؤقت لـ Gradle لتسريع البناء

### المرحلة الثالثة: البناء
7. **Make Gradlew Executable**: جعل ملف gradlew قابل للتنفيذ
8. **Clean Build**: تنظيف البناء السابق
9. **Build Debug APK**: بناء الـ APK (المحاولة الأولى)
10. **Build Debug APK (Retry)**: إعادة المحاولة مع تحديث Dependencies عند الفشل

### المرحلة الرابعة: التحقق والرفع
11. **Verify Build Success**: التحقق من نجاح البناء
12. **Upload APK**: رفع الـ APK كـ artifact
13. **Display Build Information**: عرض معلومات البناء

## 🎯 استراتيجية التعامل مع الأخطاء

### المحاولة الأولى:
```bash
./gradlew clean assembleDebug --stacktrace --no-daemon
```

### عند الفشل - المحاولة الثانية:
```bash  
./gradlew assembleDebug --refresh-dependencies --stacktrace --no-daemon
```

## ⚡ تحسينات الأداء

### 1. التخزين المؤقت للـ Gradle:
- مجلدات `~/.gradle/caches` و `~/.gradle/wrapper`
- مفتاح التخزين المؤقت يعتمد على ملفات `build.gradle` و `gradle.properties`
- يتم إبطال التخزين المؤقت عند تغيير هذه الملفات

### 2. خيارات Gradle المُحسنة:
- `--stacktrace`: عرض تفاصيل الأخطاء
- `--no-daemon`: منع استخدام Gradle daemon لتوفير الذاكرة
- `--refresh-dependencies`: تحديث Dependencies عند الحاجة

## 🔍 استكشاف الأخطاء

### إذا فشل البناء:
1. تحقق من logs الـ workflow في GitHub Actions
2. تأكد من صحة ملفات `build.gradle`
3. تحقق من توفر جميع Dependencies
4. تأكد من أن الكود يتم compile محلياً

### مشاكل شائعة:
- **مفاتيح API مفقودة**: تأكد من وجود ملفات الإعدادات
- **مشاكل Dependencies**: سيتم حلها تلقائياً بخاصية retry
- **مشاكل الذاكرة**: الـ workflow مُحسن لتجنب هذه المشاكل

## 📊 معلومات إضافية

- **مدة البناء المتوقعة**: 3-7 دقائق (حسب حجم التغييرات والتخزين المؤقت)
- **حجم الـ APK المتوقع**: 15-50 MB (حسب المحتوى)
- **صلاحية الـ Artifact**: 30 يوم من تاريخ البناء

## 🚀 للمطورين

### تشغيل البناء محلياً:
```bash
cd mobile/android
./gradlew clean assembleDebug
```

### التحقق من الـ APK محلياً:
```bash
ls -la mobile/android/app/build/outputs/apk/debug/
```

---

**ملاحظة**: هذا الـ workflow يبني Debug APK فقط. للإنتاج، ستحتاج إلى إعداد منفصل مع التوقيع والتحسين.