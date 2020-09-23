package net.wojdat.damian.thebeeper.service;

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

import net.wojdat.damian.thebeeper.Beeper;
import net.wojdat.damian.thebeeper.BeeperServiceRestartBroadcastReceiver;
import net.wojdat.damian.thebeeper.R;
import net.wojdat.damian.thebeeper.SettingsActivity;

import java.util.ArrayList;

import androidx.core.app.NotificationCompat;

/**
 * Created by 7 on 2015-02-26.
 */
public class BeeperService extends Service {
    private Boolean restart = Boolean.TRUE;
    private Boolean enabled;
    private Integer volume;
    private ArrayList<String> enabledHours;
    private Notification notification;

    public static Intent getBeeperServiceIntent(Context context,
                                                Boolean enabled,
                                                Integer volume,
                                                ArrayList<String> enabledHours) {
        Intent beeperServiceIntent = new Intent(context, BeeperService.class);
        beeperServiceIntent.putExtra(
                Beeper.PREF_ENABLED,
                enabled);
        beeperServiceIntent.putExtra(
                Beeper.PREF_BEEP_VOLUME,
                volume);
        beeperServiceIntent.putStringArrayListExtra(
                Beeper.PREF_ENABLED_HOURS,
                enabledHours);
        Log.d(Beeper.LOG_TAG, "getBeeperServiceIntent " +
                "enabled: " + enabled + ", " +
                "volume: " + volume + ", " +
                "enabledHours: " + enabledHours.toString());
        return beeperServiceIntent;
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
            BeeperBeepService.queBeepingService(
                    getApplicationContext(),
                    null,
                    volume,
                    enabledHours);
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
            Intent intent = new Intent(BeeperServiceRestartBroadcastReceiver.RESTART_INTENT);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelBeepingService();
        if (restart) {
            Intent intent = new Intent(BeeperServiceRestartBroadcastReceiver.RESTART_INTENT);
            sendBroadcast(intent);
        }
    }

    private void cancelBeepingService() {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(
                BeeperBeepService.getBeeperBeepServicePendingIntent(
                        getApplicationContext(),
                        volume,
                        enabledHours));
        Log.d(Beeper.LOG_TAG, "Beeping service canceled.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
