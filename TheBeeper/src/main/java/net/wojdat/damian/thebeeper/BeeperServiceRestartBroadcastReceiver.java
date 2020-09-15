package net.wojdat.damian.thebeeper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class BeeperServiceRestartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Beeper.LOG_TAG, "BeeperServiceRestartBroadcastReceiver onReceive.");
        Beeper beeper = (Beeper) context.getApplicationContext();
        Intent beeperServiceIntent = BeeperService
                .getBeeperServiceIntent(
                        context,
                        beeper.getEnabled(),
                        beeper.getBeeperVolume(),
                        beeper.getEnabledHours());
        ContextCompat.startForegroundService(context, beeperServiceIntent);
    }
}
