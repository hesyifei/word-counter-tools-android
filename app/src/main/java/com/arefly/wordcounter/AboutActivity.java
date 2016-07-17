package com.arefly.wordcounter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import java.util.List;

public class AboutActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }


    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return AboutFragment.class.getName().equals(fragmentName);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // http://stackoverflow.com/q/12070744/2603230
            // Show the Up button in the action bar.
            actionBar.setHomeButtonEnabled(true);
        }
    }

    // http://stackoverflow.com/q/12070744/2603230
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class AboutFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_about);

            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            Preference appVersionInfoPref = findPreference("version_info");
            appVersionInfoPref.setTitle(String.format(getString(R.string.about_app_version_info), BuildConfig.VERSION_NAME));
            appVersionInfoPref.setSummary(String.format(getString(R.string.about_app_version_info_summary), BuildConfig.VERSION_CODE));
        }
    }
}
