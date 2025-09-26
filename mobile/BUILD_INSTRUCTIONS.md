# تعليمات بناء تطبيق Marina Hotel Mobile مع نظام المدفوعات المتقدم

## المتطلبات
- Flutter SDK 3.22.0 أو أحدث
- Dart SDK 3.3.0 أو أحدث
- Android Studio أو VS Code
- Android SDK (API Level 21 أو أعلى)

## خطوات البناء

### 1. تحضير البيئة
```bash
# تحقق من إصدار Flutter
flutter --version

# تحقق من وجود جميع المتطلبات
flutter doctor
```

### 2. تثبيت Dependencies
```bash
cd mobile
flutter pub get
```

### 3. إنشاء ملفات قاعدة البيانات المطلوبة
```bash
# تشغيل build runner لإنشاء ملفات Drift
flutter packages pub run build_runner build
```

### 4. بناء APK للتطوير
```bash
flutter build apk --debug
```

### 5. بناء APK للإنتاج
```bash
flutter build apk --release
```

## المميزات الجديدة في نظام المدفوعات

### ✅ الميزات المضافة:
1. **نماذج بيانات شاملة للمدفوعات** (`lib/models/payment_models.dart`)
   - دعم 5 طرق دفع: نقدي، بطاقة ائتمانية، تحويل بنكي، شيك، تقسيط
   - 4 حالات للمدفوعات: في الانتظار، مكتمل، فشل، مسترد
   - نماذج Receipt و Invoice مع إنشاء PDF

2. **شاشات المدفوعات المتقدمة**:
   - `BookingPaymentScreen`: معالجة مدفوعات الحجز الواحد
   - `PaymentHistoryScreen`: سجل المدفوعات مع فلترة
   - `PaymentsMainScreen`: الشاشة الرئيسية لإدارة المدفوعات
   - `BookingCheckoutScreen`: تسجيل المغادرة وإنهاء الحجز

3. **مكونات واجهة المستخدم** (`lib/components/widgets/payment_widgets.dart`):
   - `PaymentSummaryWidget`: عرض ملخص مدفوعات الحجز
   - `PaymentCard`: بطاقة تفاصيل الدفعة
   - `PaymentStatusBadge`: شارة حالة الدفعة
   - مكونات أخرى للواجهة

4. **تحسين شاشة الحجوزات** (`lib/screens/bookings/bookings_list.dart`):
   - تصميم جديد بنظام البطاقات
   - أزرار مدفوعات مدمجة
   - زر تسجيل مغادرة للحجوزات النشطة
   - ربط مباشر بنظام المدفوعات

### 🔧 Dependencies المضافة:
```yaml
pdf: ^3.11.0           # لإنشاء ملفات PDF
printing: ^5.12.0      # للطباعة
path_provider: ^2.1.3  # للوصول لمجلدات النظام
```

## بنية المشروع الجديدة

```
mobile/lib/
├── models/
│   └── payment_models.dart          # نماذج بيانات المدفوعات
├── screens/
│   ├── bookings/
│   │   └── bookings_list.dart       # (محسّن) قائمة الحجوزات
│   └── payments/                    # مجلد شاشات المدفوعات الجديد
│       ├── booking_payment_screen.dart
│       ├── payment_history_screen.dart  
│       ├── payments_main_screen.dart
│       └── booking_checkout_screen.dart
└── components/widgets/
    └── payment_widgets.dart         # مكونات واجهة المدفوعات
```

## اختبار النظام

### 1. اختبار أساسي
```bash
# تشغيل التطبيق على محاكي أو جهاز
flutter run
```

### 2. اختبار المميزات:
- ✅ الدخول للحجوزات ومشاهدة التصميم الجديد
- ✅ النقر على زر "دفع" لحجز معين
- ✅ اختبار طرق الدفع المختلفة
- ✅ إنشاء إيصال PDF
- ✅ مشاهدة سجل المدفوعات
- ✅ تجربة تسجيل المغادرة

## ملاحظات مهمة

### ⚠️ تحذيرات:
1. **أذونات Android**: قد تحتاج إضافة أذونات في `android/app/src/main/AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

2. **إعدادات PDF**: للطباعة على أجهزة حقيقية، تأكد من توصيل طابعة أو حفظ كملف

3. **قاعدة البيانات**: النظام يستخدم بيانات تجريبية حالياً، يحتاج ربط مع Backend

### 🔄 التطوير المستقبلي:
- ربط مع API الخاص بالخادم
- إضافة نظام إشعارات للمدفوعات
- تحسين تصميم الإيصالات
- إضافة تصدير Excel للتقارير

## إصلاح المشاكل الشائعة

### خطأ في build_runner:
```bash
flutter packages pub run build_runner clean
flutter packages pub run build_runner build --delete-conflicting-outputs
```

### خطأ في PDF:
- تأكد من إضافة أذونات الكتابة
- اختبر على جهاز حقيقي للطباعة

### خطأ في Navigation:
- تأكد من أن جميع الشاشات مستوردة بشكل صحيح
- فحص أسماء المسارات

---

## التواصل والدعم
للمساعدة في حل المشاكل أو تطوير المميزات الإضافية، يمكن الاتصال بفريق التطوير.

**آخر تحديث**: ديسمبر 2024
**الإصدار**: 1.0.0+1 مع نظام المدفوعات المتقدم