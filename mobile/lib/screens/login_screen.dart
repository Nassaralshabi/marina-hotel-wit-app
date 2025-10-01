import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/auth_provider.dart';

class LoginScreen extends ConsumerStatefulWidget {
  const LoginScreen({super.key});
  @override
  ConsumerState<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends ConsumerState<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _userCtrl = TextEditingController();
  final _passCtrl = TextEditingController();
  bool _passwordVisible = false;
  
  @override
  void dispose() {
    _userCtrl.dispose();
    _passCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final auth = ref.watch(authProvider);
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        body: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 420),
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Form(
                    key: _formKey,
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Text('تسجيل الدخول', style: Theme.of(context).textTheme.titleLarge),
                        const SizedBox(height: 16),
                        TextFormField(
                          controller: _userCtrl,
                          decoration: const InputDecoration(labelText: 'اسم المستخدم'),
                          validator: (v) => (v == null || v.isEmpty) ? 'أدخل اسم المستخدم' : null,
                        ),
                        const SizedBox(height: 8),
                        TextFormField(
                          controller: _passCtrl,
                          obscureText: !_passwordVisible,
                          decoration: InputDecoration(
                            labelText: 'كلمة المرور',
                            suffixIcon: IconButton(
                              icon: Icon(
                                _passwordVisible ? Icons.visibility : Icons.visibility_off,
                              ),
                              onPressed: () {
                                setState(() {
                                  _passwordVisible = !_passwordVisible;
                                });
                              },
                            ),
                          ),
                          validator: (v) {
                            if (v == null || v.isEmpty) {
                              return 'أدخل كلمة المرور';
                            }
                            if (v.length < 3) {
                              return 'كلمة المرور يجب أن تكون 3 أحرف على الأقل';
                            }
                            return null;
                          },
                          onFieldSubmitted: (_) => _handleLogin(),
                        ),
                        const SizedBox(height: 16),
                        if (auth.error != null) ...[
                          Container(
                            padding: const EdgeInsets.all(12),
                            decoration: BoxDecoration(
                              color: Colors.red.shade50,
                              border: Border.all(color: Colors.red.shade200),
                              borderRadius: BorderRadius.circular(8),
                            ),
                            child: Row(
                              children: [
                                Icon(Icons.error_outline, color: Colors.red.shade700),
                                const SizedBox(width: 8),
                                Expanded(
                                  child: Text(
                                    auth.error!,
                                    style: TextStyle(color: Colors.red.shade700, fontSize: 14),
                                  ),
                                ),
                                IconButton(
                                  icon: const Icon(Icons.close, size: 18),
                                  onPressed: () => ref.read(authProvider.notifier).clearError(),
                                  color: Colors.red.shade700,
                                ),
                              ],
                            ),
                          ),
                          const SizedBox(height: 8),
                        ],
                        
                        SizedBox(
                          width: double.infinity,
                          child: FilledButton(
                            onPressed: auth.isLoading ? null : _handleLogin,
                            style: FilledButton.styleFrom(
                              padding: const EdgeInsets.symmetric(vertical: 16),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(8),
                              ),
                            ),
                            child: auth.isLoading
                                ? const SizedBox(
                                    width: 20,
                                    height: 20,
                                    child: CircularProgressIndicator(
                                      strokeWidth: 2,
                                      valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                                    ),
                                  )
                                : const Text('تسجيل الدخول', style: TextStyle(fontSize: 16)),
                          ),
                        ),
                        
                        const SizedBox(height: 16),
                        
                        // زر اختبار الاتصال
                        TextButton(
                          onPressed: auth.isLoading ? null : _testConnection,
                          child: const Text('اختبار الاتصال مع الخادم'),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
  
  void _handleLogin() async {
    if (!_formKey.currentState!.validate()) return;
    
    // مسح الخطأ السابق قبل المحاولة الجديدة
    ref.read(authProvider.notifier).clearError();
    
    await ref.read(authProvider.notifier).login(
      _userCtrl.text.trim(),
      _passCtrl.text,
    );
  }
  
  void _testConnection() async {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('جاري اختبار الاتصال...'),
        duration: Duration(seconds: 2),
      ),
    );
    
    await ref.read(authProvider.notifier).checkAuthStatus();
    final auth = ref.read(authProvider);
    
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            auth.isAuthenticated
                ? 'تم الاتصال بنجاح مع الخادم'
                : 'فشل في الاتصال مع الخادم أو انتهت صلاحية الجلسة',
          ),
          backgroundColor: auth.isAuthenticated ? Colors.green : Colors.orange,
        ),
      );
    }
  }
}
