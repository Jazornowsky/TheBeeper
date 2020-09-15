package net.wojdat.damian.thebeeper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.annotation.Nullable;

public class BeeperBeepService extends Service {

    private MediaPlayer mp;
    private AudioManager audioManager;
    private int beepVolume;
    private ArrayList<String> enabledHours;
    private boolean isLedNotificationEnabled;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Beeper.LOG_TAG, "BeeperBeepService onStartCommand.");
        beepVolume = intent.getIntExtra(
                Beeper.PREF_BEEP_VOLUME,
                Beeper.DEFAULT_BEEP_VOLUME);
        if (beepVolume == audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
            beepVolume = beepVolume / 2;
        } else {
            beepVolume = (audioManager.getStreamMaxVolume(
                    AudioManager.STREAM_MUSIC) * beepVolume) / 100;
        }
        enabledHours = intent.getStringArrayListExtra(Beeper.PREF_ENABLED_HOURS);
        LocalDateTime now = LocalDateTime.now();
        Log.d(Beeper.LOG_TAG,
                "Enabled hours contains current hour (" + now.getHour() + "): " +
                        enabledHours.contains(String.valueOf(now.getHour())));
        if (enabledHours.contains(String.valueOf(now.getHour()))) {
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, beepVolume, 0);
            mp.start();
        }
        queBeepingService();
        return START_NOT_STICKY;
    }

    private void queBeepingService() {
        GregorianCalendar current = new GregorianCalendar();
        current.add(Calendar.MINUTE, 1);
        LocalDateTime now = LocalDateTime.now();
        current.set(now.getYear(), now.getMonth().getValue() - 1, now.getDayOfMonth(), now.getHour(), 0, 0);
        current.add(GregorianCalendar.HOUR, 1);
        LocalDateTime nextBeepTime = LocalDateTime.from(current.toZonedDateTime());
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent beeperBeepServicePendingIntent = PendingIntent
                .getService(getApplication().getApplicationContext(), 0, getBeeperBeepServiceIntent(), 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, current.getTimeInMillis(), beeperBeepServicePendingIntent);
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

    private Intent getBeeperBeepServiceIntent() {
        Intent beeperBeepServiceIntent = new Intent(getApplicationContext(), BeeperBeepService.class);
        beeperBeepServiceIntent.putExtra(
                Beeper.PREF_BEEP_VOLUME,
                beepVolume);
        beeperBeepServiceIntent.putStringArrayListExtra(
                Beeper.PREF_ENABLED_HOURS,
                enabledHours);
        return beeperBeepServiceIntent;
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
