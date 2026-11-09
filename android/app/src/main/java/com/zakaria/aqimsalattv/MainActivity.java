package com.zakaria.aqimsalattv;

import android.app.AlarmManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "com.aqimsalattv/alarm";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
            .setMethodCallHandler((call, result) -> {
                if ("canScheduleExactAlarms".equals(call.method)) {
                    AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    boolean allowed = Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                        || manager.canScheduleExactAlarms();
                    result.success(allowed);
                } else if ("openExactAlarmSettings".equals(call.method)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                    result.success(null);
                } else if ("schedulePrayerAlarms".equals(call.method)) {
                    result.error("NOT_IMPLEMENTED", "Alarm scheduling bridge is added in the next implementation stage.", null);
                } else {
                    result.notImplemented();
                }
            });
    }
}
