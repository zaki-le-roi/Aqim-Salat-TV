import 'package:flutter/services.dart';

import '../models/prayer_alarm_spec.dart';

class PrayerAlarmBridge {
  PrayerAlarmBridge._();

  static const MethodChannel _channel = MethodChannel('com.aqimsalattv/alarm');

  static Future<bool> canScheduleExactAlarms() async {
    return await _channel.invokeMethod<bool>('canScheduleExactAlarms') ?? false;
  }

  static Future<void> openExactAlarmSettings() {
    return _channel.invokeMethod<void>('openExactAlarmSettings');
  }

  static Future<int> schedulePrayerAlarms(List<PrayerAlarmSpec> alarms) async {
    final result = await _channel.invokeMethod<int>(
      'schedulePrayerAlarms',
      {'alarms': alarms.map((alarm) => alarm.toPlatformMap()).toList()},
    );
    return result ?? 0;
  }

  static Future<void> cancelPrayerAlarms() {
    return _channel.invokeMethod<void>('cancelPrayerAlarms');
  }
}
