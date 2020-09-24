package net.wojdat.damian.thebeeper.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import net.wojdat.damian.thebeeper.Beeper;
import net.wojdat.damian.thebeeper.BeeperServiceRestartBroadcastReceiver;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.core.content.ContextCompat;

public class BeeperJobService extends JobService {
    private static BeeperServiceRestartBroadcastReceiver beeperServiceRestartBroadcastReceiver;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(Beeper.LOG_TAG, "BeeperJobService onStartJob.");
        Intent beeperServiceIntent = BeeperService
                .getBeeperServiceIntent(
                        this,
                        params.getExtras().getBoolean(Beeper.PREF_ENABLED),
                        params.getExtras().getInt(Beeper.PREF_BEEP_VOLUME),
                        new ArrayList<>(Arrays.asList(
                                params.getExtras().getStringArray(Beeper.PREF_ENABLED_HOURS))));
        logPreferences(params);
        ContextCompat.startForegroundService(this, beeperServiceIntent);
        Log.d(Beeper.LOG_TAG, "BeeperJobService foreground beeper service started.");
        registerBeeperServiceRestartBroadcastReceiver();
        Log.d(Beeper.LOG_TAG, "BeeperJobService broadcast receiver restart service.");
        return false;
    }

    private void logPreferences(JobParameters params) {
        Log.d(Beeper.LOG_TAG,
                "BeeperServiceRestartBroadcastReceiver PREF_ENABLED: " +
                        params.getExtras().getBoolean(Beeper.PREF_ENABLED));
        Log.d(Beeper.LOG_TAG,
                "BeeperServiceRestartBroadcastReceiver PREF_BEEP_VOLUME: " +
                        params.getExtras().getInt(Beeper.PREF_BEEP_VOLUME));
        Log.d(Beeper.LOG_TAG,
                "BeeperServiceRestartBroadcastReceiver PREF_ENABLED_HOURS: " +
                        params.getExtras().getStringArray(Beeper.PREF_ENABLED_HOURS));
    }

    private void registerBeeperServiceRestartBroadcastReceiver() {
        if (beeperServiceRestartBroadcastReceiver == null) {
            beeperServiceRestartBroadcastReceiver = new BeeperServiceRestartBroadcastReceiver();
        } else try {
            unregisterReceiver(beeperServiceRestartBroadcastReceiver);
        } catch (Exception e) {
            Log.d(Beeper.LOG_TAG,
                    "Exception during broadcast receiver registration/unregistration", e);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(BeeperServiceRestartBroadcastReceiver.RESTART_INTENT);
                try {
                    registerReceiver(beeperServiceRestartBroadcastReceiver, intentFilter);
                } catch (Exception e) {
                    try {
                        getApplicationContext()
                                .registerReceiver(beeperServiceRestartBroadcastReceiver,
                                        intentFilter);
                    } catch (Exception ex) {
                        Log.d(Beeper.LOG_TAG,
                                "Exception during delayed broadcast receiver " +
                                        "registration/unregistration", e);
                    }
                }
            }
        }, 1000);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Intent broadcastIntent = new Intent(BeeperServiceRestartBroadcastReceiver.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                unregisterReceiver(beeperServiceRestartBroadcastReceiver);
            }
        }, 1000);
        return false;
    }
}
