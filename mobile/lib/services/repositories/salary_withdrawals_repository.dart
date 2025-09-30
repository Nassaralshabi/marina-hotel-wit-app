import 'package:drift/drift.dart' as d;
import '../local_db.dart';
import '../daos/outbox_dao.dart';
import '../daos/salary_withdrawals_dao.dart';

class SalaryWithdrawalsRepository {
  SalaryWithdrawalsRepository(this.db)
      : outbox = OutboxDao(db),
        dao = SalaryWithdrawalsDao(db, OutboxDao(db));
  final AppDatabase db;
  final OutboxDao outbox;
  final SalaryWithdrawalsDao dao;

  Stream<List<SalaryWithdrawal>> watchAll({int? employeeId}) => dao.watchList(employeeId: employeeId);
  Stream<SalaryWithdrawal?> watchOne(int id) => dao.watchById(id);

  Future<int> create({required int employeeId, required double amount, required String date, String? notes, String withdrawalType = 'cash', int? cashTransactionId, int? createdBy}) => dao.insertOne(
        SalaryWithdrawalsCompanion(
          employeeId: d.Value(employeeId),
          amount: d.Value(amount),
          date: d.Value(date),
          notes: d.Value(notes),
          withdrawalType: d.Value(withdrawalType),
          cashTransactionId: d.Value(cashTransactionId),
          createdBy: d.Value(createdBy),
        ),
      );

  Future<int> update(int id, {int? employeeId, double? amount, String? date, String? notes, String? withdrawalType, int? cashTransactionId, int? createdBy}) => dao.updateById(
        id,
        SalaryWithdrawalsCompanion(
          employeeId: d.Value(employeeId),
          amount: amount != null ? d.Value(amount) : const d.Value.absent(),
          date: date != null ? d.Value(date) : const d.Value.absent(),
          notes: d.Value(notes),
          withdrawalType: withdrawalType != null ? d.Value(withdrawalType) : const d.Value.absent(),
          cashTransactionId: d.Value(cashTransactionId),
          createdBy: d.Value(createdBy),
        ),
      );

  Future<int> delete(int id) => dao.softDelete(id);

  Future<double> monthlyWithdrawnAmount(int employeeId, DateTime month) async {
    final from = DateTime(month.year, month.month, 1);
    final to = DateTime(month.year, month.month + 1, 0);
    final rows = await (db.customSelect(
      'SELECT COALESCE(SUM(amount),0) AS s FROM salary_withdrawals WHERE employee_id = ? AND date BETWEEN ? AND ? AND deleted_at IS NULL',
      variables: [
        d.Variable.withInt(employeeId),
        d.Variable.withString('${from.toIso8601String().substring(0, 10)}'),
        d.Variable.withString('${to.toIso8601String().substring(0, 10)}'),
      ],
    ).get());
    if (rows.isEmpty) return 0;
    final v = rows.first.data['s'];
    if (v == null) return 0;
    if (v is num) return v.toDouble();
    return double.tryParse(v.toString()) ?? 0;
  }
}
