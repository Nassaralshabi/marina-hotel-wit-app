import 'package:flutter_riverpod/flutter_riverpod.dart';

// 1. نموذج بيانات المستخدم (User Model)
class User {
  final String username;
  final String name;
  final String userType; // 'admin' أو 'employee'

  User({required this.username, required this.name, required this.userType});
}

// 2. كلاس حالة المصادقة (AuthState)
class AuthState {
  final bool isAuthenticated;
  final String? error;
  final User? currentUser;

  const AuthState(
    this.isAuthenticated, {
    this.error,
    this.currentUser,
  });
}

// 3. AuthNotifier
class AuthNotifier extends StateNotifier<AuthState> {
  AuthNotifier() : super(const AuthState(false));

  Future<void> login(String username, String password) async {
    // --- التحقق فقط من admin / 1234 ---
    if (username == 'admin' && password == '1234') {
      final adminUser = User(
        username: 'admin',
        name: 'مدير النظام',
        userType: 'admin',
      );
      state = AuthState(true, currentUser: adminUser);
      return;
    }

    // --- بيانات خاطئة ---
    state = const AuthState(false, error: 'اسم المستخدم أو كلمة المرور غير صحيحة');
  }

  Future<void> logout() async {
    state = const AuthState(false, currentUser: null);
  }
}

// 4. مزود Riverpod
final authProvider =
    StateNotifierProvider<AuthNotifier, AuthState>((ref) => AuthNotifier());
