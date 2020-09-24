package net.wojdat.damian.thebeeper;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.util.Log;

import net.wojdat.damian.thebeeper.service.BeeperJobService;

public class BeeperServiceRestartBroadcastReceiver extends BroadcastReceiver {
    public static final String RESTART_INTENT = "net.wojdat.damian.thebeeper";
    private JobScheduler jobSchedulerService;

    public static void scheduleBeeperJobService(Context context,
                                                JobScheduler jobScheduler) {
        Beeper beeper = (Beeper) context.getApplicationContext();
        ComponentName beeperServiceComponent = new ComponentName(context, BeeperJobService.class);
        PersistableBundle beeperServiceExtras = new PersistableBundle();
        beeperServiceExtras.putBoolean(
                Beeper.PREF_ENABLED,
                beeper.getEnabled());
        beeperServiceExtras.putInt(
                Beeper.PREF_BEEP_VOLUME,
                beeper.getBeeperVolume());
        beeperServiceExtras.putStringArray(
                Beeper.PREF_ENABLED_HOURS,
                beeper.getEnabledHours().toArray(new String[]{}));
        JobInfo beeperServiceJobInfo = new JobInfo.Builder(1, beeperServiceComponent)
                .setOverrideDeadline(0)
                .setPersisted(true)
                .setExtras(beeperServiceExtras)
                .build();
        jobScheduler.schedule(beeperServiceJobInfo);
        Log.d(Beeper.LOG_TAG, "BeeperServiceRestartBroadcastReceiver job scheduled.");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Beeper.LOG_TAG, "BeeperServiceRestartBroadcastReceiver onReceive.");
        scheduleBeeperJobService(context);
    }

    private void scheduleBeeperJobService(Context context) {
        Beeper beeper = (Beeper) context.getApplicationContext();
        if (jobSchedulerService == null) {
            jobSchedulerService = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }
        logPreferences(beeper);
        scheduleBeeperJobService(context, jobSchedulerService);
    }

    private void logPreferences(Beeper beeper) {
        Log.d(Beeper.LOG_TAG,
                "BeeperServiceRestartBroadcastReceiver PREF_ENABLED: " +
                        beeper.getEnabled());
        Log.d(Beeper.LOG_TAG,
                "BeeperServiceRestartBroadcastReceiver PREF_BEEP_VOLUME: " +
                        beeper.getBeeperVolume());
        Log.d(Beeper.LOG_TAG,
                "BeeperServiceRestartBroadcastReceiver PREF_ENABLED_HOURS: " +
                        beeper.getEnabledHours());
    }
}
