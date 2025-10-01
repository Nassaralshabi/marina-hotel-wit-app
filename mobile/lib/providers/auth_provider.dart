import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../services/api_service.dart';

class AuthState {
  final bool isAuthenticated;
  final bool isLoading;
  final String? error;
  final Map<String, dynamic>? user;
  
  const AuthState({
    required this.isAuthenticated,
    this.isLoading = false,
    this.error,
    this.user,
  });
  
  AuthState copyWith({
    bool? isAuthenticated,
    bool? isLoading,
    String? error,
    Map<String, dynamic>? user,
  }) {
    return AuthState(
      isAuthenticated: isAuthenticated ?? this.isAuthenticated,
      isLoading: isLoading ?? this.isLoading,
      error: error,
      user: user ?? this.user,
    );
  }
}

class AuthNotifier extends StateNotifier<AuthState> {
  AuthNotifier() : super(const AuthState(isAuthenticated: false));

  Future<void> login(String username, String password) async {
    if (username.trim().isEmpty || password.trim().isEmpty) {
      state = state.copyWith(error: 'يرجى إدخال اسم المستخدم وكلمة المرور');
      return;
    }
    
    state = state.copyWith(isLoading: true, error: null);
    
    try {
      final result = await ApiService.I.loginWithDetails(username, password);
      if (result['success'] == true) {
        state = AuthState(
          isAuthenticated: true,
          isLoading: false,
          user: result['user'],
        );
      } else {
        state = state.copyWith(
          isAuthenticated: false,
          isLoading: false,
          error: result['error'] ?? 'بيانات الدخول غير صحيحة',
        );
      }
    } catch (e) {
      String errorMessage = 'حدث خطأ غير متوقع';
      
      if (e is DioException) {
        switch (e.type) {
          case DioExceptionType.connectionTimeout:
          case DioExceptionType.sendTimeout:
          case DioExceptionType.receiveTimeout:
            errorMessage = 'انتهت مهلة الاتصال. تحقق من اتصال الإنترنت';
            break;
          case DioExceptionType.connectionError:
            errorMessage = 'لا يمكن الاتصال بالخادم. تحقق من اتصال الإنترنت';
            break;
          case DioExceptionType.badResponse:
            if (e.response?.statusCode == 401) {
              errorMessage = 'اسم المستخدم أو كلمة المرور غير صحيحة';
            } else if (e.response?.statusCode == 500) {
              errorMessage = 'خطأ في الخادم. حاول لاحقاً';
            } else {
              errorMessage = 'حدث خطأ في الاتصال (${e.response?.statusCode})';
            }
            break;
          default:
            errorMessage = 'حدث خطأ في الشبكة';
        }
      }
      
      state = state.copyWith(
        isAuthenticated: false,
        isLoading: false,
        error: errorMessage,
      );
    }
  }

  Future<void> logout() async {
    state = state.copyWith(isLoading: true);
    try {
      await ApiService.I.logout();
      state = const AuthState(isAuthenticated: false, isLoading: false);
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'حدث خطأ أثناء تسجيل الخروج',
      );
    }
  }
  
  Future<void> checkAuthStatus() async {
    state = state.copyWith(isLoading: true);
    try {
      final isLoggedIn = await ApiService.I.ping();
      state = state.copyWith(
        isAuthenticated: isLoggedIn,
        isLoading: false,
      );
    } catch (e) {
      state = state.copyWith(
        isAuthenticated: false,
        isLoading: false,
      );
    }
  }
  
  void clearError() {
    state = state.copyWith(error: null);
  }
}

final authProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) => AuthNotifier());
