import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:drift/drift.dart' as d;
import '../../components/app_scaffold.dart';
import '../../services/providers.dart';
import '../../services/sync_service.dart';
import '../../services/local_db.dart';
import '../../services/api_service.dart';
import '../../utils/time.dart';

class ExpensesListScreen extends ConsumerStatefulWidget {
  const ExpensesListScreen({super.key});
  @override
  ConsumerState<ExpensesListScreen> createState() => _ExpensesListScreenState();
}

class _ExpensesListScreenState extends ConsumerState<ExpensesListScreen> {
  String? _typeFilter;
  DateTime? _from;
  DateTime? _to;

  @override
  Widget build(BuildContext context) {
    final repo = ref.watch(expensesRepoProvider);
    return AppScaffold(
      title: 'المصروفات',
      actions: [
        IconButton(onPressed: () => ref.read(syncServiceProvider).runSync(), icon: const Icon(Icons.sync)),
        IconButton(onPressed: () => _edit(context), icon: const Icon(Icons.add)),
      ],
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(12),
            child: Row(
              children: [
                Expanded(
                  child: InkWell(
                    onTap: () async {
                      final picked = await showDateRangePicker(
                        context: context,
                        firstDate: DateTime(2020),
                        lastDate: DateTime(2100),
                        initialDateRange: _from != null && _to != null ? DateTimeRange(start: _from!, end: _to!) : null,
                      );
                      if (picked != null) setState(() { _from = picked.start; _to = picked.end; });
                    },
                    child: InputDecorator(
                      decoration: const InputDecoration(labelText: 'الفترة'),
                      child: Text(_from == null || _to == null ? 'حدد الفترة' : '${_fmtDate(_from!)} → ${_fmtDate(_to!)}'),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: FutureBuilder<List<String>>(
                    future: _loadExpenseTypes(ref),
                    builder: (c, s) {
                      final items = s.data ?? const <String>[];
                      return DropdownButtonFormField<String>(
                        value: _typeFilter,
                        decoration: const InputDecoration(labelText: 'النوع'),
                        items: [const DropdownMenuItem(value: null, child: Text('الكل')),
                          ...items.map((t) => DropdownMenuItem(value: t, child: Text(t)))],
                        onChanged: (v) => setState(() => _typeFilter = v),
                      );
                    },
                  ),
                ),
              ],
            ),
          ),
          Expanded(
            child: StreamBuilder<List<Expense>>(
              stream: repo.watchAll(),
              builder: (context, snapshot) {
                if (!snapshot.hasData) return const Center(child: CircularProgressIndicator());
                var list = snapshot.data!;
                if (_typeFilter != null && _typeFilter!.isNotEmpty) list = list.where((e) => e.expenseType == _typeFilter).toList();
                if (_from != null && _to != null) {
                  final f = _fmtDate(_from!);
                  final t = _fmtDate(_to!);
                  list = list.where((e) => e.date.compareTo(f) >= 0 && e.date.compareTo(t) <= 0).toList();
                }
                list.sort((a,b) => b.date.compareTo(a.date));
                return ListView.separated(
                  itemCount: list.length,
                  separatorBuilder: (_, __) => const Divider(height: 1),
                  itemBuilder: (c, i) {
                    final e = list[i];
                    return ListTile(
                      title: Text(e.description),
                      subtitle: Text('${e.expenseType} • ${e.date}${e.relatedId != null ? ' • #${e.relatedId}' : ''}'),
                      trailing: Text(e.amount.toStringAsFixed(2)),
                      onTap: () => _edit(context, existing: e),
                    );
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _edit(BuildContext context, {Expense? existing}) async {
    final description = TextEditingController(text: existing?.description ?? '');
    final amount = TextEditingController(text: existing?.amount.toString() ?? '');
    String expenseType = existing?.expenseType ?? 'other';
    int? relatedId = existing?.relatedId;
    final date = TextEditingController(text: existing?.date ?? Time.nowDateString());

    final employeesAsync = ref.read(employeesListProvider);
    final suppliers = await _loadSuppliers();
    final types = await _loadExpenseTypes(ref);

    final ok = await showDialog<bool>(
      context: context,
      builder: (ctx) => Directionality(
        textDirection: TextDirection.rtl,
        child: StatefulBuilder(
          builder: (ctx, setD) => AlertDialog(
            title: Text(existing == null ? 'إضافة مصروف' : 'تعديل مصروف'),
            content: SingleChildScrollView(
              child: Column(mainAxisSize: MainAxisSize.min, children: [
                TextField(controller: description, decoration: const InputDecoration(labelText: 'الوصف')),
                const SizedBox(height: 8),
                TextField(controller: amount, decoration: const InputDecoration(labelText: 'المبلغ'), keyboardType: TextInputType.number),
                const SizedBox(height: 8),
                DropdownButtonFormField<String>(
                  value: expenseType,
                  decoration: const InputDecoration(labelText: 'النوع'),
                  items: types.map((t) => DropdownMenuItem(value: t, child: Text(t))).toList(),
                  onChanged: (v) { setD(() { expenseType = v ?? 'other'; relatedId = null; }); },
                ),
                const SizedBox(height: 8),
                if (expenseType == 'salary')
                  employeesAsync.when(
                    data: (emps) => DropdownButtonFormField<int>(
                      value: relatedId,
                      decoration: const InputDecoration(labelText: 'الموظف'),
                      items: emps.map((e) => DropdownMenuItem(value: e.id, child: Text(e.name))).toList(),
                      onChanged: (v) => setD(() => relatedId = v),
                    ),
                    loading: () => const SizedBox(height: 48, child: Center(child: CircularProgressIndicator())),
                    error: (e, st) => Text('خطأ $e'),
                  ),
                if (expenseType == 'purchase')
                  DropdownButtonFormField<int>(
                    value: relatedId,
                    decoration: const InputDecoration(labelText: 'المورد'),
                    items: suppliers.map((s) => DropdownMenuItem(value: s['id'] as int, child: Text(s['name'] as String? ?? ''))).toList(),
                    onChanged: (v) => setD(() => relatedId = v),
                  ),
                const SizedBox(height: 8),
                TextField(controller: date, decoration: const InputDecoration(labelText: 'التاريخ YYYY-MM-DD')),
              ]),
            ),
            actions: [TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('إلغاء')), FilledButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('حفظ'))],
          ),
        ),
      ),
    );
    if (ok != true) return;

    final repo = ref.read(expensesRepoProvider);
    if (existing == null) {
      await repo.create(expenseType: expenseType.trim(), relatedId: relatedId, description: description.text.trim(), amount: double.tryParse(amount.text) ?? 0, date: date.text.trim());
    } else {
      await repo.update(existing.id, expenseType: expenseType.trim(), relatedId: relatedId, description: description.text.trim(), amount: double.tryParse(amount.text) ?? 0, date: date.text.trim());
    }
  }

  Future<List<String>> _loadExpenseTypes(WidgetRef ref) async {
    final list = await ref.read(expensesRepoProvider).watchAll().first;
    final set = <String>{'utilities','purchase','salary','other'};
    for (final e in list) { set.add(e.expenseType); }
    return set.toList();
  }

  Future<List<Map<String, dynamic>>> _loadSuppliers() async {
    final api = await ApiService.I.listEntity('suppliers');
    final data = List<Map<String, dynamic>>.from(api['data']['items'] ?? api['data'] ?? []);
    return data;
  }

  String _fmtDate(DateTime d) => d.toIso8601String().substring(0,10);
}
