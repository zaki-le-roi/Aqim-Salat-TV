class PrayerAlarmSpec {
  const PrayerAlarmSpec({
    required this.localDateTime,
    required this.zoneId,
    required this.prayer,
    this.mosque = '',
  });

  /// Local wall-clock time in the selected city's IANA timezone.
  /// Do not pass a UTC timestamp here: Java resolves DST using [zoneId].
  final DateTime localDateTime;
  final String zoneId;
  final String prayer;
  final String mosque;

  String get localIso8601 {
    String two(int value) => value.toString().padLeft(2, '0');
    return '${localDateTime.year.toString().padLeft(4, '0')}-'
        '${two(localDateTime.month)}-${two(localDateTime.day)}T'
        '${two(localDateTime.hour)}:${two(localDateTime.minute)}:${two(localDateTime.second)}';
  }

  Map<String, Object> toPlatformMap() => {
        'localDateTime': localIso8601,
        'zoneId': zoneId,
        'prayer': prayer,
        'mosque': mosque,
      };
}
