class Env {
  static String baseApiUrl = const String.fromEnvironment(
    'BASE_API_URL',
    defaultValue: 'http://hotelmarina.com/MARINA_HOTEL_PORTABLE/api/v1',
  );
  
  // إعدادات بديلة للتطوير المحلي
  static String get localApiUrl => 'https://localhost/MARINA_HOTEL_PORTABLE/api/v1';
  static String get testApiUrl => 'https://192.168.1.100/MARINA_HOTEL_PORTABLE/api/v1';
  
  // إعدادات المهلة الزمنية
  static const Duration connectTimeout = Duration(seconds: 15);
  static const Duration receiveTimeout = Duration(seconds: 20);
  
  // إعدادات التوثيق
  static const String tokenKey = 'auth_token';
  static const String userDataKey = 'user_data';
  
  // معلومات التطبيق
  static const String appName = 'Marina Hotel';
  static const String appVersion = '1.0.0';
}
