package net.wojdat.damian.thebeeper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by 7 on 2015-02-27.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
