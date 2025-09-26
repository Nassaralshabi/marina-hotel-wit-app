# 🤖 GitHub Actions - Marina Hotel Mobile

هذا المجلد يحتوي على workflows تلقائية لبناء ونشر تطبيق Marina Hotel Mobile مع نظام المدفوعات المتقدم.

## 📋 الـ Workflows المتاحة

### 1. 🔨 Build APK (`build-apk.yml`)
**المُحفز**: Push, PR, تشغيل يدوي
**الوظيفة**: بناء APK تلقائياً مع فحص الجودة

#### المراحل:
- ✅ إعداد Flutter و Java
- ✅ تنظيف وتثبيت dependencies  
- ✅ تشغيل code generation
- ✅ بناء APK Debug & Release
- ✅ رفع الملفات كـ artifacts
- ✅ إنشاء release تلقائي (للفروع الرئيسية)
- ✅ تعليق على PR مع معلومات البناء

#### الاستخدام:
```bash
# تلقائياً عند Push أو PR
git push origin main

# يدوياً من GitHub
Actions → Build Marina Hotel APK → Run workflow
```

### 2. 🚀 Release APK (`release-apk.yml`) 
**المُحفز**: إنشاء Tag أو تشغيل يدوي
**الوظيفة**: إنشاء إصدار إنتاجي كامل

#### المراحل:
- ✅ بناء APK محسّن للإنتاج
- ✅ بناء App Bundle (AAB) للـ Play Store
- ✅ بناء لمعماريات متعددة
- ✅ إنشاء release notes تفصيلي
- ✅ رفع الملفات مع checksums
- ✅ إنشاء GitHub Release

#### الاستخدام:
```bash
# إنشاء tag لإطلاق release
git tag v1.0.0
git push origin v1.0.0

# أو يدوياً
Actions → Release Marina Hotel APK → Run workflow
```

### 3. 🔍 PR Check (`pr-check.yml`)
**المُحفز**: Pull Request
**الوظيفة**: فحص جودة الكود قبل الدمج

#### المراحل:
- ✅ تحليل الكود مع flutter analyze
- ✅ حساب إحصائيات التغييرات
- ✅ اختبار البناء
- ✅ تعليق تلقائي مع النتائج

#### المميزات:
- فحص سريع للأخطاء
- إحصائيات مفيدة للمراجعة
- تعليق تلقائي محدّث
- منع دمج الكود المكسور

## 🎯 سيناريوهات الاستخدام

### للتطوير اليومي:
1. **إنشاء PR** ← يُشغل `pr-check.yml`
2. **مراجعة التعليق التلقائي** للتأكد من جودة الكود
3. **دمج PR** ← يُشغل `build-apk.yml`
4. **تحميل APK** من Artifacts للاختبار

### للإصدارات:
1. **إنهاء التطوير** وجعل الكود جاهز
2. **إنشاء Tag** مع رقم الإصدار
3. **انتظار `release-apk.yml`** لإنهاء البناء
4. **تحميل الملفات** من GitHub Releases
5. **نشر على Play Store** باستخدام AAB

## 📁 الملفات المُنتجة

### Build APK:
```
artifacts/
├── marina-hotel-debug-{hash}.apk      # للاختبار السريع
├── marina-hotel-release-{hash}.apk    # للنشر العادي  
├── marina-hotel-latest.apk            # آخر إصدار
└── build_info.txt                     # معلومات البناء
```

### Release APK:
```
release/
├── marina-hotel-release.apk           # APK موحد
├── marina-hotel-release.aab           # للـ Play Store
├── app-arm64-v8a-release.apk         # ARM64 فقط
├── app-armeabi-v7a-release.apk       # ARMv7 فقط  
├── app-x86_64-release.apk            # x86_64 فقط
├── release_notes.md                   # ملاحظات الإصدار
└── checksums.txt                      # للتحقق من سلامة الملفات
```

## ⚙️ الإعدادات والتخصيص

### متغيرات البيئة:
```yaml
env:
  FLUTTER_VERSION: '3.22.0'  # يمكن تغييرها حسب الحاجة
```

### تخصيص التشغيل:
- **الفروع**: يمكن تعديل الفروع المُستهدفة في `on.push.branches`
- **المسارات**: تحديد مسارات معينة في `on.paths`
- **التوقيت**: إضافة `schedule` للتشغيل الدوري

### إضافة secrets (إذا لزم):
```bash
# للتوقيع المتقدم أو النشر التلقائي
Settings → Secrets and variables → Actions
- ANDROID_KEYSTORE
- KEYSTORE_PASSWORD  
- PLAY_STORE_API_KEY
```

## 🚀 مميزات متقدمة

### 1. Parallel Jobs:
- فحص سريع + بناء متوازي
- توفير الوقت والموارد

### 2. Smart Caching:
- حفظ Flutter dependencies
- تسريع البناءات المتتالية

### 3. Multi-Architecture:
- دعم ARM64, ARMv7, x86_64
- ملفات محسّنة لكل نوع جهاز

### 4. Quality Gates:
- منع الدمج إذا فشل البناء
- فحص تلقائي للأخطاء

## 📊 الإحصائيات والتقارير

### في كل build:
- عدد الملفات المتغيرة
- حجم APK النهائي
- وقت البناء
- نتائج تحليل الكود

### في كل release:
- مقارنة مع الإصدار السابق
- قائمة التحسينات
- معلومات التوافق

## 🛠️ استكشاف الأخطاء

### مشاكل شائعة:
1. **Flutter version mismatch**: تحديث `FLUTTER_VERSION`
2. **Build dependencies**: تشغيل `flutter clean`
3. **Code generation**: فحص drift models
4. **Permission denied**: فحص secrets والـ tokens

### سجلات مفيدة:
- **Actions tab**: سجل مفصل لكل workflow
- **Step summary**: ملخص سريع للنتائج  
- **Artifacts**: الملفات المُنتجة للتحميل

## 📞 الدعم

لأي مشاكل في GitHub Actions:
1. فحص **Actions tab** للسجلات
2. مراجعة **Step summary** للأخطاء
3. إنشاء **Issue** مع تفاصيل الخطأ

---

🤖 **هذه Workflows تجعل تطوير ونشر Marina Hotel Mobile أوتوماتيكي بالكامل!**