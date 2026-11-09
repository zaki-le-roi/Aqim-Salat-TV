package com.zakaria.aqimsalattv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Time and boot changes invalidate wall-clock alarm instants.
 * The Dart scheduler recalculates the rolling seven-day window on the next app start.
 * Exact prayer recomputation is intentionally not attempted in this receiver.
 */
public class BootTimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Keep the receiver lightweight. A later scheduler stage persists and restores
        // the next alarms from a timezone-aware schedule snapshot.
    }
}
