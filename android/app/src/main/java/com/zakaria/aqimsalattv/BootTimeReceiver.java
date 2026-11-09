package com.zakaria.aqimsalattv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootTimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent == null ? "" : intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
            || Intent.ACTION_TIME_CHANGED.equals(action)
            || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
            // Stored local wall times are converted again with their saved IANA city zone.
            AlarmScheduler.restore(context);
        }
    }
}
