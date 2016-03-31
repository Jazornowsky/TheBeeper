package net.wojdat.damian.thebeeper;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by 7 on 2015-02-27.
 */
public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startService(((Beeper)getApplication()).getBeeperServiceIntent());

		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}
}
