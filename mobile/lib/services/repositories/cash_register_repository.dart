import 'package:drift/drift.dart' as d;
import '../local_db.dart';
import '../daos/outbox_dao.dart';
import '../daos/cash_register_dao.dart';
import '../daos/cash_transactions_dao.dart';
import '../../utils/time.dart';

class CashRegisterRepository {
  CashRegisterRepository(this.db)
      : outbox = OutboxDao(db),
        registerDao = CashRegisterDao(db, OutboxDao(db)),
        cashDao = CashTransactionsDao(db, OutboxDao(db));

  final AppDatabase db;
  final OutboxDao outbox;
  final CashRegisterDao registerDao;
  final CashTransactionsDao cashDao;

  Stream<CashRegisterData?> watchTodayOpen() => registerDao.watchOpenForToday();

  Future<CashRegisterData> openTodayIfNeeded() async {
    final today = Time.safeIsoToDateString(Time.nowIso());
    final existing = await registerDao.getByDate(today);
    if (existing != null && existing.status == 'open') return existing;

    double opening = 0;
    final last = await (db.customSelect('SELECT closing_balance AS cb FROM cash_register WHERE status = "closed" ORDER BY date DESC LIMIT 1').get());
    if (last.isNotEmpty) {
      final cb = last.first.data['cb'];
      if (cb is num) opening = cb.toDouble();
      if (cb is String) opening = double.tryParse(cb) ?? 0;
    }
    final id = await registerDao.insertOne(CashRegisterCompanion(
      date: d.Value(today),
      openingBalance: d.Value(opening),
      status: const d.Value('open'),
    ));
    final row = await registerDao.getById(id);
    return row!;
  }

  Future<void> closeRegister(int id, double closingBalance, {String? notes}) async {
    await registerDao.updateById(id, CashRegisterCompanion(
      closingBalance: d.Value(closingBalance),
      status: const d.Value('closed'),
      notes: d.Value(notes),
    ));
  }

  Stream<List<CashTransaction>> watchTransactions({int? registerId}) => cashDao.watchList(includeDeleted: false);

  Future<int> addTransaction({required int registerId, required String type, required double amount, String? referenceType, int? referenceId, String? description, String? atIso}) async {
    final txTime = atIso ?? Time.nowIso();
    return cashDao.insertOne(CashTransactionsCompanion(
      registerId: d.Value(registerId),
      transactionType: d.Value(type),
      amount: d.Value(amount),
      referenceType: d.Value(referenceType),
      referenceId: d.Value(referenceId),
      description: d.Value(description),
      transactionTime: d.Value(txTime),
    ));
  }
}
