package com.zakaria.aqimsalattv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

final class AlarmScheduler {
    private static final String PREFS = "aqim_prayer_schedule";
    private static final String KEY_ALARMS = "alarms_json";

    private AlarmScheduler() { }

    static int replaceSchedule(Context context, JSONArray alarms) throws Exception {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) {
            throw new SecurityException("Exact alarm permission is not granted");
        }
        cancelStored(context);
        JSONArray valid = new JSONArray();
        int scheduled = 0;
        for (int i = 0; i < alarms.length(); i++) {
            JSONObject alarm = alarms.optJSONObject(i);
            if (alarm == null) continue;
            String localDateTime = alarm.optString("localDateTime", "");
            String zoneId = alarm.optString("zoneId", "");
            String prayer = alarm.optString("prayer", "");
            if (localDateTime.isEmpty() || zoneId.isEmpty() || prayer.isEmpty()) continue;
            int requestCode = requestCode(localDateTime, zoneId, prayer);
            alarm.put("requestCode", requestCode);
            valid.put(alarm);
            if (scheduleOne(context, alarm)) scheduled++;
        }
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_ALARMS, valid.toString()).apply();
        return scheduled;
    }

    static void restore(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String raw = preferences.getString(KEY_ALARMS, "[]");
        try {
            JSONArray alarms = new JSONArray(raw);
            for (int i = 0; i < alarms.length(); i++) {
                JSONObject alarm = alarms.optJSONObject(i);
                if (alarm != null) scheduleOne(context, alarm);
            }
        } catch (Exception ignored) {
            // Corrupt cached schedule is ignored; Dart will replace it on next launch.
        }
    }

    static void cancelStored(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String raw = preferences.getString(KEY_ALARMS, "[]");
        try {
            JSONArray alarms = new JSONArray(raw);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            for (int i = 0; i < alarms.length(); i++) {
                JSONObject alarm = alarms.optJSONObject(i);
                if (alarm == null) continue;
                int requestCode = alarm.optInt("requestCode", -1);
                if (requestCode >= 0) {
                    PendingIntent pending = pendingIntent(context, alarm, requestCode);
                    if (pending != null) manager.cancel(pending);
                }
            }
        } catch (Exception ignored) { }
        preferences.edit().remove(KEY_ALARMS).apply();
    }

    private static boolean scheduleOne(Context context, JSONObject alarm) {
        try {
            String localDateTime = alarm.getString("localDateTime");
            String zoneId = alarm.getString("zoneId");
            String prayer = alarm.optString("prayer", "Prayer");
            int requestCode = alarm.optInt("requestCode",
                requestCode(localDateTime, zoneId, prayer));
            long triggerAt = LocalDateTime.parse(localDateTime)
                .atZone(ZoneId.of(zoneId)).toInstant().toEpochMilli();
            if (triggerAt <= System.currentTimeMillis()) return false;
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) {
                return false;
            }
            PendingIntent pending = pendingIntent(context, alarm, requestCode);
            if (pending == null) return false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending);
            } else {
                manager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending);
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static PendingIntent pendingIntent(Context context, JSONObject alarm, int requestCode) {
        Intent intent = new Intent(context, PrayerAlarmReceiver.class);
        intent.putExtra("prayer", alarm.optString("prayer", "Prayer"));
        intent.putExtra("mosque", alarm.optString("mosque", ""));
        intent.putExtra("localDateTime", alarm.optString("localDateTime", ""));
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(context, requestCode, intent, flags);
    }

    private static int requestCode(String dateTime, String zone, String prayer) {
        return (dateTime + "|" + zone + "|" + prayer).hashCode() & 0x7fffffff;
    }
}
