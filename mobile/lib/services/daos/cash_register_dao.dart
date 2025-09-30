import 'package:drift/drift.dart';
import '../../utils/id.dart';
import '../../utils/time.dart';
import '../local_db.dart';
import 'outbox_dao.dart';

part 'cash_register_dao.g.dart';

@DriftAccessor(tables: [CashRegister])
class CashRegisterDao extends DatabaseAccessor<AppDatabase> with _$CashRegisterDaoMixin {
  CashRegisterDao(AppDatabase db, this.outboxDao) : super(db);
  final OutboxDao outboxDao;

  Future<CashRegisterData?> getById(int id) => (select(cashRegister)..where((t) => t.id.equals(id))).getSingleOrNull();
  Stream<CashRegisterData?> watchById(int id) => (select(cashRegister)..where((t) => t.id.equals(id))).watchSingleOrNull();

  Future<CashRegisterData?> getByDate(String date) => (select(cashRegister)..where((t) => t.date.equals(date) & t.deletedAt.isNull())).getSingleOrNull();
  Stream<CashRegisterData?> watchOpenForToday() {
    final today = Time.safeIsoToDateString(Time.nowIso());
    final q = select(cashRegister)..where((t) => t.date.equals(today) & t.status.equals('open') & t.deletedAt.isNull());
    return q.watchSingleOrNull();
  }

  Future<int> insertOne(CashRegisterCompanion data, {bool originIsServer = false}) async {
    final now = Time.nowEpoch();
    final uu = data.localUuid.present ? data.localUuid.value : IdGen.uuid();
    final comp = data.copyWith(
      localUuid: Value(uu),
      createdAt: Value(now),
      updatedAt: Value(now),
      lastModified: Value(now),
      origin: Value(originIsServer ? 'server' : 'local'),
    );
    final id = await into(cashRegister).insert(comp);
    if (!originIsServer) {
      await outboxDao.merge(
        entity: 'cash_register',
        op: 'create',
        localUuid: uu,
        serverId: comp.serverId.present ? comp.serverId.value : null,
        payload: _payloadFrom(comp),
        clientTs: now,
      );
    }
    return id;
  }

  Future<int> updateById(int id, CashRegisterCompanion data, {bool originIsServer = false}) async {
    final now = Time.nowEpoch();
    final existing = await getById(id);
    if (existing == null) return 0;
    final comp = data.copyWith(updatedAt: Value(now), lastModified: Value(now));
    final rows = await (update(cashRegister)..where((t) => t.id.equals(id))).write(comp);
    if (rows > 0 && !originIsServer) {
      await outboxDao.merge(
        entity: 'cash_register',
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
    final rows = await (update(cashRegister)..where((t) => t.id.equals(id))).write(CashRegisterCompanion(
      deletedAt: Value(now),
      updatedAt: Value(now),
      lastModified: Value(now),
    ));
    if (rows > 0 && !originIsServer) {
      await outboxDao.merge(
        entity: 'cash_register',
        op: 'delete',
        localUuid: existing.localUuid,
        serverId: existing.serverId,
        payload: {'id': id},
        clientTs: now,
      );
    }
    return rows;
  }

  Map<String, dynamic> _payloadFrom(CashRegisterCompanion comp, {CashRegisterData? base}) {
    final m = <String, dynamic>{};
    if (comp.date.present) m['date'] = comp.date.value;
    if (comp.openingBalance.present) m['opening_balance'] = comp.openingBalance.value;
    if (comp.totalIncome.present) m['total_income'] = comp.totalIncome.value;
    if (comp.totalExpense.present) m['total_expense'] = comp.totalExpense.value;
    if (comp.closingBalance.present) m['closing_balance'] = comp.closingBalance.value;
    if (comp.status.present) m['status'] = comp.status.value;
    if (comp.notes.present) m['notes'] = comp.notes.value;
    return m;
  }
}
