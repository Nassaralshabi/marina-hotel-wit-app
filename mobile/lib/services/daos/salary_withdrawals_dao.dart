import 'package:drift/drift.dart';
import '../../utils/id.dart';
import '../../utils/time.dart';
import '../local_db.dart';
import 'outbox_dao.dart';

part 'salary_withdrawals_dao.g.dart';

@DriftAccessor(tables: [SalaryWithdrawals])
class SalaryWithdrawalsDao extends DatabaseAccessor<AppDatabase> with _$SalaryWithdrawalsDaoMixin {
  SalaryWithdrawalsDao(AppDatabase db, this.outboxDao) : super(db);
  final OutboxDao outboxDao;

  Future<List<SalaryWithdrawal>> list({int? employeeId, String? from, String? to, bool includeDeleted = false}) async {
    final q = select(salaryWithdrawals);
    if (!includeDeleted) q.where((t) => t.deletedAt.isNull());
    if (employeeId != null) q.where((t) => t.employeeId.equals(employeeId));
    if (from != null && to != null) q.where((t) => t.date.isBiggerOrEqualValue(from) & t.date.isSmallerOrEqualValue(to));
    q.orderBy([(t) => OrderingTerm(expression: t.date, mode: OrderingMode.desc)]);
    return q.get();
  }

  Stream<List<SalaryWithdrawal>> watchList({int? employeeId, bool includeDeleted = false}) {
    final q = select(salaryWithdrawals);
    if (!includeDeleted) q.where((t) => t.deletedAt.isNull());
    if (employeeId != null) q.where((t) => t.employeeId.equals(employeeId));
    q.orderBy([(t) => OrderingTerm(expression: t.date, mode: OrderingMode.desc)]);
    return q.watch();
  }

  Future<SalaryWithdrawal?> getById(int id) => (select(salaryWithdrawals)..where((t) => t.id.equals(id))).getSingleOrNull();
  Stream<SalaryWithdrawal?> watchById(int id) => (select(salaryWithdrawals)..where((t) => t.id.equals(id))).watchSingleOrNull();

  Future<int> insertOne(SalaryWithdrawalsCompanion data, {bool originIsServer = false}) async {
    final now = Time.nowEpoch();
    final uu = data.localUuid.present ? data.localUuid.value : IdGen.uuid();
    final comp = data.copyWith(
      localUuid: Value(uu),
      createdAt: Value(now),
      updatedAt: Value(now),
      lastModified: Value(now),
      origin: Value(originIsServer ? 'server' : 'local'),
    );
    final id = await into(salaryWithdrawals).insert(comp);
    if (!originIsServer) {
      await outboxDao.merge(
        entity: 'salary_withdrawals',
        op: 'create',
        localUuid: uu,
        serverId: comp.serverId.present ? comp.serverId.value : null,
        payload: _payloadFrom(comp),
        clientTs: now,
      );
    }
    return id;
  }

  Future<int> updateById(int id, SalaryWithdrawalsCompanion data, {bool originIsServer = false}) async {
    final now = Time.nowEpoch();
    final existing = await getById(id);
    if (existing == null) return 0;
    final comp = data.copyWith(updatedAt: Value(now), lastModified: Value(now));
    final rows = await (update(salaryWithdrawals)..where((t) => t.id.equals(id))).write(comp);
    if (rows > 0 && !originIsServer) {
      await outboxDao.merge(
        entity: 'salary_withdrawals',
        op: 'update',
        localUuid: existing.localUuid,
        serverId: existing.serverId,
        payload: _payloadFrom(comp, base: existing),
        clientTs: now,
      );
    }
    return rows;
  }

  Future<int> softDelete(int id, {bool originIsServer = false}) async {
    final now = Time.nowEpoch();
    final existing = await getById(id);
    if (existing == null) return 0;
    final rows = await (update(salaryWithdrawals)..where((t) => t.id.equals(id))).write(SalaryWithdrawalsCompanion(
      deletedAt: Value(now),
      updatedAt: Value(now),
      lastModified: Value(now),
    ));
    if (rows > 0 && !originIsServer) {
      await outboxDao.merge(
        entity: 'salary_withdrawals',
        op: 'delete',
        localUuid: existing.localUuid,
        serverId: existing.serverId,
        payload: {'id': id},
        clientTs: now,
      );
    }
    return rows;
  }

  Map<String, dynamic> _payloadFrom(SalaryWithdrawalsCompanion comp, {SalaryWithdrawal? base}) {
    final m = <String, dynamic>{};
    if (comp.employeeId.present) m['employee_id'] = comp.employeeId.value;
    if (comp.amount.present) m['amount'] = comp.amount.value;
    if (comp.date.present) m['date'] = comp.date.value;
    if (comp.notes.present) m['notes'] = comp.notes.value;
    if (comp.withdrawalType.present) m['withdrawal_type'] = comp.withdrawalType.value;
    if (comp.cashTransactionId.present) m['cash_transaction_id'] = comp.cashTransactionId.value;
    if (comp.createdBy.present) m['created_by'] = comp.createdBy.value;
    return m;
  }
}
