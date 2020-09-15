package net.wojdat.damian.thebeeper;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Created by Xtreme on 2015-08-23.
 */
public class Beeper extends Application {

    public static final String LOG_TAG = "TheBeeper";
    public static final String PREF_RESTART = "beeper_restart";
    public static final String PREF_ENABLED = "beeper_enabled";
    public static final String PREF_KEEP_SCREEN_ON = "keep_screen_on";
    public static final String PREF_BEEP_VOLUME = "beep_volume";
    public static final String PREF_LED_NOTIFICATION_ENABLED = "led_notification_enabled";
    public static final String PREF_ENABLED_HOURS = "enabled_hours";
    public static final Boolean DEFAULT_BEEPER_ENABLED = Boolean.TRUE;
    public static final Integer DEFAULT_BEEP_VOLUME = 50;

    private Intent beeperServiceIntent;
    private Boolean enabled;
    private Integer beeperVolume;
    private ArrayList<String> enabledHours;

    /**
     * @suppressWarnings This app uses WakeLock permission to configure screen lit option globally - it's intended.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preference = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        beeperServiceIntent = new Intent(getApplicationContext(), BeeperService.class);
        enabled = preference.getBoolean(PREF_ENABLED, DEFAULT_BEEPER_ENABLED);
        beeperVolume = preference.getInt(PREF_BEEP_VOLUME, DEFAULT_BEEP_VOLUME);
        enabledHours = new ArrayList<>();
        enabledHours.addAll(preference.getStringSet(PREF_ENABLED_HOURS,
                PreferenceUtil.getDefaultEnabledHours()));
    }

    public Intent getBeeperServiceIntent() {
        return beeperServiceIntent;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getBeeperVolume() {
        return beeperVolume;
    }

    public void setBeeperVolume(Integer beeperVolume) {
        this.beeperVolume = beeperVolume;
    }

    public ArrayList<String> getEnabledHours() {
        return enabledHours;
    }

    public void setEnabledHours(ArrayList<String> enabledHours) {
        this.enabledHours = enabledHours;
    }
}
