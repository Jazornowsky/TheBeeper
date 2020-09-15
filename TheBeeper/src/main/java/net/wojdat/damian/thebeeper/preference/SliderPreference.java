package net.wojdat.damian.thebeeper.preference;

import android.content.Context;
import android.util.AttributeSet;

import net.wojdat.damian.thebeeper.R;

import androidx.preference.DialogPreference;

/**
 * Created by Xtreme on 2015-08-23.
 */
public class SliderPreference extends DialogPreference {
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    public SliderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_slider_dialog);
    }


}
