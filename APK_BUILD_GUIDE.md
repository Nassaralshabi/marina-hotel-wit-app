# 🚀 دليل متابعة وتحميل الـ APK

## 📊 الحالة الحالية
✅ **3 workflows تعمل بنجاح:**
- 🔧 Android Debug Build
- 🚀 Android Release Build  
- 🚀 Marina Hotel APK Builder

⏱️ **الوقت المتوقع للإنجاز**: 5-8 دقائق إجمالي

---

## 🔍 كيفية المراقبة

### الطريقة 1: من خلال المتصفح
1. اذهب إلى: https://github.com/Nassaralshabi/marina-hotel-wit-app/actions
2. ستجد الـ runs قيد العمل مع أيقونة دوارة 🔄
3. عند الانتهاء ستظهر ✅ (نجح) أو ❌ (فشل)

### الطريقة 2: من خلال سطر الأوامر
```bash
gh run list --repo Nassaralshabi/marina-hotel-wit-app | head -5
```

---

## 📱 تحميل الـ APK عند الإنجاز

### الخطوات:
1. **اذهب لتبويب Actions** في الريبو
2. **اضغط على أي workflow مكتمل بنجاح** (✅ علامة خضراء)
3. **انتقل لأسفل الصفحة** لقسم "Artifacts"
4. **ابحث عن ملف مثل**:
   - `android-debug` 
   - `debug-apk`
   - `marina-hotel-debug`
5. **اضغط على الاسم** لتحميل الملف المضغوط
6. **استخرج الملف** وستجد ملف `.apk`

### ملاحظات مهمة:
- 📁 الملف المحمل سيكون مضغوط (ZIP)
- 📱 الـ APK بالداخل جاهز للتثبيت
- ⏰ الملفات متاحة لمدة 30 يوم
- 🔓 هذا Debug APK (غير موقع للاختبار)

---

## 🎯 الـ Workflows المتاحة

| الاسم | النوع | المخرج |
|-------|--------|---------|
| 🔧 Android Debug Build | Debug APK | للاختبار |
| 🚀 Android Release Build | Release APK | للإنتاج |
| 🚀 Marina Hotel APK Builder | Multi APK | شامل |

---

## 🆘 في حالة الفشل

### إذا ظهرت علامة ❌:
1. اضغط على الـ run الفاشل
2. اضغط على المرحلة الحمراء لرؤية الخطأ
3. انسخ رسالة الخطأ واطلب المساعدة

### أخطاء شائعة:
- **Dependencies error**: مشاكل في المكتبات
- **Build timeout**: الـ build يحتاج وقت أطول
- **Memory issues**: مشاكل في الذاكرة

---

## 📞 التواصل للمساعدة

إذا واجهت أي مشكلة:
1. انسخ رابط الـ workflow run
2. انسخ رسالة الخطأ إن وجدت  
3. اطلب المساعدة مع التفاصيل

---

## ⚡ أوامر سريعة للمتابعة

```bash
# فحص الحالة
gh run list --repo Nassaralshabi/marina-hotel-wit-app

# تفاصيل run محدد
gh run view <run-id> --repo Nassaralshabi/marina-hotel-wit-app

# تحميل artifacts
gh run download <run-id> --repo Nassaralshabi/marina-hotel-wit-app
```

---

🎉 **نصيحة**: احتفظ بهذا الدليل مرجعياً للمرات القادمة!