package com.crm.cardlinkmerchant.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LanguageUtil {
    public static void setLanguage(Activity activity, String language){

        //updateLocale(gr);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        //Locale locale = new Locale(languageode);
        editor.putString("LANGUAGE",language);
        editor.commit();
    }
    public static String getCurrentLanguage(Activity activity){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        String language = preferences.getString("LANGUAGE", "el");
        return language;
    }
    public static void initLanguage(Activity activity){
        String language = getCurrentLanguage(activity);
        Locale gr = new Locale(language);
        Locale.setDefault(gr);
        Resources resources = activity.getBaseContext().getApplicationContext().getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(gr);

        activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
        resources.updateConfiguration(config, resources.getDisplayMetrics());

    }
}
