package net.wojdat.damian.thebeeper.preference;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import net.wojdat.damian.thebeeper.Beeper;
import net.wojdat.damian.thebeeper.R;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceDialogFragmentCompat;

public class SliderDialog extends PreferenceDialogFragmentCompat implements SeekBar.OnSeekBarChangeListener {

    private SeekBar seekBar;
    private int defaultValue, maxValue, volume = 0;
    private int oldVolume;

    public static SliderDialog newInstance(String preferenceKey) {
        SliderDialog sliderDialog = new SliderDialog();
        Bundle bundle = new Bundle();
        bundle.putString(PreferenceDialogFragmentCompat.ARG_KEY, preferenceKey);
        sliderDialog.setArguments(bundle);

        return sliderDialog;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        seekBar.setMax(100);
        seekBar.setProgress(volume);
        seekBar.setOnSeekBarChangeListener(this);
        oldVolume = getPreference()
                .getPreferenceManager()
                .getSharedPreferences()
                .getInt(getArguments().get(ARG_KEY).toString(), Beeper.DEFAULT_BEEP_VOLUME);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        int volumePreference = getPreference()
                .getSharedPreferences()
                .getInt(getArguments()
                        .getString(ARG_KEY), Beeper.DEFAULT_BEEP_VOLUME);
        volume = volumePreference;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            volume = seekBar.getProgress();
        } else {
            volume = oldVolume;
        }
        getPreference()
                .getSharedPreferences()
                .edit()
                .putInt(getArguments()
                        .getString(ARG_KEY), volume)
                .apply();
    }

    private void playTestSound(int volume) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.beep);
        AudioManager audioManager = (AudioManager) getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * volume) / 100, 0);
        mediaPlayer.start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(Beeper.LOG_TAG, "" + seekBar.getProgress());
        playTestSound(seekBar.getProgress());
    }
}
