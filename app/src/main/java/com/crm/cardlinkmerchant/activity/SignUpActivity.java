package com.crm.cardlinkmerchant.activity;

import static com.crm.cardlinkmerchant.utils.LanguageUtil.initLanguage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.adapters.SpinnerAdapter;
import com.crm.cardlinkmerchant.model.Customer;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;
import com.crm.cardlinkmerchant.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {
    private final String TAG = "REGISTER_ACT";
    @BindView(R.id.tvHeaderTitle)
    TextView tvHeaderTitle;


    @BindView(R.id.edRegisterPhoneNumber)
    EditText edRegisterPhoneNumber;


    @BindView(R.id.btSendOTPSubmit)
    TextView btSendOTPSubmit;

    @BindView(R.id.snRegisterCountry)
    Spinner snRegisterCountry;
    CustomProgressDialog customProgressDialog;

    @BindView(R.id.rlRegisterPhoneNumber)
    RelativeLayout rlRegisterPhoneNumber;

    private Boolean isCheckAccept = true;
    private Boolean isValid = false;


    private String phone_number="";
    private String country_code="";
    private Customer customer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguage(this);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        initScreen();

        tvHeaderTitle.setText(getString(R.string.signup_and_verify));


        customProgressDialog = new CustomProgressDialog();


//        edRegisterSecondName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus){
//                    fullSceen();
//                }
//            }
//        });



        edRegisterPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phone_number = edRegisterPhoneNumber.getText().toString();

                if(!phone_number.isEmpty()){
                    rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_border);

                }else{
                    rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_backgound_error);

                }
                if(phone_number.isEmpty()){
                    btSendOTPSubmit.setBackgroundColor(getColor(R.color.buttonDisable));
                }
                else{
                    btSendOTPSubmit.setBackgroundColor(getColor(R.color.buttonEnable));
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
                if(country.equals("Greek")){
                    country_code = "GRC";
                    InputFilter[] filterArray = new InputFilter[1];
                    filterArray[0] = new InputFilter.LengthFilter(10);
                    edRegisterPhoneNumber.setFilters(filterArray);
                }else if(country.equals("Cyprus")){
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

    private boolean checkPhoneNumber(String phone){
        Boolean check = true;
        if(!phone.isEmpty()){
            if(country_code.equals("CYP") && phone.length() != 8 ){
                check = false;
                AppUtils.msg(SignUpActivity.this, getString(R.string.invalid_phone_number).replace("%s", "8"), null, false);
            }else if(country_code.equals("GRC") && phone.length() != 10){
                check = false;
                AppUtils.msg(SignUpActivity.this, getString(R.string.invalid_phone_number).replace("%s", "10"), null, false);
            }
        }
        return check;
    }
    @OnClick(R.id.btSendOTPSubmit)
    public void onClickSendOTP(){
        isValid = onValidations();
        if(isValid){
            if(AppUtils.isNetworkConnected(this)){
                (new SignUpActivity.GetCustomerTask()).execute("");
            }
        }
        //Intent intent = new Intent(this, VerifyOTPActivity.class);
        //activityResultLaunch.launch(intent);
    }

    public Boolean onValidations(){
        Boolean checkValid = false;

        //country_code = "CYP";//hardcode
        phone_number = edRegisterPhoneNumber.getText().toString();

        if( !country_code.isEmpty() && !phone_number.isEmpty() && isCheckAccept){
            if(!checkPhoneNumber(phone_number)){
                checkValid = false;
                rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_backgound_error);
            }else {
                checkValid = true;
                rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_border);
            }
        }else {
            if(phone_number.isEmpty()){
                rlRegisterPhoneNumber.setBackgroundResource(R.drawable.field_backgound_error);
            }
//            if(!ckbRegisterAccept.isChecked()){
//                ckbRegisterAccept.setButtonTintList(getColorStateList(R.color.colorAccent));
//            }
            checkValid = false;
        }

        return checkValid;
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_CODE.VERIFY_OTP_OK) {

                        if(AppUtils.isNetworkConnected(SignUpActivity.this) && customer != null){
                            new SignUpOrganisationTask().execute("");
                        }
                    } else if(result.getResultCode() == RESULT_CODE.VERIFY_OTP_FAILED) {
                        AppUtils.msg(SignUpActivity.this, "Verify OTP failed", new CustomDialog.OKListener() {
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
    public void onExit(View view){
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

    private class GetCustomerTask extends AsyncTask<String, Void, Void> {

        boolean success = false;
        boolean exception = false;
        boolean isReadySignUp = false;
        JSONObject result = null;
        JSONObject result2 = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(SignUpActivity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(SignUpActivity.this);
                result = services.getCustomer(phone_number, true);
                if (result != null && result.has("code")){
                    try {
                        String code = result.getString("code");
                        AppUtils.console(SignUpActivity.this,TAG, "getCustomer="+result.toString());
                        if(code.equals(ResultCode.OK.getLabel())){
                            JSONArray content = result.getJSONArray("content");

                            List<Customer> _customer = new ArrayList<>();
                            //outlets.clear();
                            if(content != null){
                                for (int i=0; i< content.length() ; i++){
                                    ObjectMapper mapper = AppUtils.initMapper();
                                    JSONObject org = (JSONObject) content.get(i);
                                    Customer customer = mapper.readValue(org.toString(), Customer.class);
                                    _customer.add(customer);
                                }
                            }
                            AppUtils.console(SignUpActivity.this,TAG, "getCustomer _customer size="+_customer.size());
                            if (_customer.size() > 0){
                                isReadySignUp = true;
                            }
                            else{
                                result2 = services.getCustomer(phone_number, false);
                                AppUtils.console(SignUpActivity.this,TAG, "getCustomer2="+result2.toString());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(SignUpActivity.this,TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(SignUpActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(SignUpActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(SignUpActivity.this, getString(R.string.system_error), null, false);
                }
                exception = true;
                customProgressDialog.dismiss();
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            customProgressDialog.dismiss();
            if (result2 != null && result2.has("code")){
                try {
                    String code = result2.getString("code");
                    if(code.equals(ResultCode.OK.getLabel())){
                        JSONArray content = result2.getJSONArray("content");

                        List<Customer> _customer = new ArrayList<>();
                        //outlets.clear();
                        if(content != null){
                            for (int i=0; i< content.length() ; i++){
                                ObjectMapper mapper = AppUtils.initMapper();
                                JSONObject org = (JSONObject) content.get(i);
                                Customer customer = mapper.readValue(org.toString(), Customer.class);
                                _customer.add(customer);
                            }
                        }
                        if (_customer.size() > 0){
                            customer = _customer.get(0);
                            Intent intent = new Intent(SignUpActivity.this, VerifyOTPActivity.class);
                            intent.putExtra("contact_id", customer.getId());
                            intent.putExtra("is_flow", "signup");
                            activityResultLaunch.launch(intent);
                        }
                        else{
                            AppUtils.msg(SignUpActivity.this, getString(R.string.customer_not_register), null, false);
                        }
                    }
                    else {
                        AppUtils.msg(SignUpActivity.this, getString(R.string.system_error), null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            else if(isReadySignUp){
                AppUtils.msg(SignUpActivity.this, getString(R.string.customer_ready_sigup), null, false);
            }
            else if(!exception){
                AppUtils.msg(SignUpActivity.this, getString(R.string.system_error), null, false);
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
            customProgressDialog.show(SignUpActivity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());

                result = services.signUpOrganisation(customer.getId());
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(SignUpActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(SignUpActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(SignUpActivity.this, getString(R.string.system_error), null, false);
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
                    AppUtils.console(getApplicationContext(),TAG, "onPostExecute createContact result code==" + code);

                    if(code.equals(ResultCode.OK.getLabel())){
                        AppUtils.msg(SignUpActivity.this, getString(R.string.sign_up_successful), new CustomDialog.OKListener() {
                            @Override
                            public void onOk() {
                                finish();
                            }
                            @Override
                            public void onCancel() {

                            }
                        }, true);
                    }
                    else{
                        AppUtils.msg(SignUpActivity.this, getString(R.string.system_error), null, false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String stackTrace = Log.getStackTraceString(e);
                    AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);
                }
            }
            else if(!exception){
                AppUtils.msg(SignUpActivity.this, "Failed to create an account", null, false);
            }
        }
    }
}