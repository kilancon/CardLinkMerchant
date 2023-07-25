package com.crm.cardlinkmerchant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = "SPLASH_ACT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);
        initScreen();
        if (AppUtils.isNetworkConnected(this))
            new GetAppConfig().execute("");
        //checkLogin();
    }
    private void initScreen() {
        //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        // Hide the status bar and navigation bar
        fullSceen();
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                fullSceen();
            }
        });
    }
    private void fullSceen(){

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    private void checkLogin() {
        String access_token = Storage.getToken(SplashActivity.this);
        String business = Storage.getBusiness(SplashActivity.this);
        if(access_token.isEmpty()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else{
            if(isExpToken()){
                if(AppUtils.isNetworkConnected(this)){
                    new RefreshToken().execute("");
                }
            }
            else{
                if(!business.isEmpty()){
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                else{
                    startActivity(new Intent(this, SwitchBusinessActivity.class));
                    finish();
                }

            }
        }

    }

    private Boolean isExpToken(){
        Boolean check = false;
        String exp = Storage.getExpTime(SplashActivity.this);
        Date dateNow = new Date();
        long timeNow = dateNow.getTime();
        if(!exp.isEmpty()){
            long timeExp = Long.parseLong(exp);
            AppUtils.console(getApplicationContext(),TAG, "checkExpToken timeExp: " + timeExp);
            AppUtils.console(getApplicationContext(),TAG, "checkExpToken timeNow: " + timeNow);

            long tokenLiveInSec = (timeExp - timeNow) / 1000;
            //tokenLiveInSec = 10;
            AppUtils.console(getApplicationContext(),TAG, "checkExpToken tokenLiveInSec: " + tokenLiveInSec);
            if(tokenLiveInSec < 60*60){
                check = true;
            }
        }
        return check;
    }
    private class GetAppConfig extends AsyncTask<String, Void, Void> {

        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());
                result = services.getAppConfig();
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(SplashActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(SplashActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(SplashActivity.this, getString(R.string.system_error), null, false);
                }
                exception = true;
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result != null && result.has("code")){
                try {
                    String code = result.getString("code");

                    if(code.equals(ResultCode.OK.getLabel())){
                        //Log.e(TAG, "result code=="+code);
                        JSONArray content = result.getJSONArray("content");
                        if(content.length() > 0){
                            Storage.setAppConfig(SplashActivity.this, content.get(0).toString());
                        }
                        checkLogin();
                    }
                    else {
                        AppUtils.msg(SplashActivity.this, getString(R.string.system_error), null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(!exception){
                AppUtils.msg(SplashActivity.this, getString(R.string.system_error), null, false);
            }
        }
    }


    private class RefreshToken extends AsyncTask<String, Void, Void> {

        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());
                result = services.refreshToken();
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(SplashActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(SplashActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(SplashActivity.this, getString(R.string.system_error), null, false);
                }
                exception = true;
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result != null && result.has("code")){
                try {
                    String code = result.getString("code");

                    if(code.equals(ResultCode.OK.getLabel())){
                        //Log.e(TAG, "result code=="+code);
                        String access_token = result.getString("access_token");
                        String refresh_token = result.getString("refresh_token");
                        String exp = result.getString("exp");
                        Storage.setToken(SplashActivity.this, access_token);
                        Storage.setRefreshToken(SplashActivity.this, refresh_token);
                        Storage.setExpTime(SplashActivity.this, exp);

                        JSONArray organisations = result.getJSONArray("organisations");
                        Storage.setOrganisations(SplashActivity.this, organisations.toString());
                        String business = Storage.getBusiness(SplashActivity.this);

                        if(!business.isEmpty())
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        else
                            startActivity(new Intent(SplashActivity.this, SwitchBusinessActivity.class));

                        SplashActivity.this.finish();
                    }
                    else {
                        //AppUtils.msg(SplashActivity.this, getString(R.string.system_error), null, false);
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        SplashActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(!exception){
                //AppUtils.msg(SplashActivity.this, getString(R.string.system_error), null, false);
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                SplashActivity.this.finish();
            }
        }
    }
}