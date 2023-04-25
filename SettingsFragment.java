package com.wgu.termtracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static String PREFERENCE_THEME = "pref_theme";
    public static String PREFERENCE_TERM_ORDER = "pref_term_order";
    public static String PREFERENCE_DEFAULT_COURSE = "pref_default_course";

    @Override
    public void onResume(){
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause(){
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
        if(key.equals(PREFERENCE_THEME)){
            //recreate the activity so the theme takes effect
            getActivity().recreate();
        } else if (key.equals(PREFERENCE_TERM_ORDER)){
            setPrefSummaryTermOrder(sharedPreferences);
        } else if(key.equals(PREFERENCE_DEFAULT_COURSE)) {
            setPrefDefaultCourse(sharedPreferences);
        }
    }



@Override
    public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);

    //load the prefs from xml rsource
    addPreferencesFromResource(R.xml.preferences);

    //access the default prefs
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

    setPrefSummaryTermOrder(sharedPrefs);
    setPrefDefaultCourse(sharedPrefs);
}

// set the suammary to the currently selected order
    private void setPrefSummaryTermOrder(SharedPreferences sharedPrefs){
    String order = sharedPrefs.getString(PREFERENCE_TERM_ORDER, "1");
    String[] termOrders = getResources().getStringArray(R.array.pref_term_order);
    Preference termOrderPref = findPreference(PREFERENCE_TERM_ORDER);
    termOrderPref.setSummary(termOrders[Integer.parseInt(order)]);
    }

    // set summary to the default course
    private void setPrefDefaultCourse(SharedPreferences sharedPrefs){
    String defaultCourse = sharedPrefs.getString(PREFERENCE_DEFAULT_COURSE, "");
    defaultCourse = defaultCourse.trim();
    Preference coursePref = findPreference(PREFERENCE_DEFAULT_COURSE);
    if(defaultCourse.length() == 0){
        coursePref.setSummary(getResources().getString(R.string.pref_none));
    } else {
        coursePref.setSummary(defaultCourse);
    }
    }

}
