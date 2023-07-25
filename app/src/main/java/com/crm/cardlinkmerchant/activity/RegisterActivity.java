package com.crm.cardlinkmerchant.activity;

import static com.crm.cardlinkmerchant.utils.LanguageUtil.initLanguage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.adapters.SpinnerAdapter;
import com.crm.cardlinkmerchant.model.SendingSmsResult;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.services.ServiceUtils;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.Constants;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.crm.cardlinkmerchant.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "REGISTER_ACT";
    @BindView(R.id.tvHeaderTitle)
    TextView tvHeaderTitle;

//    @BindView(R.id.edRegisterFirstName)
//    EditText edRegisterFirstName;
//
//    @BindView(R.id.edRegisterSecondName)
//    EditText edRegisterSecondName;

    @BindView(R.id.edRegisterPhoneNumber)
    EditText edRegisterPhoneNumber;

//    @BindView(R.id.llCustomerInfo)
//    LinearLayout llCustomerInfo;

    @BindView(R.id.btRegisterSubmit)
    TextView btRegisterSubmit;

    @BindView(R.id.snRegisterCountry)
    Spinner snRegisterCountry;
    CustomProgressDialog customProgressDialog;

//    @BindView(R.id.rlRegisterFirstName)
//    RelativeLayout rlRegisterFirstName;
//
//    @BindView(R.id.rlRegisterSecondName)
//    RelativeLayout rlRegisterSecondName;

    @BindView(R.id.rlRegisterPhoneNumber)
    RelativeLayout rlRegisterPhoneNumber;

    private Boolean isCheckAccept = true;
    private Boolean isValid = false;

    private String first_name = "";
    private String second_name = "";
    private String phone_number = "";
    private String country_code = "";
    private String contact_type = "PERSON";
    private String phone_type = "MOBILE";
    private String contact_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguage(this);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initScreen();


        tvHeaderTitle.setText(getString(R.string.new_customer_registration));


        customProgressDialog = new CustomProgressDialog();


//        edRegisterSecondName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus){
//                    fullSceen();
//                }
//            }
//        });
//        edRegisterFirstName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                first_name = edRegisterFirstName.getText().toString();
//                if (!first_name.isEmpty()) {
//                    rlRegisterFirstName.setBackgroundResource(R.drawable.field_border);
//                } else {
//                    rlRegisterFirstName.setBackgroundResource(R.drawable.field_backgound_error);
//                }
//                if (phone_number.isEmpty() || first_name.isEmpty() || second_name.isEmpty()) {
//                    btRegisterSubmit.setBackgroundColor(getColor(R.color.buttonDisable));
//                } else {
//                    btRegisterSubmit.setBackgroundColor(getColor(R.color.buttonEnable));
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//        edRegisterSecondName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                second_name = edRegisterSecondName.getText().toString();
//                if (!second_name.isEmpty()) {
//                    rlRegisterSecondName.setBackgroundResource(R.drawable.field_border);
//                } else {
//                    rlRegisterSecondName.setBackgroundResource(R.drawable.field_backgound_error);
//                }
//                if (phone_number.isEmpty() || first_name.isEmpty() || second_name.isEmpty()) {
//                    btRegisterSubmit.setBackgroundColor(getColor(R.color.buttonDisable));
//                } else {
//                    btRegisterSubmit.setBackgroundColor(getColor(R.color.buttonEnable));
//                }
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        edRegisterPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phone_number = edRegisterPhoneNumber.getText().toString();

                if (!phone_number.isEmpty()) {
                    rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_border);

                } else {
                    rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_backgound_error);

                }
                if (phone_number.isEmpty()) {
                    btRegisterSubmit.setBackgroundColor(getColor(R.color.buttonDisable));
                } else {
                    btRegisterSubmit.setBackgroundColor(getColor(R.color.buttonEnable));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        String[] countries = new String[]{"Greek", "Cyprus"};
        int flags[] = {R.mipmap.gr, R.mipmap.cy};
        //ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(this, R.layout.custom_spinner_text, items );
        //langAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getApplicationContext(), flags, countries);
        snRegisterCountry.setAdapter(spinnerAdapter);
        snRegisterCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String country = adapterView.getSelectedItem().toString();
                if (country.equals("Greek")) {
                    country_code = "GRC";
                    InputFilter[] filterArray = new InputFilter[1];
                    filterArray[0] = new InputFilter.LengthFilter(10);
                    edRegisterPhoneNumber.setFilters(filterArray);
                } else if (country.equals("Cyprus")) {
                    country_code = "CYP";
                    InputFilter[] filterArray = new InputFilter[1];
                    filterArray[0] = new InputFilter.LengthFilter(8);
                    edRegisterPhoneNumber.setFilters(filterArray);
                }
                rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_border);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean checkPhoneNumber(String phone) {
        Boolean check = true;
        if (!phone.isEmpty()) {
            if (country_code.equals("CYP") && phone.length() != 8) {
                check = false;
                AppUtils.msg(RegisterActivity.this, getString(R.string.invalid_phone_number).replace("%s", "8"), null, false);
            } else if (country_code.equals("GRC") && phone.length() != 10) {
                check = false;
                AppUtils.msg(RegisterActivity.this, getString(R.string.invalid_phone_number).replace("%s", "10"), null, false);
            }
        }
        return check;
    }

    @OnClick(R.id.btRegisterSubmit)
    public void onClickRegister() {
        isValid = onValidations();
        if (isValid) {
//            customProgressDialog.show(this);
//            if (AppUtils.isNetworkConnected(this)) {
//                (new RegisterActivity.CreateContactTask()).execute("");
//            }
            SendSmsTask sendSmsTask = new SendSmsTask();
            sendSmsTask.execute();
        }
        //Intent intent = new Intent(this, VerifyOTPActivity.class);
        //activityResultLaunch.launch(intent);
    }

