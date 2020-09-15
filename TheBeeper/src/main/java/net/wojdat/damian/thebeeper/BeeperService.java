package net.wojdat.damian.thebeeper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.core.app.NotificationCompat;

/**
 * Created by 7 on 2015-02-26.
 */
public class BeeperService extends Service {

    private Beeper beeper;
    private AlarmManager alarmManager;
    private Boolean restart = Boolean.TRUE;
    private Boolean enabled;
    private Integer volume;
    private ArrayList<String> enabledHours;
    private PendingIntent beeperBeepServicePendingIntent;
    private Notification notification;

    public static Intent getBeeperServiceIntent(Context context,
                                                Boolean enabled,
                                                Integer volume,
                                                ArrayList<String> enabledHours) {
        Intent beeperBeepServiceIntent = new Intent(context, BeeperService.class);
        beeperBeepServiceIntent.putExtra(
                Beeper.PREF_ENABLED,
                enabled);
        beeperBeepServiceIntent.putExtra(
                Beeper.PREF_BEEP_VOLUME,
                volume);
        beeperBeepServiceIntent.putStringArrayListExtra(
                Beeper.PREF_ENABLED_HOURS,
                enabledHours);
        Log.d(Beeper.LOG_TAG, "getBeeperServiceIntent " +
                "enabled: " + enabled + ", " +
                "volume: " + volume + ", " +
                "enabledHours: " + enabledHours.toString());
        return beeperBeepServiceIntent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Beeper.LOG_TAG, "BeeperService onStartCommand.");
        /*notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.service_name))
                .setSmallIcon(R.drawable.watch04_stat_notify)
                .setAutoCancel(true)
                .setProgress(0, 0, false)
                .build();*/
        loadPreferences(intent);

        if (enabled) {
            queBeepingService();
            createNotification();
        } else {
            cancelBeepingService();
            deleteNotification();
        }
        return START_STICKY;
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void loadPreferences(Intent intent) {
        restart = intent.getBooleanExtra(
                Beeper.PREF_RESTART,
                Boolean.TRUE);
        enabled = intent.getBooleanExtra(
                Beeper.PREF_ENABLED,
                Beeper.DEFAULT_BEEPER_ENABLED);
        volume = intent.getIntExtra(
                Beeper.PREF_BEEP_VOLUME,
                Beeper.DEFAULT_BEEP_VOLUME);
        enabledHours = intent.getStringArrayListExtra(
                Beeper.PREF_ENABLED_HOURS);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Beeper.LOG_TAG, "Beeper service onCreate.");

        createNotification();

        beeper = (Beeper) getApplication();
    }

    private void createNotification() {
        Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
        PendingIntent settingsPendingIntent = PendingIntent
                .getActivity(getApplicationContext(), 0, settingsIntent, 0);
        notification = new NotificationCompat.Builder(this,
                createNotificationChannel("my_service", "The Beeper background service"))
                .setContentTitle(getString(R.string.service_name))
                .setSmallIcon(R.drawable.watch04_star_white_test4)
                .setAutoCancel(true)
                .setProgress(0, 0, false)
                .setContentText("The Beeper is active.")
                .setContentIntent(settingsPendingIntent)
                .setShowWhen(false)
                .build();
        startForeground(1, notification);
    }

    private void deleteNotification() {
        getNotificationManager().deleteNotificationChannel(notification.getChannelId());
    }

    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    private void queBeepingService() {
        GregorianCalendar current = new GregorianCalendar();
        current.add(Calendar.MINUTE, 1);
        LocalDateTime now = LocalDateTime.now();
        current.set(now.getYear(), now.getMonth().getValue() - 1, now.getDayOfMonth(), now.getHour(), 0, 0);
        current.add(GregorianCalendar.HOUR, 1);
        LocalDateTime nextBeepTime = LocalDateTime.from(current.toZonedDateTime());
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (beeperBeepServicePendingIntent != null) {
            beeperBeepServicePendingIntent.cancel();
        }
        beeperBeepServicePendingIntent = getBeeperBeepServiceIntent();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
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

    private PendingIntent getBeeperBeepServiceIntent() {
        return PendingIntent
                .getService(beeper.getApplicationContext(),
                        0,
                        getBeeperBeepServiceIntent(
                                getApplicationContext(),
                                volume,
                                enabledHours
                        ),
                        0);
    }

    private Intent getBeeperBeepServiceIntent(Context context,
                                              Integer volume,
                                              ArrayList<String> enabledHours) {
        Intent beeperBeepServiceIntent = new Intent(context, BeeperBeepService.class);
        beeperBeepServiceIntent.putExtra(
                Beeper.PREF_BEEP_VOLUME,
                volume);
        beeperBeepServiceIntent.putStringArrayListExtra(
                Beeper.PREF_ENABLED_HOURS,
                enabledHours);
        return beeperBeepServiceIntent;
    }

    /*private void updateNotification(boolean flashLed) {
        if (notification == null) {
            return;
        }

        if (flashLed && isLedNotificationEnabled) {
            notificationBuilder.setLights(Color.GREEN, 20, 200);
        } else {
            notificationBuilder.setLights(Color.BLACK, 0, 0);
        }

        if (beeper.getIsWakeLockAcquired() != null && beeper.getIsWakeLockAcquired()) {
            notificationBuilder.setSmallIcon(R.drawable.watch04_stat_notify_lit);
        } else {
            notificationBuilder.setSmallIcon(R.drawable.watch04_stat_notify);
        }

        notificationBuilder.setProgress(60, now.get(Calendar.MINUTE), false);
        notificationManager.notify(1, notificationBuilder.build());
    }*/

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        cancelBeepingService();
        if (restart) {
            sendBroadcast(new Intent(this, BeeperServiceRestartBroadcastReceiver.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        refreshPreferences();
//        start(beeper.getBeeperServiceIntent());
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        prefs.unregisterOnSharedPreferenceChangeListener(this);
        cancelBeepingService();
        if (restart) {
            sendBroadcast(new Intent(this, BeeperServiceRestartBroadcastReceiver.class));
        }
    }

    private void cancelBeepingService() {
        if (beeperBeepServicePendingIntent != null) {
            beeperBeepServicePendingIntent.cancel();
        }
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getBeeperBeepServiceIntent());
        Log.d(Beeper.LOG_TAG, "Beeping service canceled.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(Beeper.LOG_TAG, "Shared preferences listener.");
        if (getBeeperEnabled(sharedPreferences)) {
            queBeepingService();
        } else {
            Intent beeperBeepServiceIntent =
                    new Intent(getApplicationContext(), BeeperBeepService.class);
            PendingIntent pendingIntent = PendingIntent
                    .getService(beeper.getApplicationContext(), 0, beeperBeepServiceIntent, 0);
            alarmManager.cancel(pendingIntent);
        }
    }*/
}
