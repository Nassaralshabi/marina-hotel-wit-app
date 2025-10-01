import 'package:intl/intl.dart';

/// كلاس مركزي لتنسيق الأرقام في تطبيق فندق مارينا
/// يوحد طريقة عرض الأرقام كأرقام صحيحة بدون رموز عملة
class CurrencyFormatter {
  // تنسيق الأرقام العربية
  static final NumberFormat _arabicFormat = NumberFormat.decimalPattern('ar');
  
  /// تنسيق المبلغ كرقم صحيح بدون رمز عملة
  /// [amount] المبلغ المراد تنسيقه (double أو int)
  static String formatAmount(num amount) {
    final intAmount = amount.round();
    return _arabicFormat.format(intAmount);
  }
  
  /// تنسيق المبلغ من double إلى رقم صحيح
  /// [amount] المبلغ كـ double
  static String formatFromDouble(double amount) {
    return formatAmount(amount);
  }
  
  /// تنسيق المبلغ من int
  /// [amount] المبلغ كـ int
  static String formatFromInt(int amount) {
    return _arabicFormat.format(amount);
  }
  
  /// تحويل NumberFormat إلى الدالة المركزية (للتوافق العكسي)
  /// يتم استخدامها لاستبدال استخدامات NumberFormat الموجودة
  static String formatLegacy(double amount) {
    return formatAmount(amount);
  }
  
  /// تحويل double إلى int مع التقريب
  static int doubleToInt(double amount) {
    return amount.round();
  }
}