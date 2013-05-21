package com.bitflippersanonymous.buck.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.bitflippersanonymous.buck.R;

public class SettingsActivity extends Activity {

	public final static String KEY_PREF_KERF = "pref_kerf";
	public final static String KEY_PREF_TOP = "pref_top";
	
	public static class SettingsFragment extends PreferenceFragment
		implements OnSharedPreferenceChangeListener {
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.preferences);
	    
	        // Would be nice to notify all automatically
	        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
	        setSummary(sharedPreferences, KEY_PREF_KERF, R.string.kerf_desc);
	        setSummary(sharedPreferences, KEY_PREF_TOP, R.string.top_desc);
	    }
	    
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_PREF_KERF)) {
                setSummary(sharedPreferences, key, R.string.kerf_desc);
            } else if (key.equals(KEY_PREF_KERF)) {
                setSummary(sharedPreferences, key, R.string.top_desc);
            }
        }

		private void setSummary(SharedPreferences sharedPreferences, String key, int desc) {
			StringBuilder summary = new StringBuilder(); 
			summary.append(Integer.valueOf(sharedPreferences.getString(key, "1")))
				.append(getResources().getString(desc));
			findPreference(key).setSummary(summary);
		}	        

        @Override
		public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
		public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
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