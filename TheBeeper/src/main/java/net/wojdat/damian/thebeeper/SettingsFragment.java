package net.wojdat.damian.thebeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import net.wojdat.damian.thebeeper.preference.SliderDialog;
import net.wojdat.damian.thebeeper.preference.SliderPreference;

import java.util.ArrayList;
import java.util.Collections;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

/**
 * Created by Xtreme on 2015-08-23.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Beeper beeper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beeper = (Beeper) getActivity().getApplication();
        getActivity().startForegroundService(
                getBeeperServiceIntent(PreferenceManager.getDefaultSharedPreferences(getContext())));
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof SliderPreference) {
            DialogFragment sliderDialog = SliderDialog.newInstance(preference.getKey());
            sliderDialog.setTargetFragment(this, 0);
            sliderDialog.show(getParentFragmentManager(), "SliderPreference");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Log.d(Beeper.LOG_TAG, "SharedPreference key: " + key);
        if (key.equals(Beeper.PREF_ENABLED) ||
                key.equals(Beeper.PREF_BEEP_VOLUME) ||
                key.equals(Beeper.PREF_ENABLED_HOURS)) {
            switch (key) {
                case Beeper.PREF_ENABLED:
                    beeper.setEnabled(
                            sharedPreferences.getBoolean(
                                    Beeper.PREF_ENABLED,
                                    Beeper.DEFAULT_BEEPER_ENABLED));
                    break;
                case Beeper.PREF_BEEP_VOLUME:
                    beeper.setBeeperVolume(
                            sharedPreferences.getInt(
                                    Beeper.PREF_BEEP_VOLUME,
                                    Beeper.DEFAULT_BEEP_VOLUME));
                    break;
                case Beeper.PREF_ENABLED_HOURS:
                    ArrayList<String> enabledHours = new ArrayList<>(
                            sharedPreferences.getStringSet(
                                    Beeper.PREF_ENABLED_HOURS,
                                    PreferenceUtil.getDefaultEnabledHours()));
                    beeper.setEnabledHours(enabledHours);
                    break;
            }
            getActivity().startForegroundService(getBeeperServiceIntent(sharedPreferences));
        }
    }

    private Intent getBeeperServiceIntent(SharedPreferences sharedPreferences) {
        ArrayList<String> enabledHours = new ArrayList<>();
        enabledHours.addAll(sharedPreferences.getStringSet(
                Beeper.PREF_ENABLED_HOURS,
                Collections.<String>emptySet()));
        Intent beeperServiceIntent = BeeperService.getBeeperServiceIntent(
                getContext(),
                sharedPreferences.getBoolean(
                        Beeper.PREF_ENABLED,
                        Boolean.TRUE),
                sharedPreferences.getInt(
                        Beeper.PREF_BEEP_VOLUME,
                        Beeper.DEFAULT_BEEP_VOLUME),
                enabledHours);
        return beeperServiceIntent;
    }
}