import 'package:flutter_test/flutter_test.dart';
import 'package:marina_hotel/utils/currency_formatter.dart';

void main() {
  group('CurrencyFormatter', () {
    test('formatAmount should format numbers without currency symbols', () {
      expect(CurrencyFormatter.formatAmount(150.75), equals('151'));
      expect(CurrencyFormatter.formatAmount(1000.0), equals('1,000'));
      expect(CurrencyFormatter.formatAmount(150), equals('150'));
    });

    test('formatFromDouble should convert double to integer format', () {
      expect(CurrencyFormatter.formatFromDouble(150.75), equals('151'));
      expect(CurrencyFormatter.formatFromDouble(1000.50), equals('1,001'));
      expect(CurrencyFormatter.formatFromDouble(999.25), equals('999'));
    });

    test('formatFromInt should format integers with Arabic numbering', () {
      expect(CurrencyFormatter.formatFromInt(150), equals('150'));
      expect(CurrencyFormatter.formatFromInt(1000), equals('1,000'));
      expect(CurrencyFormatter.formatFromInt(2500), equals('2,500'));
    });

    test('formatLegacy should work for backward compatibility', () {
      expect(CurrencyFormatter.formatLegacy(150.75), equals('151'));
      expect(CurrencyFormatter.formatLegacy(1000.0), equals('1,000'));
    });

    test('doubleToInt should properly round double values', () {
      expect(CurrencyFormatter.doubleToInt(150.75), equals(151));
      expect(CurrencyFormatter.doubleToInt(150.25), equals(150));
      expect(CurrencyFormatter.doubleToInt(150.50), equals(151));
    });

    test('formatAmount should handle edge cases', () {
      expect(CurrencyFormatter.formatAmount(0), equals('0'));
      expect(CurrencyFormatter.formatAmount(0.49), equals('0'));
      expect(CurrencyFormatter.formatAmount(0.51), equals('1'));
    });
  });
}