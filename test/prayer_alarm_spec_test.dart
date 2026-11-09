import 'package:aqim_salat_tv/core/models/prayer_alarm_spec.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('serializes city wall time without a device offset', () {
    const alarm = PrayerAlarmSpec(
      localDateTime: _FixedDateTime(),
      zoneId: 'Africa/Algiers',
      prayer: 'Fajr',
      mosque: 'Test Mosque',
    );
    expect(alarm.localIso8601, '2026-11-09T05:12:00');
    expect(alarm.toPlatformMap()['zoneId'], 'Africa/Algiers');
  });
}

class _FixedDateTime extends DateTime {
  const _FixedDateTime() : super(2026, 11, 9, 5, 12);
}
