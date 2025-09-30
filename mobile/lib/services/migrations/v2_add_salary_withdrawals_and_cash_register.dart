import 'package:drift/drift.dart' as d;
import '../local_db.dart';
import '../../utils/time.dart';

Future<void> runV2Migration(AppDatabase db) async {
  // Drift MigrationStrategy already creates the new tables.
  // Optionally seed default expense categories if the app relies on them.
  final existingTypes = await db.customSelect(
    "SELECT DISTINCT expense_type AS t FROM expenses WHERE deleted_at IS NULL",
  ).get();
  final hasAny = existingTypes.isNotEmpty;
  if (!hasAny) {
    final now = Time.nowEpoch();
    await db.into(db.expenses).insert(ExpensesCompanion(
      expenseType: d.Value('utilities'),
      relatedId: const d.Value(null),
      description: d.Value('تهيئة فئات المصروفات'),
      amount: const d.Value(0),
      date: d.Value(Time.safeIsoToDateString(Time.nowIso())),
      localUuid: d.Value('seed-x-utilities'),
      createdAt: d.Value(now),
      updatedAt: d.Value(now),
      lastModified: d.Value(now),
      version: const d.Value(1),
      origin: const d.Value('local'),
    ));
    await db.into(db.expenses).insert(ExpensesCompanion(
      expenseType: d.Value('other'),
      relatedId: const d.Value(null),
      description: d.Value('تهيئة فئات المصروفات'),
      amount: const d.Value(0),
      date: d.Value(Time.safeIsoToDateString(Time.nowIso())),
      localUuid: d.Value('seed-x-other'),
      createdAt: d.Value(now),
      updatedAt: d.Value(now),
      lastModified: d.Value(now),
      version: const d.Value(1),
      origin: const d.Value('local'),
    ));
  }
}
