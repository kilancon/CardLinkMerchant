package com.crm.cardlinkmerchant.activity;

import static com.crm.cardlinkmerchant.utils.LanguageUtil.getCurrentLanguage;
import static com.crm.cardlinkmerchant.utils.LanguageUtil.initLanguage;

import androidx.appcompat.app.AppCompatActivity;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.model.Offer;
import com.crm.cardlinkmerchant.model.Organisation;
import com.crm.cardlinkmerchant.model.Outlet;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.LanguageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LOGIN_ACT";
    @BindView(R.id.edLoginUserName)
    EditText edLoginUserName;

    @BindView(R.id.edLoginPassword)
    EditText edLoginPassword;

    @BindView(R.id.btLoginSubmit)
    TextView btLoginSubmit;

//    @BindView(R.id.btLoginForgotPassword)
//    TextView btLoginForgotPassword;

    @BindView(R.id.rbLoginEnglish)
    RadioButton rbLoginEnglish;

    @BindView(R.id.rbLoginGreek)
    RadioButton rbLoginGreek;

    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.btLoginIconPassword)
    ImageView btLoginIconPassword;
    CustomProgressDialog customProgressDialog;

    private String username = "";
    private String password = "";
    private Boolean isEnableSubmit = false;
    private Boolean isShowPass = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguage(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initScreen();

        if (getCurrentLanguage(this).equals("en")){
           rbLoginEnglish.setChecked(true);
            rbLoginGreek.setChecked(false);
        }
        else{
            rbLoginEnglish.setChecked(false);
            rbLoginGreek.setChecked(true);
        }
        customProgressDialog = new CustomProgressDialog();

        edLoginUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                username = edLoginUserName.getText().toString();
                if(!username.isEmpty() && !password.isEmpty()){
                   // Boolean isEmail = isValidEmailAddress(username);
                    //if(isEmail){
                        btLoginSubmit.setEnabled(true);
                        btLoginSubmit.setBackgroundColor(getColor(R.color.buttonEnable));
                    //}
                }else{
                    btLoginSubmit.setEnabled(false);
                    btLoginSubmit.setBackgroundColor(getColor(R.color.buttonDisable));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password = edLoginPassword.getText().toString();
                if(!username.isEmpty() && !password.isEmpty()){
                    //Boolean isEmail = isValidEmailAddress(username);
                    //if(isEmail){
                        btLoginSubmit.setEnabled(true);
                        btLoginSubmit.setBackgroundColor(getColor(R.color.buttonEnable));
                    //}
                }else{
                    btLoginSubmit.setEnabled(false);
                    btLoginSubmit.setBackgroundColor(getColor(R.color.buttonDisable));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @OnClick(R.id.btLoginSubmit)
    public void onClickLogin(){
        username = edLoginUserName.getText().toString();
        //System.out.println("username===="+username);
        password = edLoginPassword.getText().toString();
        System.out.println("password===="+password);
        //Log.e(TAG, "password===="+password);
        Boolean isEmail = isValidEmailAddress(username);

        if(!username.isEmpty() && !password.isEmpty()){
            if(isEmail){
                customProgressDialog.show(LoginActivity.this);
                if(AppUtils.isNetworkConnected(this)){
                    (new LoginActivity.LoginTask()).execute("");
                }
            }else{
                AppUtils.msg(LoginActivity.this, getString(R.string.invalid_email), null, false);
            }
        }else{
            AppUtils.msg(LoginActivity.this, getString(R.string.invalid_username_password), null, false);
        }

        //Intent intent = new Intent(this,SwitchBusinessActivity.class);
        //startActivity(intent);
    }

    @OnClick({R.id.rbLoginEnglish, R.id.rbLoginGreek})
    public void onChangeLanguage(View view){
        if(view.getId() == R.id.rbLoginEnglish){
            LanguageUtil.setLanguage(LoginActivity.this, "en");
        }
        else{
            LanguageUtil.setLanguage(LoginActivity.this, "el");
        }

        LoginActivity.this.recreate();
    }

    @OnClick(R.id.btLoginIconPassword)
    public void onChangeIconPassword(){
        System.out.println("edLoginPassword.getInputType(): "+ edLoginPassword.getInputType());
        isShowPass = !isShowPass;
        if(!isShowPass)
        {
            btLoginIconPassword.setImageResource(R.drawable.ic_eye_off_24);
            edLoginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }else{
            btLoginIconPassword.setImageResource(R.drawable.ic_eye_24);
            edLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            edLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
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
    private void onLayoutChange(boolean hasFocus){
        if (hasFocus){
            ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            //linearLayout.setLayoutParams(params);
            linearLayout.requestLayout();
        }
        else{
            ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
            float dip = 420f;
            Resources r = getResources();
            float px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dip,
                    r.getDisplayMetrics()
            );
            params.height = (int) px;
            //linearLayout.setLayoutParams(params);
            linearLayout.requestLayout();
        }
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

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email)
                ;
        return m.matches();
    }

    private class LoginTask extends AsyncTask<String, Void, Void> {

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
                result = services.login(username, password);
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(LoginActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(LoginActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(LoginActivity.this, getString(R.string.system_error), null, false);
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
                    Log.e(TAG, "result code=="+code);
                    if(code.equals(ResultCode.OK.getLabel())){
                        String access_token = result.getString("access_token");
                        String refresh_token = result.getString("refresh_token");
                        String exp = result.getString("exp");
                        Storage.setToken(LoginActivity.this, access_token);
                        Storage.setRefreshToken(LoginActivity.this, refresh_token);
                        Storage.setExpTime(LoginActivity.this, exp);

                        JSONArray organisations = result.getJSONArray("organisations");
                        Storage.setOrganisations(LoginActivity.this, organisations.toString());

                        Intent intent = new Intent(LoginActivity.this, SwitchBusinessActivity.class);
                        startActivity(intent);
                    }
                    else if(code.equals(ResultCode.INVALIDLOGINEXCEPTION.getLabel())){
                        AppUtils.msg(LoginActivity.this, getString(R.string.invalid_login), null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else if(!exception){
                AppUtils.msg(LoginActivity.this, getString(R.string.login_failed), null, false);
            }
        }
    }

}