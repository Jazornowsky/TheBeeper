package net.wojdat.damian.thebeeper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;

/**
 * Created by Xtreme on 2015-08-23.
 */
public class Beeper extends Application {

	public static final String LOG_TAG                       = "TheBeeper";
	public static final String PREF_KEEP_SCREEN_ON           = "keep_screen_on";
	public static final String PREF_BEEP_VOLUME              = "beep_volume";
	public static final String PREF_LED_NOTIFICATION_ENABLED = "led_notification_enabled";

	private SharedPreferences     preference;
	private PowerManager          powerManager;
	private PowerManager.WakeLock wakeLock;
	private Boolean               isWakeLockAcquired;
	private Intent                beeperServiceIntent;

	/**
	 * @suppressWarnings This app uses WakeLock permission to configure screen lit option globally - it's intended.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate () {
		super.onCreate();

		preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
		beeperServiceIntent = new Intent(getApplicationContext(), BeeperService.class);

		processWakeLockPreference();
	}

	public void processWakeLockPreference () {
		Boolean prefKeepScreenOn = preference.getBoolean(PREF_KEEP_SCREEN_ON, false);

		if (prefKeepScreenOn && !wakeLock.isHeld()) {
			wakeLock.acquire();
			isWakeLockAcquired = true;
		} else if (wakeLock.isHeld()) {
			wakeLock.release();
			isWakeLockAcquired = false;
		}
	}

	public SharedPreferences getPreference () {
		return preference;
	}

	public PowerManager getPowerManager () {
		return powerManager;
	}

	public PowerManager.WakeLock getWakeLock () {
		return wakeLock;
	}

	public Boolean getIsWakeLockAcquired () {
		return isWakeLockAcquired;
	}

	public Intent getBeeperServiceIntent () {
		return beeperServiceIntent;
	}
}
