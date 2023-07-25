package com.crm.cardlinkmerchant.activity;

import static com.crm.cardlinkmerchant.utils.LanguageUtil.getCurrentLanguage;
import static com.crm.cardlinkmerchant.utils.LanguageUtil.initLanguage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppConfig;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.Constants;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.LanguageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    CustomProgressDialog customProgressDialog;
    private final String TAG = "MAIN_ACT";

    private CoordinatorLayout mainPage;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguage(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        String business_name = Storage.getBusinessName(MainActivity.this);
        toolbar.setTitle(business_name);

        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        initScreen();
        customProgressDialog = new CustomProgressDialog();
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mainPage = findViewById(R.id.mainPage);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        //nav home de lam gi ????
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home
                )
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.app_bar_switch)
                .setActionView(new Switch(this));

        Menu menu = navigationView.getMenu();
        MenuItem logoutMenu = menu.findItem(R.id.nav_logout);
        MenuItem consoleMenu = menu.findItem(R.id.nav_console);
        MenuItem switchBusinessMenu = menu.findItem(R.id.nav_switch_business);
        if(!AppConfig.OPEN_CONSOLE_PAGE){
            consoleMenu.setVisible(false);
        }


        MenuItem item = menu.findItem(R.id.app_bar_switch);
        ((Switch) item.getActionView()).setClickable(false);
        if (getCurrentLanguage(this).equals("en")){
            ((Switch) item.getActionView()).setChecked(true);

        }
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(getCurrentLanguage(MainActivity.this).equals("el")){
                    LanguageUtil.setLanguage(MainActivity.this, "en");
                    ((Switch) item.getActionView()).setChecked(true);
                }
                else{
                    LanguageUtil.setLanguage(MainActivity.this, "el");
                    ((Switch) item.getActionView()).setChecked(false);
                    //updateLocale(gr);
                }
                MainActivity.this.recreate();
                return false;
            }
        });

        logoutMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                new CustomDialog(MainActivity.this, new CustomDialog.OKListener() {
                    @Override
                    public void onOk() {
                        customProgressDialog.show(MainActivity.this);
                        if(AppUtils.isNetworkConnected(MainActivity.this)){
                            (new MainActivity.SignOutTask()).execute("");
                        }
                    }
                    @Override
                    public void onCancel() {

                    }
                }, getString(R.string.logout_msg), getString(R.string.menu_logout), getString(R.string.cancel),false).show();
                //android.os.Process.killProcess(android.os.Process.myPid());
                return false;
            }
        });
        switchBusinessMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(MainActivity.this, SwitchBusinessActivity.class);
                intent.putExtra("from_home", true);
                startActivity(intent);
                finish();
                return false;
            }
        });


        String appConfigStr = Storage.getAppConfig(this);
        //AppUtils.console(getContext(),TAG, "appConfig="+ appConfigStr);
        String url = null;
        ObjectMapper mapper = AppUtils.initMapper();

        try {
            com.crm.cardlinkmerchant.model.AppConfig appConfig = mapper.readValue(appConfigStr.toString(), com.crm.cardlinkmerchant.model.AppConfig.class);
            AppUtils.console(this, TAG, "appname="+ appConfig.getName());
            setTitle(appConfig.getName());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            String stackTrace = Log.getStackTraceString(e);
            AppUtils.console(this,TAG, "AppConfig JsonProcessingException e ="+ stackTrace);
        }

        animationNav();
    }

    private void animationNav() {
        drawer.setScrimColor(getResources().getColor(R.color.Transparent));
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                final float diffScaledOffset = slideOffset *(1-0.95f);
                final float offsetScale = 1-diffScaledOffset;
                mainPage.setScaleX(offsetScale);
//                mainPage.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth()*slideOffset;
                final float xOffsetDiff = mainPage.getWidth()*diffScaledOffset/2;
                final float xTraslation = xOffset -xOffsetDiff;
                mainPage.setTranslationX(xTraslation);

            }
        });
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
        {
            fullSceen();
        }
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


    private class SignOutTask extends AsyncTask<String, Void, Void> {

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

                result = services.signOut();
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(MainActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(MainActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(MainActivity.this, getString(R.string.system_error), null, false);
                }
                exception = true;
                customProgressDialog.dismiss();
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            customProgressDialog.dismiss();

            if (result != null && result.has("code")){
                try {
                    String code = result.getString("code");
                    AppUtils.console(getApplicationContext(),TAG, "onPostExecute sign out result code==" + code);
                    if(code.equals(ResultCode.OK.getLabel())){
                        logOut();
                    }
                    else{

                        CustomDialog customDialog = new CustomDialog(MainActivity.this, new CustomDialog.OKListener() {
                            @Override
                            public void onOk() {
                                logOut();
                            }

                            @Override
                            public void onCancel() {

                            }
                        }, getString(R.string.logout_failed), getString(R.string.force_logout), "", false);
                        customDialog.show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String stackTrace = Log.getStackTraceString(e);
                    AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);
                }
            }
            else if(!exception){
                CustomDialog customDialog = new CustomDialog(MainActivity.this, new CustomDialog.OKListener() {
                    @Override
                    public void onOk() {
                        logOut();
                    }

                    @Override
                    public void onCancel() {

                    }
                }, getString(R.string.logout_failed), getString(R.string.force_logout), "", false);
                customDialog.show();
            }
        }
    }

    private void logOut(){
        Storage.setToken(MainActivity.this, "");
        Storage.setOrganisations(MainActivity.this, "");
        Storage.setBusiness(MainActivity.this, "");
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private class SendEmailTask extends AsyncTask<String, Void, Void> {
        private Boolean isTermAndCondition;
        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        public SendEmailTask(Boolean _isTermAndCondition) {
            super();
            this.isTermAndCondition = _isTermAndCondition;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(MainActivity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());
                String content = makeContentCommnucation(isTermAndCondition);
                String subject = isTermAndCondition ? "Cardlink Term And Conditions" : "Cardlink Privacy Policy";
                result = services.sendCommunication(subject,content);
            }catch(Exception e){
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(MainActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(MainActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(MainActivity.this, getString(R.string.system_error), null, false);
                }
                exception = true;
                customProgressDialog.dismiss();
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            customProgressDialog.dismiss();
            if (result != null && result.has("code")){
                try {
                    String code = result.getString("code");
                    AppUtils.console(getApplicationContext(),TAG, "onPostExecute sign out result code==" + code);
                    if(code.equals(ResultCode.OK.getLabel())){
                        AppUtils.msg(MainActivity.this, getString(R.string.send_email_success), null, true);
                    }
                    else{
                        AppUtils.msg(MainActivity.this, getString(R.string.send_email_fail), null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String stackTrace = Log.getStackTraceString(e);
                    AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);
                }
            }else{
                AppUtils.msg(MainActivity.this, getString(R.string.send_email_fail), null, false);
            }
        }
    }

    private String makeContentCommnucation(Boolean isTermAndCondition){
        String content ="Please click on the link below: <br>";
        if(isTermAndCondition){
            content = content + Constants.CrmConfig.TERM_AND_CONDITION_MERCHANT_URL;
        }else{
            content = content + Constants.CrmConfig.PRIVACY_POLICY_MERCHANT_URL;
        }
        return content;
    }
}