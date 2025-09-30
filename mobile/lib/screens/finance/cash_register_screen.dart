import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../components/app_scaffold.dart';
import '../../services/providers.dart';
import '../../services/local_db.dart';

class CashRegisterScreen extends ConsumerStatefulWidget {
  const CashRegisterScreen({super.key});
  @override
  ConsumerState<CashRegisterScreen> createState() => _CashRegisterScreenState();
}

class _CashRegisterScreenState extends ConsumerState<CashRegisterScreen> {
  CashRegisterData? _register;

  @override
  void initState() {
    super.initState();
    Future.microtask(() async {
      final row = await ref.read(cashRegisterRepoProvider).openTodayIfNeeded();
      setState(() => _register = row);
    });
  }

  @override
  Widget build(BuildContext context) {
    final repo = ref.watch(cashRegisterRepoProvider);
    final registerStream = ref.watch(todayOpenRegisterProvider);
    return AppScaffold(
      title: 'سجل الصندوق',
      body: registerStream.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, st) => Center(child: Text('خطأ: $e')),
        data: (row) {
          final r = row ?? _register;
          if (r == null) return const Center(child: CircularProgressIndicator());
          final current = (r.openingBalance + r.totalIncome - r.totalExpense);
          return Column(
            children: [
              Padding(
                padding: const EdgeInsets.all(12),
                child: Row(
                  children: [
                    _metric('الرصيد الافتتاحي', r.openingBalance, Colors.grey),
                    const SizedBox(width: 8),
                    _metric('الإيرادات', r.totalIncome, Colors.green),
                    const SizedBox(width: 8),
                    _metric('المصروفات', r.totalExpense, Colors.red),
                    const SizedBox(width: 8),
                    _metric('الرصيد الحالي', current, Colors.blue),
                  ],
                ),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 12),
                child: Row(
                  children: [
                    Expanded(
                      child: ElevatedButton.icon(
                        onPressed: () => _addTransaction(context, r.id),
                        icon: const Icon(Icons.add),
                        label: const Text('إضافة حركة'),
                      ),
                    ),
                    const SizedBox(width: 8),
                    OutlinedButton.icon(
                      onPressed: r.status == 'open' ? () => _closeRegister(context, r) : null,
                      icon: const Icon(Icons.lock),
                      label: const Text('إغلاق اليوم'),
                    ),
                  ],
                ),
              ),
              const Divider(),
              Expanded(
                child: StreamBuilder<List<CashTransaction>>(
                  stream: ref.watch(cashRepoProvider).dao.watchList(registerId: r.id),
                  builder: (context, snapshot) {
                    if (!snapshot.hasData) return const Center(child: CircularProgressIndicator());
                    final list = snapshot.data!;
                    if (list.isEmpty) return const Center(child: Text('لا توجد حركات'));
                    return ListView.separated(
                      itemCount: list.length,
                      separatorBuilder: (_, __) => const Divider(height: 1),
                      itemBuilder: (c, i) {
                        final t = list[i];
                        final sign = t.transactionType == 'income' ? '+' : '-';
                        final color = t.transactionType == 'income' ? Colors.green : Colors.red;
                        return ListTile(
                          leading: Icon(t.transactionType == 'income' ? Icons.call_received : Icons.call_made, color: color),
                          title: Text(t.description ?? ''),
                          subtitle: Text('${t.referenceType ?? ''}${t.referenceId != null ? ' #${t.referenceId}' : ''} • ${t.transactionTime.substring(11,16)}'),
                          trailing: Text('$sign ${t.amount.toStringAsFixed(2)}', style: TextStyle(color: color, fontWeight: FontWeight.bold)),
                        );
                      },
                    );
                  },
                ),
              ),
            ],
          );
        },
      ),
    );
  }

  Widget _metric(String title, double v, Color c) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(border: Border.all(color: c.withOpacity(.3)), borderRadius: BorderRadius.circular(8)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(title, style: TextStyle(fontSize: 12, color: c.withOpacity(.8))),
          const SizedBox(height: 4),
          Text(v.toStringAsFixed(2), style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: c)),
        ]),
      ),
    );
  }

  Future<void> _addTransaction(BuildContext context, int registerId) async {
    String type = 'income';
    final amount = TextEditingController();
    String refType = 'other';
    final refId = TextEditingController();
    final desc = TextEditingController();
    final ok = await showDialog<bool>(
      context: context,
      builder: (ctx) => Directionality(
        textDirection: TextDirection.rtl,
        child: StatefulBuilder(
          builder: (ctx, setD) => AlertDialog(
            title: const Text('إضافة حركة'),
            content: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  DropdownButtonFormField<String>(
                    value: type,
                    decoration: const InputDecoration(labelText: 'النوع'),
                    items: const [
                      DropdownMenuItem(value: 'income', child: Text('إيراد')),
                      DropdownMenuItem(value: 'expense', child: Text('مصروف')),
                    ],
                    onChanged: (v) => setD(() => type = v ?? 'income'),
                  ),
                  const SizedBox(height: 8),
                  TextField(controller: amount, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: 'المبلغ')),
                  const SizedBox(height: 8),
                  DropdownButtonFormField<String>(
                    value: refType,
                    decoration: const InputDecoration(labelText: 'نوع المرجع'),
                    items: const [
                      DropdownMenuItem(value: 'booking', child: Text('حجز')),
                      DropdownMenuItem(value: 'restaurant', child: Text('مطعم')),
                      DropdownMenuItem(value: 'service', child: Text('خدمة')),
                      DropdownMenuItem(value: 'salary', child: Text('راتب')),
                      DropdownMenuItem(value: 'utility', child: Text('فاتورة')),
                      DropdownMenuItem(value: 'purchase', child: Text('مشتريات')),
                      DropdownMenuItem(value: 'other', child: Text('أخرى')),
                    ],
                    onChanged: (v) => setD(() => refType = v ?? 'other'),
                  ),
                  const SizedBox(height: 8),
                  TextField(controller: refId, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: 'رقم المرجع (اختياري)')),
                  const SizedBox(height: 8),
                  TextField(controller: desc, decoration: const InputDecoration(labelText: 'الوصف')),
                ],
              ),
            ),
            actions: [
              TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('إلغاء')),
              FilledButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('إضافة')),
            ],
          ),
        ),
      ),
    );
    if (ok != true) return;
    final amt = double.tryParse(amount.text) ?? 0;
    if (amt <= 0) return;
    await ref.read(cashRegisterRepoProvider).addTransaction(
          registerId: registerId,
          type: type,
          amount: amt,
          referenceType: refType,
          referenceId: int.tryParse(refId.text),
          description: desc.text.trim(),
        );
  }

  Future<void> _closeRegister(BuildContext context, CashRegisterData r) async {
    final closing = TextEditingController(text: (r.openingBalance + r.totalIncome - r.totalExpense).toStringAsFixed(2));
    final notes = TextEditingController();
    final ok = await showDialog<bool>(
      context: context,
      builder: (ctx) => Directionality(
        textDirection: TextDirection.rtl,
        child: AlertDialog(
          title: const Text('إغلاق الصندوق'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(controller: closing, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: 'الرصيد الختامي')),
              const SizedBox(height: 8),
              TextField(controller: notes, decoration: const InputDecoration(labelText: 'ملاحظات')),
            ],
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('إلغاء')),
            FilledButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('إغلاق')),
          ],
        ),
      ),
    );
    if (ok != true) return;
    await ref.read(cashRegisterRepoProvider).closeRegister(r.id, double.tryParse(closing.text) ?? 0, notes: notes.text.trim());
  }
}
