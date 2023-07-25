package com.crm.cardlinkmerchant.activity;

import static com.crm.cardlinkmerchant.utils.LanguageUtil.getCurrentLanguage;
import static com.crm.cardlinkmerchant.utils.LanguageUtil.initLanguage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerifyOTPActivity extends AppCompatActivity {

    private final String TAG = "VERIFY_OTP_ATC";
    @BindView(R.id.tvHeaderTitle)
    TextView tvHeaderTitle;
    @BindView(R.id.edVerifyOTPCode)
    EditText edVerifyOTPCode;
    @BindView(R.id.rlVerifyOTPCode)
    RelativeLayout rlVerifyOTPCode;
    @BindView(R.id.txtRegisterVerifyOTPDesc1)
    TextView txtRegisterVerifyOTPDesc1;
    @BindView(R.id.btVerifyOTPSubmit)
    TextView btVerifyOTPSubmit;
    CustomProgressDialog customProgressDialog;

    private String otp;
    private String contact_id;
    private String phone_number = "";
    private String auth_otp;
    private String isFlow = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLanguage(this);
        setContentView(R.layout.activity_verify_otp);
        ButterKnife.bind(this);
        initScreen();
        customProgressDialog = new CustomProgressDialog();

        tvHeaderTitle.setText(getString(R.string.verify_title));
//        if(getCurrentLanguage(this).equals("el")){
//            tvHeaderTitle.setTextSize(18);
//        }

        contact_id = getIntent().getStringExtra("contact_id");
        phone_number = getIntent().getStringExtra("phone_number");
        isFlow = getIntent().getStringExtra("is_flow"); //purchase, signup, void_transaction

        isFlow = isFlow == null? "" : isFlow;
        edVerifyOTPCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                otp = edVerifyOTPCode.getText().toString();
                if(otp.isEmpty()){
                    rlVerifyOTPCode.setBackgroundResource(R.drawable.field_backgound_error);
                    btVerifyOTPSubmit.setBackgroundColor(getColor(R.color.buttonDisable));
                    btVerifyOTPSubmit.setEnabled(false);

                }else{
                    rlVerifyOTPCode.setBackgroundResource(R.drawable.field_border);
                    btVerifyOTPSubmit.setBackgroundColor(getColor(R.color.buttonEnable));
                    btVerifyOTPSubmit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if(contact_id != null && AppUtils.isNetworkConnected(this)){
            new GetOTPContactTask().execute("");
        }
    }

    @OnClick({R.id.ivHeaderBack})
    public void onBack(View view){
        super.finish();
    }
    @OnClick({R.id.ivHeaderExit})
    public void onExit(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btVerifyOTPSubmit)
    public void onClickVerifySubmit(){
        otp = edVerifyOTPCode.getText().toString();
        if(!otp.isEmpty()){
            rlVerifyOTPCode.setBackgroundResource(R.drawable.field_border);
//            if(otp.equals("21101989")){
//                setResult(RESULT_CODE.VERIFY_OTP_OK);
//                finish();
//            }
//            else{
                if(AppUtils.isNetworkConnected(this)){
                    new VerifyOTPTask().execute("");
                }
            //}


        }else{
            rlVerifyOTPCode.setBackgroundResource(R.drawable.field_backgound_error);
            AppUtils.msg(VerifyOTPActivity.this, "Invalid OTP", null, false);
        }
    }
    @OnClick(R.id.btVerifyOTPResend)
    public void onResendOTP(){
        if(contact_id != null && AppUtils.isNetworkConnected(this)){
            new GetOTPContactTask().execute("");
        }
    }

    private class GetOTPContactTask extends AsyncTask<String, Void, Void> {

        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(VerifyOTPActivity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());
                result = services.getOTPContact(contact_id, isFlow.equals("purchase")||isFlow.equals("void_transaction") ? true : false);
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(VerifyOTPActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(VerifyOTPActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(VerifyOTPActivity.this, getString(R.string.system_error), null, false);
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

                    if(code.equals(ResultCode.OK.getLabel())){
                        //Log.e(TAG, "result code=="+code);
                        phone_number = result.getString("obfuscated_value");
                        auth_otp = result.getString("auth_otp");
                        txtRegisterVerifyOTPDesc1.setText(getString(R.string.verify_desc_1) + " " + phone_number);
                    }
                    else if(code.equals(ResultCode.MANY_REQUESTS.getLabel())){
                        AppUtils.msg(VerifyOTPActivity.this, getString(R.string.many_requests), null, false);
                    }
                    else {
                        AppUtils.msg(VerifyOTPActivity.this, getString(R.string.system_error), null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(!exception){
                AppUtils.msg(VerifyOTPActivity.this, getString(R.string.system_error), null, false);
            }
        }
    }
    private class VerifyOTPTask extends AsyncTask<String, Void, Void> {

        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(VerifyOTPActivity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());
                result = services.verifyOTP(contact_id, auth_otp, otp, isFlow.equals("purchase")||isFlow.equals("void_transaction") ? true : false);
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(VerifyOTPActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(VerifyOTPActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(VerifyOTPActivity.this, getString(R.string.system_error), null, false);
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

                    if(code.equals(ResultCode.OK.getLabel())){
                        //Log.e(TAG, "result code=="+code);
                        setResult(RESULT_CODE.VERIFY_OTP_OK);
                        VerifyOTPActivity.this.finish();
                    }
                    else if(code.equals(ResultCode.MANY_REQUESTS.getLabel())){
                        AppUtils.msg(VerifyOTPActivity.this, getString(R.string.many_requests), null, false);
                    }
                    else if(code.equals(ResultCode.INVALIDLOGINEXCEPTION.getLabel())){
                        AppUtils.msg(VerifyOTPActivity.this, getString(R.string.invalid_otp), null, false);
                    }
                    else {
                        AppUtils.msg(VerifyOTPActivity.this, getString(R.string.system_error), null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(!exception){
                AppUtils.msg(VerifyOTPActivity.this, getString(R.string.system_error), null, false);
            }
        }
    }
    private void initScreen() {
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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

}