//    @OnClick(R.id.termAndConditionLink)
//    public void onClickSendTermAndCondition() {
//        SendEmailTask sendEmailTask = new SendEmailTask();
//        sendEmailTask.execute();
//    }

//    public Boolean onValidations() {
//        Boolean checkValid = false;
//        first_name = edRegisterFirstName.getText().toString();
//        second_name = edRegisterSecondName.getText().toString();
//        //country_code = "CYP";//hardcode
//        phone_number = edRegisterPhoneNumber.getText().toString();
//
//        if (!first_name.isEmpty() && !second_name.isEmpty() && !country_code.isEmpty() && !phone_number.isEmpty() && isCheckAccept) {
//            if (!checkPhoneNumber(phone_number)) {
//                checkValid = false;
//                rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_backgound_error);
//            } else {
//                checkValid = true;
//                rlRegisterFirstName.setBackgroundResource(R.drawable.field_border);
//                rlRegisterSecondName.setBackgroundResource(R.drawable.field_border);
//                rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_border);
//            }
//        } else {
//            if (first_name.isEmpty()) {
//                rlRegisterFirstName.setBackgroundResource(R.drawable.field_backgound_error);
//            }
//            if (second_name.isEmpty()) {
//                rlRegisterSecondName.setBackgroundResource(R.drawable.field_backgound_error);
//            }
//            if (phone_number.isEmpty()) {
//                rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_backgound_error);
//            }
////            if(!ckbRegisterAccept.isChecked()){
////                ckbRegisterAccept.setButtonTintList(getColorStateList(R.color.colorAccent));
////            }
//            checkValid = false;
//        }
//
//        return checkValid;
//    }

    public Boolean onValidations() {
        Boolean checkValid = true;
        phone_number = edRegisterPhoneNumber.getText().toString();
        if (phone_number.isEmpty()) {
            checkValid = false;
            rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_backgound_error);
        }else {
            if (!checkPhoneNumber(phone_number)) {
                checkValid = false;
                rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_backgound_error);
            }
        }
        return checkValid;
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_CODE.VERIFY_OTP_OK) {

                        if (AppUtils.isNetworkConnected(RegisterActivity.this)) {
                            new SignUpOrganisationTask().execute("");
                        }
                    } else if (result.getResultCode() == RESULT_CODE.VERIFY_OTP_FAILED) {
                        AppUtils.msg(RegisterActivity.this, "Verify OTP failed", new CustomDialog.OKListener() {
                            @Override
                            public void onOk() {
                                finish();
                            }

                            @Override
                            public void onCancel() {

                            }
                        }, false);
                    }
                }
            });

    @OnClick({R.id.ivHeaderExit, R.id.ivHeaderBack})
    public void onExit(View view) {
        super.finish();
    }

