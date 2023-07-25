package com.crm.cardlinkmerchant.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.crm.cardlinkmerchant.activity.SwitchBusinessActivity;

import org.json.JSONObject;

public class Storage {
    public static String getAppConfig(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("APP_CONFIG", "");
    }
    public static void setAppConfig(Context context, String config){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("APP_CONFIG",config);
        editor.commit();
    }
    public static String getToken(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("TOKEN", "");
    }
    public static void setToken(Context context, String token){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("TOKEN",token);
        editor.commit();
    }

    public static String getMainUserEmail(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("MAIN_USER_EMAIL","");
    }
    public static void setMainUserEmail(Context context, String email){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("MAIN_USER_EMAIL",email);
        editor.commit();
    }

    public static String getMainUserName(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("MAIN_USER_NAME","");
    }
    public static void setMainUserName(Context context, String userName){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("MAIN_USER_NAME",userName);
        editor.commit();
    }
    public static String getOrganisations(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("ORGANISATIONS", "");
    }
    public static void setOrganisations(Context context, String orgs){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ORGANISATIONS", orgs);
        editor.commit();
    }
    public static String getBusiness(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("BUSINESS", "");
    }
    public static void setBusiness(Context context, String business){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("BUSINESS", business);
        editor.commit();
    }
    public static String getBusinessName(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("BUSINESS_NAME", "");
    }
    public static void setBusinessName(Context context, String business){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("BUSINESS_NAME", business);
        editor.commit();
    }
    public static String getOutletsJSON(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("OUTLETSJSON", "");
    }
    public static void setOutletsJSON(Context context, String outletjson){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("OUTLETSJSON", outletjson);
        editor.commit();
    }
    public static String getOutlet(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("OUTLET", "");
    }
    public static void setOutlet(Context context, String outlet){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("OUTLET", outlet);
        editor.commit();
    }
    public static String getOutletName(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("OUTLET_NAME", "");
    }
    public static void setOutletName(Context context, String outlet){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("OUTLET_NAME", outlet);
        editor.commit();
    }
    public static String getRefreshToken(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("REFRESH_TOKEN", "");
    }
    public static void setRefreshToken(Context context, String refreshToken){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("REFRESH_TOKEN",refreshToken);
        editor.commit();
    }

    public static String getExpTime(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("EXP_TIME", "");
    }
    public static void setExpTime(Context context, String refreshToken){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("EXP_TIME",refreshToken);
        editor.commit();
    }
    public static String getOrganisationSelected(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("ORG_SELECTED", "");
    }
    public static void setOrganisationSelected(Context context, String orgId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ORG_SELECTED",orgId);
        editor.commit();
    }

}
