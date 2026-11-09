package com.zakaria.aqimsalattv;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;

public class PrayerAudioService extends Service {
    private static final String CHANNEL_ID = "prayer_adhan";
    private static final int NOTIFICATION_ID = 7101;
    private MediaPlayer mediaPlayer;
    private ToneGenerator toneGenerator;
    private AudioManager audioManager;
    private AudioFocusRequest focusRequest;
    private PowerManager.WakeLock wakeLock;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = buildNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
        acquireWakeLock();
        requestAudioFocus();
        if (!playBundledAdhan()) playFallbackTone();
        return START_NOT_STICKY;
    }

    private boolean playBundledAdhan() {
        int resourceId = getResources().getIdentifier(
            "adhan_default", "raw", getPackageName());
        if (resourceId == 0) {
            resourceId = getResources().getIdentifier(
                "aqim_alert_tone", "raw", getPackageName());
        }
        if (resourceId == 0) return false;
        try {
            AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(attributes);
            try (AssetFileDescriptor descriptor = getResources().openRawResourceFd(resourceId)) {
                if (descriptor == null) return false;
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(),
                    descriptor.getStartOffset(), descriptor.getLength());
            }
            mediaPlayer.setOnCompletionListener(player -> stopSelf());
            mediaPlayer.setOnErrorListener((player, what, extra) -> {
                stopSelf();
                return true;
            });
            mediaPlayer.prepare();
            mediaPlayer.start();
            return true;
        } catch (Exception exception) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            return false;
        }
    }

    private void playFallbackTone() {
        toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 85);
        toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 2500);
        handler.postDelayed(this::stopSelf, 3000);
    }

    private void requestAudioFocus() {
        if (audioManager == null) return;
        AudioAttributes attributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(attributes)
                .setOnAudioFocusChangeListener(change -> { })
                .build();
            audioManager.requestAudioFocus(focusRequest);
        } else {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_ALARM,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }

    private void acquireWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "AqimSalatTV:Adhan");
            wakeLock.acquire(5 * 60 * 1000L);
        }
    }

    private Notification buildNotification() {
        Intent launch = getPackageManager().getLaunchIntentForPackage(getPackageName());
        PendingIntent contentIntent = launch == null ? null : PendingIntent.getActivity(
            this, 0, launch, PendingIntent.FLAG_UPDATE_CURRENT |
                (Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_IMMUTABLE : 0));
        Notification.Builder builder = Build.VERSION.SDK_INT >= 26
            ? new Notification.Builder(this, CHANNEL_ID)
            : new Notification.Builder(this);
        builder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Aqim Salat TV")
            .setContentText("Prayer time notification")
            .setCategory(Notification.CATEGORY_ALARM)
            .setPriority(Notification.PRIORITY_HIGH)
            .setOngoing(true);
        if (contentIntent != null) builder.setContentIntent(contentIntent);
        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Prayer alerts", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Scheduled prayer alerts and adhan playback");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (toneGenerator != null) {
            toneGenerator.stopTone();
            toneGenerator.release();
            toneGenerator = null;
        }
        if (audioManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && focusRequest != null) {
            audioManager.abandonAudioFocusRequest(focusRequest);
        }
        if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
