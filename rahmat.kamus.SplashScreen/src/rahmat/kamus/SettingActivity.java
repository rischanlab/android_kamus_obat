package rahmat.kamus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
 
public class SettingActivity extends PreferenceActivity {
		Preference autolink, font_size, search_limit;
		
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.layout.setting);
                
                autolink = (Preference) findPreference("autolink");
                autolink.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference prefs, Object new_value) {
						// TODO Auto-generated method stub
						if (new_value.equals(new Boolean(true))) {
							autolink.setSummary("Enabled");
						} else {
							autolink.setSummary("Disabled");
						}
						return true;
					}
				});
                
                font_size = (Preference) findPreference("font_size");
                font_size.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference prefs, Object new_value) {
						// TODO Auto-generated method stub
						font_size.setSummary(new_value.toString()+" sp");
						return true;
					}
				});
                
                search_limit = (Preference) findPreference("search_limit");
                search_limit.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference prefs, Object new_value) {
						// TODO Auto-generated method stub
						search_limit.setSummary(new_value.toString()+" entries displayed on search.");
						return true;
					}
				});
        }
        
        public void onStart() {
        	super.onStart();
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	if (prefs.getBoolean("autolink", true)) {
				autolink.setSummary("Enabled");
			} else {
				autolink.setSummary("Disabled");
			}
        	font_size.setSummary(prefs.getString("font_size", "18")+" sp");
        	search_limit.setSummary(prefs.getString("search_limit", "15")+" entries displayed on search.");
        }
        
}
