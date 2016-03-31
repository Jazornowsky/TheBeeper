package net.wojdat.damian.thebeeper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by 7 on 2015-02-26.
 */
public class BeeperService extends Service {

	private MediaPlayer          mp;
	private Calendar             now;
	private Notification         notification;
	private NotificationManager  notificationManager;
	private Notification.Builder notificationBuilder;
	private AudioManager         audioManager;
	private Beeper beeper;
	private SharedPreferences    preference;
	private int                  beepVolume;
	private boolean              isLedNotificationEnabled;

	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {

		notificationBuilder = new Notification.Builder(getApplicationContext())
				.setContentTitle(getString(R.string.service_name))
				.setSmallIcon(R.drawable.watch04_stat_notify)
				.setAutoCancel(true)
				.setProgress(0, 0, false);

		notification = notificationBuilder.build();

		startForeground(1, notification);

		return START_STICKY;
	}

	@Override
	public void onCreate () {
		super.onCreate();
		mp = MediaPlayer.create(getApplicationContext(), R.raw.beep);

		beeper = (Beeper) getApplication();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		refreshPreferences();

		new Thread(new Runnable() {
			@Override
			public void run () {
				while (true) {
					now = Calendar.getInstance();
					Log.d(Beeper.LOG_TAG, "Curr min = " + now.get(Calendar.MINUTE) + " and sec = " + now.get(Calendar.SECOND));
					if (now.get(Calendar.MINUTE) == 0 && now.get(Calendar.SECOND) == 0) {
						if (!mp.isPlaying()) {
							int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
							audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, beepVolume, 0);
							mp.start();
							BeeperService.this.updateNotification(true);
							try {
								Thread.sleep(1000);
								BeeperService.this.updateNotification(false);
								audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
								Thread.sleep(58000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} else {
						BeeperService.this.updateNotification(false);
						try {
							Thread.sleep(600);
						} catch (InterruptedException e) {
							return;
						}
					}
				}
			}
		}
		).start();
	}

	private void updateNotification (boolean flashLed) {

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
	}

	private void refreshPreferences () {
		isLedNotificationEnabled = preference.getBoolean(Beeper.PREF_LED_NOTIFICATION_ENABLED, false);
		beepVolume = preference.getInt(Beeper.PREF_BEEP_VOLUME, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

		if (beepVolume == audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
			beepVolume = beepVolume / 2;
		} else {
			beepVolume = (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * beepVolume) / 100;
		}
	}

	@Override
	public void onDestroy () {
		super.onDestroy();
		refreshPreferences();
		startService(beeper.getBeeperServiceIntent());
	}

	@Override
	public IBinder onBind (Intent intent) {
		return null;
	}
}
