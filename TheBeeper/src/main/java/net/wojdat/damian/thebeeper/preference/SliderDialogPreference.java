package net.wojdat.damian.thebeeper.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import net.wojdat.damian.thebeeper.R;

/**
 * Created by Xtreme on 2015-08-23.
 */
public class SliderDialogPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

	private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

	private Context context;
	private SeekBar seekBar;
	private int defaultValue, maxValue, value = 0;

	public SliderDialogPreference (Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		setDialogLayoutResource(R.layout.preference_slider_dialog);

		defaultValue = attrs.getAttributeIntValue(ANDROID_NS, "defaultValue", 0);
		maxValue = attrs.getAttributeIntValue(ANDROID_NS, "maxValue", 100);
	}

	/**
	 * Binds views in the content View of the dialog to data.
	 * <p>
	 * Make sure to call through to the superclass implementation.
	 *
	 * @param view The content View of the dialog, if it is custom.
	 */
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		value = getPersistedInt(defaultValue);

		seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
		seekBar.setMax(maxValue);
		seekBar.setProgress(value);
	}

	@Override
	public void showDialog(Bundle state) {

		super.showDialog(state);

		Button positiveButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
		positiveButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (shouldPersist()) {

			value = seekBar.getProgress();
			persistInt(seekBar.getProgress());
			callChangeListener(Integer.valueOf(seekBar.getProgress()));
		}

		getDialog().dismiss();
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
	}

	@Override
	public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch (SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch (SeekBar seekBar) {

	}
}
