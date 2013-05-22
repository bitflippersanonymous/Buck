package com.bitflippersanonymous.buck.ui;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import com.bitflippersanonymous.buck.R;

public class SettingsActivity extends Activity {

	public final static String KEY_PREF_KERF = "pref_kerf";
	public final static String KEY_PREF_TOP = "pref_top";
	
	private final static int TOP_MIN = 4;
	private final static int KERF_MIN = 0;
	
	public static class SettingsFragment extends PreferenceFragment
		implements OnPreferenceChangeListener {
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.preferences);

	        setupOnChangeListeners();
	    }

		private void setupOnChangeListeners() {
			final String keys[] = {KEY_PREF_KERF, KEY_PREF_TOP};
	        final int descs[] = {R.string.kerf_desc, R.string.top_desc};
	        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
	        for ( int i = 0; i < keys.length; i++ ) {
		        findPreference(keys[i]).setOnPreferenceChangeListener(this);
	        	setSummary(findPreference(keys[i]), sharedPreferences.getString(keys[i], "0"), descs[i]);
	        }
		}
	    
		private void setSummary(Preference preference, String newValue, int desc) {
			StringBuilder summary = new StringBuilder(); 
			summary.append(newValue).append(getResources().getString(desc));
			preference.setSummary(summary);
		}	        

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
            final String key = preference.getKey();
			if ( key.equals(KEY_PREF_KERF) ) {
				if (Integer.parseInt((String)newValue) < KERF_MIN ) 
					return false;
		        setSummary(preference, (String)newValue, R.string.kerf_desc);
            } else if ( key.equals(KEY_PREF_TOP) ) {
				if (Integer.parseInt((String)newValue) < TOP_MIN ) 
					return false;
		        setSummary(preference, (String)newValue, R.string.top_desc);
			}
			return true;
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
    

}