//    @OnClick(R.id.ckbRegisterAccept)
//    public void onClickAccept(){
//        if(ckbRegisterAccept.isChecked()){
//            isCheckAccept = true;
//        }else{
//            isCheckAccept = false;
//        }
//    }

    private void initScreen() {
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        // Hide the status bar and navigation ba
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
        if (hasFocus) {
            fullSceen();
        }
    }

    private void fullSceen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private static ObjectMapper initMapper() {
        ObjectMapper mapper = new ObjectMapper();

        //mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return mapper;
    }

    private class CreateContactTask extends AsyncTask<String, Void, Void> {

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

                result = services.createContact(first_name, second_name, contact_type, country_code, phone_type, phone_number);
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(), TAG, "Exception stackTrace: " + stackTrace);

                if (e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())) {
                    AppUtils.msg(RegisterActivity.this, getString(R.string.cannot_connect_server), null, false);
                } else if (e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())) {
                    AppUtils.msg(RegisterActivity.this, getString(R.string.connect_timeout), null, false);
                } else {
                    AppUtils.msg(RegisterActivity.this, getString(R.string.system_error), null, false);
                }
                exception = true;
                customProgressDialog.dismiss();
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            customProgressDialog.dismiss();

            if (result != null && result.has("code")) {
                try {
                    String code = result.getString("code");
                    AppUtils.console(getApplicationContext(), TAG, "onPostExecute createContact result code==" + code);

                    if (code.equals(ResultCode.OK.getLabel())) {
                        contact_id = result.getString("id");
                        if (contact_id != null && !contact_id.isEmpty()) {
                            Intent intent = new Intent(RegisterActivity.this, VerifyOTPActivity.class);
                            intent.putExtra("contact_id", contact_id);
                            intent.putExtra("phone_number", phone_number);
                            activityResultLaunch.launch(intent);
                        }
                    } else if (code.equals(ResultCode.READY_EXISTS_FOR_ANOTHER_CONTAC_TEXCEPTION.getLabel())) {
                        AppUtils.msg(RegisterActivity.this, getString(R.string.register_fail_contact_exists), null, false);
                    } else {
                        AppUtils.msg(RegisterActivity.this, getString(R.string.system_error), null, false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String stackTrace = Log.getStackTraceString(e);
                    AppUtils.console(getApplicationContext(), TAG, "Exception stackTrace: " + stackTrace);
                }
            } else if (!exception) {
                AppUtils.msg(RegisterActivity.this, "Failed to create an account", null, false);
            }
        }
    }

    private class SignUpOrganisationTask extends AsyncTask<String, Void, Void> {

        boolean success = false;
        boolean exception = false;
        JSONObject result = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(RegisterActivity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());

                result = services.signUpOrganisation(contact_id);
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(), TAG, "Exception stackTrace: " + stackTrace);

                if (e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())) {
                    AppUtils.msg(RegisterActivity.this, getString(R.string.cannot_connect_server), null, false);
                } else if (e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())) {
                    AppUtils.msg(RegisterActivity.this, getString(R.string.connect_timeout), null, false);
                } else {
                    AppUtils.msg(RegisterActivity.this, getString(R.string.system_error), null, false);
                }
                exception = true;
                customProgressDialog.dismiss();
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            customProgressDialog.dismiss();

            if (result != null && result.has("code")) {
                try {
                    String code = result.getString("code");
                    AppUtils.console(getApplicationContext(), TAG, "onPostExecute createContact result code==" + code);

                    if (code.equals(ResultCode.OK.getLabel())) {
                        AppUtils.msg(RegisterActivity.this, getString(R.string.register_successful), new CustomDialog.OKListener() {
                            @Override
                            public void onOk() {
                                finish();
                            }

                            @Override
                            public void onCancel() {

                            }
                        }, true);
                    } else {
                        AppUtils.msg(RegisterActivity.this, getString(R.string.system_error), null, false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String stackTrace = Log.getStackTraceString(e);
                    AppUtils.console(getApplicationContext(), TAG, "Exception stackTrace: " + stackTrace);
                }
            } else if (!exception) {
                AppUtils.msg(RegisterActivity.this, "Failed to create an account", null, false);
            }
        }
    }

    private class SendSmsTask extends AsyncTask<String, Void, Void> {
        String sendSmsResult;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(RegisterActivity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String phoneNumber = edRegisterPhoneNumber.getText().toString();
                String message = "Please click on the link to register: ";
                message = message + Constants.CrmConfig.REGISTER_PORTAL_URL;
                sendSmsResult = ServiceUtils.sendCardlinkSmsPostRequest(getApplicationContext(), phoneNumber, message);
                AppUtils.console(getApplicationContext(), TAG, "sendToPhoneNumber sendToPhoneNumber = " + phoneNumber);
            } catch (Exception e) {
                e.printStackTrace();

                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(), TAG, "Exception stackTrace: " + stackTrace);

                if (e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())) {
                    AppUtils.msg(RegisterActivity.this, getString(R.string.cannot_connect_server));
                } else if (e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())) {
                    AppUtils.msg(RegisterActivity.this, getString(R.string.connect_timeout));
                } else {
                    AppUtils.msgSystemError(RegisterActivity.this);
                }
                customProgressDialog.dismiss();
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AppUtils.console(getApplicationContext(), TAG, "sendSmsResult: " + sendSmsResult);
            if (sendSmsResult != null && !sendSmsResult.isEmpty()) {
                if (sendSmsResult.equals("OK")) {
                    (new CustomDialog(RegisterActivity.this, null, getString(R.string.send_sms_success), getString(R.string.btn_ok), "", true)).show();
                    customProgressDialog.dismiss();
                } else {
                    try {
                        SendingSmsResult result = StringUtils.getSendingSmsResponse(sendSmsResult);
                        (new CustomDialog(RegisterActivity.this, null, result.getMessage(), getString(R.string.btn_ok), "", false)).show();
                        customProgressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        (new CustomDialog(RegisterActivity.this, null, getString(R.string.send_sms_fail), getString(R.string.btn_ok), "", false)).show();
                        customProgressDialog.dismiss();
                    }
                }
            }
        }
    }

    private class SendEmailTask extends AsyncTask<String, Void, Void> {
        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        public SendEmailTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(RegisterActivity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());
                String content = makeContentCommnucation();
                String subject = "Cardlink Term And Conditions";
                result = services.sendCommunication(subject,content);
            }catch(Exception e){
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(RegisterActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(RegisterActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(RegisterActivity.this, getString(R.string.system_error), null, false);
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
                        AppUtils.msg(RegisterActivity.this, getString(R.string.send_email_success), null, true);
                    }
                    else{
                        AppUtils.msg(RegisterActivity.this, getString(R.string.send_email_fail), null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String stackTrace = Log.getStackTraceString(e);
                    AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);
                }
            }else{
                AppUtils.msg(RegisterActivity.this, getString(R.string.send_email_fail), null, false);
            }
        }
    }

    private String makeContentCommnucation(){
        String content ="Please click on the link below: <br>";
        content = content + Constants.CrmConfig.TERM_AND_CONDITION_MERCHANT_URL;
        return content;
    }
}