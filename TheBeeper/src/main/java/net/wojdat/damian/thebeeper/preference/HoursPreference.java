package net.wojdat.damian.thebeeper.preference;

import android.content.Context;
import android.util.AttributeSet;

import java.util.HashSet;
import java.util.Set;

import androidx.preference.MultiSelectListPreference;

public class HoursPreference extends MultiSelectListPreference {

    public HoursPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        CharSequence[] entries = new CharSequence[24];
        CharSequence[] entriesValues = new CharSequence[24];
        Set<String> defaultHours = new HashSet<>();

        for (int i = 0; i < 24; i++) {
            entries[i] = String.valueOf(i);
            entriesValues[i] = String.valueOf(i);
            defaultHours.add(String.valueOf(i));
        }

        setEntries(entries);
        setEntryValues(entriesValues);
        setDefaultValue(defaultHours);
    }
}
