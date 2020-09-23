package net.wojdat.damian.thebeeper.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import net.wojdat.damian.thebeeper.Beeper;
import net.wojdat.damian.thebeeper.R;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.annotation.Nullable;

public class BeeperBeepService extends Service {

    private MediaPlayer mp;
    private AudioManager audioManager;
    private int volume;
    private int systemStreamVolume;
    private ArrayList<String> enabledHours;
    private boolean isLedNotificationEnabled;

    public static Intent getBeeperBeepServiceIntent(Context context,
                                                    Integer volume,
                                                    ArrayList<String> enabledHours) {
        Intent beeperBeepServiceIntent = new Intent(context, BeeperBeepService.class);
        beeperBeepServiceIntent.putExtra(
                Beeper.PREF_BEEP_VOLUME,
                volume);
        beeperBeepServiceIntent.putStringArrayListExtra(
                Beeper.PREF_ENABLED_HOURS,
                enabledHours);
        Log.d(Beeper.LOG_TAG, "getBeeperBeepServiceIntent " +
                "volume: " + volume + ", " +
                "enabledHours: " + enabledHours.toString());
        return beeperBeepServiceIntent;
    }

    public static PendingIntent getBeeperBeepServicePendingIntent(Context context,
                                                                  Integer volume,
                                                                  ArrayList<String> enabledHours) {
        return PendingIntent
                .getService(context,
                        0,
                        getBeeperBeepServiceIntent(
                                context,
                                volume,
                                enabledHours
                        ),
                        0);
    }

    public static void queBeepingService(Context context,
                                         PendingIntent beeperBeepServicePendingIntent,
                                         Integer volume,
                                         ArrayList<String> enabledHours) {
        GregorianCalendar current = new GregorianCalendar();
        current.add(Calendar.MINUTE, 1);
        LocalDateTime now = LocalDateTime.now();
        current.set(now.getYear(), now.getMonth().getValue() - 1, now.getDayOfMonth(), now.getHour(), 0, 0);
        current.add(GregorianCalendar.HOUR, 1);
        LocalDateTime nextBeepTime = LocalDateTime.from(current.toZonedDateTime());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (beeperBeepServicePendingIntent != null) {
            beeperBeepServicePendingIntent.cancel();
        }
        beeperBeepServicePendingIntent =
                getBeeperBeepServicePendingIntent(context, volume, enabledHours);
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                current.getTimeInMillis(),
                beeperBeepServicePendingIntent);
        Log.d(Beeper.LOG_TAG, "Current time: " +
                now.getHour() + ":" +
                now.getMinute() + ":" +
                now.getSecond());
        Log.d(Beeper.LOG_TAG, "Next beep time: " +
                nextBeepTime.getHour() + ":" +
                nextBeepTime.getMinute() + ":" +
                nextBeepTime.getSecond());
        Log.d(Beeper.LOG_TAG, "Beeping service qued.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Beeper.LOG_TAG, "BeeperBeepService onStartCommand.");
        loadPreferences(intent);
        LocalDateTime now = LocalDateTime.now();
        if (enabledHours.contains(String.valueOf(now.getHour()))) {
            systemStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, systemStreamVolume, 0);
                }
            });
        }
        queBeepingService(getApplicationContext(),
                null,
                volume,
                enabledHours);
        return START_NOT_STICKY;
    }

    private void loadPreferences(Intent intent) {
        volume = intent.getIntExtra(
                Beeper.PREF_BEEP_VOLUME,
                Beeper.DEFAULT_BEEP_VOLUME);
        if (volume == audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
            volume = volume / 2;
        } else {
            volume = (audioManager.getStreamMaxVolume(
                    AudioManager.STREAM_MUSIC) * volume) / 100;
        }
        enabledHours = intent.getStringArrayListExtra(
                Beeper.PREF_ENABLED_HOURS);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mp = MediaPlayer.create(getApplicationContext(), R.raw.beep);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
