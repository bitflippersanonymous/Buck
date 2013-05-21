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
	    
	        findPreference(KEY_PREF_KERF).setOnPreferenceChangeListener(this);
	        findPreference(KEY_PREF_TOP).setOnPreferenceChangeListener(this);
	        
	        // Would be nice to notify all automatically
	        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
	        setSummary(sharedPreferences, KEY_PREF_KERF, R.string.kerf_desc);
	        setSummary(sharedPreferences, KEY_PREF_TOP, R.string.top_desc);
	    }
	    
		private void setSummary(SharedPreferences sharedPreferences, String key, int desc) {
			StringBuilder summary = new StringBuilder(); 
			summary.append(Integer.parseInt(sharedPreferences.getString(key, "0")))
				.append(getResources().getString(desc));
			findPreference(key).setSummary(summary);
		}	        

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
            final String key = preference.getKey();
			if ( key.equals(KEY_PREF_KERF) ) {
				if (Integer.parseInt((String)newValue) < KERF_MIN ) 
					return false;
		        setSummary(newValue, R.string.kerf_desc);
            } else if ( key.equals(KEY_PREF_TOP) ) {
				if (Integer.parseInt((String)newValue) < TOP_MIN ) 
					return false;
		        setSummary(newValue, R.string.top_desc);
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