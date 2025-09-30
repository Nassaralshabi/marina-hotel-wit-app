import 'package:drift/drift.dart' as d;
import '../local_db.dart';
import '../daos/outbox_dao.dart';
import '../daos/employees_dao.dart';

class EmployeesRepository {
  EmployeesRepository(this.db)
      : outbox = OutboxDao(db),
        dao = EmployeesDao(db, OutboxDao(db));
  final AppDatabase db;
  final OutboxDao outbox;
  final EmployeesDao dao;

  Stream<List<Employee>> watchAll({String? search}) => dao.watchList(search: search);
  Stream<Employee?> watchOne(int id) => dao.watchById(id);

  Future<int> create({
    required String name,
    required double salary,
    required String status,
    String position = 'موظف',
    String phone = '',
    String hireDate = '',
  }) =>
      dao.insertOne(EmployeesCompanion(
        name: d.Value(name),
        basicSalary: d.Value(salary),
        position: d.Value(position),
        phone: d.Value(phone),
        hireDate: d.Value(hireDate),
        status: d.Value(status),
      ));

  Future<int> update(
    int id, {
    String? name,
    double? salary,
    String? status,
    String? position,
    String? phone,
    String? hireDate,
  }) =>
      dao.updateById(
        id,
        EmployeesCompanion(
          name: name != null ? d.Value(name) : const d.Value.absent(),
          basicSalary: salary != null ? d.Value(salary) : const d.Value.absent(),
          status: status != null ? d.Value(status) : const d.Value.absent(),
          position: position != null ? d.Value(position) : const d.Value.absent(),
          phone: phone != null ? d.Value(phone) : const d.Value.absent(),
          hireDate: hireDate != null ? d.Value(hireDate) : const d.Value.absent(),
        ),
      );

  Future<int> delete(int id) => dao.softDelete(id);
}
