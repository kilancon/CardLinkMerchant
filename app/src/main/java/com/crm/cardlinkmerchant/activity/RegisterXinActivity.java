package com.crm.cardlinkmerchant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.crm.cardlinkmerchant.MyEmailSender;
import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.Constants;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterXinActivity extends AppCompatActivity {
    private final String TAG = "REGISTER_ACT";
    @BindView(R.id.tvHeaderTitle)
    TextView tvHeaderTitle;
    @BindView(R.id.btRegisterSubmit)
    TextView btRegisterSubmit;
    @BindView(R.id.edRegisterEmail)
    EditText edRegisterEmail;

    private String email = "";
    private CustomProgressDialog customProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_xin);
        ButterKnife.bind(this);
        initScreen();

        tvHeaderTitle.setText(getString(R.string.new_customer_registration));
        btRegisterSubmit.setBackgroundColor(getColor(R.color.colorXin));
        customProgressDialog = new CustomProgressDialog();
    }

    @OnClick(R.id.btRegisterSubmit)
    public void onClickRegister(){
        email = edRegisterEmail.getText().toString();
        if(!email.isEmpty() && isValidEmailAddress(email)){
            customProgressDialog.show(this);
            if(AppUtils.isNetworkConnected(this)) {
                new SendEmailTask().execute();
            }
        }
        else{
            AppUtils.msg(this, getString(R.string.invalid_email));
        }

//        AppUtils.msg(this, "Failed");
//        AppUtils.msg(this, "Success", null, true);
//        AppUtils.msgSystemError(this);
    }

    @OnClick({R.id.ivHeaderExit, R.id.ivHeaderBack})
    public void onExit(View view) {
        super.finish();
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private class SendEmailTask extends AsyncTask<Void, Void, Void>{
        String sendEmailResult;
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                MyEmailSender emailSender = new MyEmailSender(Constants.SdkConfig.EMAIL_SENDER, Constants.SdkConfig.EMAIL_SENDER_PASS);
                emailSender.sendEmailWithAttachments("co san trong class Emailsender", Constants.SdkConfig.EMAIL_SENDER, Storage.getBusinessName(RegisterXinActivity.this),email);
                sendEmailResult = "OK";
            }catch (Exception e){
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(), TAG, "Exception stackTrace: " + stackTrace);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (sendEmailResult.equals("OK")){
                AppUtils.msg(RegisterXinActivity.this, "Email sent to " + email, new CustomDialog.OKListener() {
                    @Override
                    public void onOk() {
                        RegisterXinActivity.this.finish();
                    }
                    @Override
                    public void onCancel() {
                    }
                }, true);
            }
            else{
                AppUtils.msg(RegisterXinActivity.this, "loi gi khum biet");
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
    @OnClick({R.id.ivHeaderBack, R.id.ivHeaderExit})
    public void onBack(View view){
        super.finish();
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