package net.wojdat.damian.thebeeper;

import java.util.HashSet;
import java.util.Set;

public class PreferenceUtil {
    public static Set<String> getDefaultEnabledHours() {
        Set<String> hours = new HashSet<>();
        for (int i = 0; i < 24; i++) {
            hours.add(String.valueOf(i));
        }
        return hours;
    }
}
