package net.wojdat.damian.thebeeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Xtreme on 2015-08-23.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private Beeper beeper;

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		beeper = (Beeper) getActivity().getApplication();
	}

	@Override
	public void onResume () {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause () {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged (SharedPreferences sharedPreferences,
										   String key) {
		if (key.equals(Beeper.PREF_KEEP_SCREEN_ON)) {
			((Beeper) getActivity().getApplication()).processWakeLockPreference();

			getActivity().stopService(beeper.getBeeperServiceIntent());
		} else if (key.equals(Beeper.PREF_BEEP_VOLUME)) {
			playTestSound(sharedPreferences.getInt(key, 0));
			getActivity().stopService(beeper.getBeeperServiceIntent());
		} else if (key.equals(Beeper.PREF_LED_NOTIFICATION_ENABLED)) {
			getActivity().stopService(beeper.getBeeperServiceIntent());
		}
	}

	private void playTestSound (int volume) {
		MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.beep);
		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

		int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * volume) / 100, 0);
		mediaPlayer.start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
	}
}