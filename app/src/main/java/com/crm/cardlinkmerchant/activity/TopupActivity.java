package com.crm.cardlinkmerchant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.fragments.Spend1Fragment;
import com.crm.cardlinkmerchant.model.Customer;
import com.crm.cardlinkmerchant.model.DeepLinkingResponse;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
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

public class TopupActivity extends AppCompatActivity {
    @BindView(R.id.tvHeaderTitle)
    TextView tvHeaderTitle;
    @BindView(R.id.edTopupPhoneNumber)
    EditText edTopupPhoneNumber;

    @BindView(R.id.ivTopupScanQRCode)
    ImageView ivTopupScanQRCode;

    @BindView(R.id.edTopupAmount)
    EditText edTopupAmount;

    @BindView(R.id.tvTopupWalletBalance)
    TextView tvTopupWalletBalance;

    @BindView(R.id.btTopupContinue)
    TextView btTopupContinue;

    private CustomProgressDialog customProgressDialog;
    private String cardNumber = null;
    private Double eWalletBalance = 0.00;
    private Boolean isVerifyOtp = false;
    private final String TAG = "TOP_UP";
    private String customerValue = "";
    private Customer customer;
    private double amountTopup = 0.00;
    private String spendMethod = "Cash"; //khum biet
    private Double requestAmount = 0.0;
    private Double amountDue= 0.00;
    private String clientTransactionId;
    private DeepLinkingResponse deepLinkingResponse;
    private Boolean isCancelTopupDeeplink = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);
        ButterKnife.bind(this);
        initScreen();
        customProgressDialog = new CustomProgressDialog();
        tvHeaderTitle.setText(getString(R.string.wallet_top_up));

        edTopupPhoneNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                setDisableOtherField();
                if ((event.getAction() == android.view.KeyEvent.ACTION_DOWN) &&
                        (keyCode == android.view.KeyEvent.KEYCODE_ENTER)) {
                    customerValue = edTopupPhoneNumber.getText().toString();
//                    hideSoftKeyboard(activity);
                    if(customerValue !=null && customerValue.length()<8){
                        AppUtils.msg(TopupActivity.this,getString(R.string.input_must_8_digis), null,false);
                    }
                    else{
                        if(AppUtils.isNetworkConnected(TopupActivity.this))
                            new GetCustomerTask().execute("");
                    }

                    return true;
                }
                return false;
            }
        });
    }
    
    @OnClick(R.id.btTopupContinue)
    public void onClickContinue(){
        if (customer == null){
            AppUtils.msg(TopupActivity.this, getString(R.string.cannot_find_customer_info), null, false);
            return;
        }
        String amountStr = edTopupAmount.getText().toString().replace(",","");
        amountTopup = Double.parseDouble(amountStr);
        AppUtils.console(TopupActivity.this, TAG, "amount top up=="+ amountTopup);
        new MakeTopupTask().execute("");
    }
    
    @OnClick({R.id.ivHeaderBack, R.id.ivHeaderExit})
    public void onBack(View view){
        super.finish();
    }

    private class GetCustomerTask extends AsyncTask<String, Void, Void> {

        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        JSONObject result2 = null;
        boolean isFoundCustomer = false;
        boolean isNotSigup = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(TopupActivity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(TopupActivity.this);
                result = services.getCustomer(customerValue, true);
                if (result != null && result.has("code")){
                    try {
                        String code = result.getString("code");
                        if(code.equals(ResultCode.OK.getLabel())){
                            JSONArray content = result.getJSONArray("content");

                            List<Customer> _customer = new ArrayList<>();
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
                                isFoundCustomer = true;
                            }
                            else{
                                result2 = services.getCustomer(customerValue, false);
                                //AppUtils.msg(TopupActivity.this, getString(R.string.cannot_find_customer_info), null, false);
                            }
                        }
                        else {
                            AppUtils.msg(TopupActivity.this, getString(R.string.system_error), null, false);
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
                AppUtils.console(TopupActivity.this,TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(TopupActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(TopupActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(TopupActivity.this, getString(R.string.system_error), null, false);
                }
                exception = true;
                customProgressDialog.dismiss();
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            customProgressDialog.dismiss();
            if(isFoundCustomer){
                Customer.Financial financial = customer.getFinancials();
                if(financial != null){
                    eWalletBalance = financial.getWallet().getBalance();
                    tvTopupWalletBalance.setText(getString(R.string.euro_symbol) + StringUtils.customFormat(eWalletBalance));
                }

//                setEnableOtherField();

            }
            else if (result2 != null && result2.has("code")){
                try {
                    String code = result2.getString("code");
                    if(code.equals(ResultCode.OK.getLabel())){
                        JSONArray content = result2.getJSONArray("content");

                        List<Customer> _customer = new ArrayList<>();
                        if(content != null){
                            for (int i=0; i< content.length() ; i++){
                                ObjectMapper mapper = AppUtils.initMapper();
                                JSONObject org = (JSONObject) content.get(i);
                                Customer customer = mapper.readValue(org.toString(), Customer.class);
                                _customer.add(customer);
                            }
                        }
                        if (_customer.size() > 0){
                            CustomDialog customDialog = new CustomDialog(TopupActivity.this, new CustomDialog.OKListener() {
                                @Override
                                public void onOk() {
                                    Intent intent = new Intent(TopupActivity.this.getApplicationContext(), SignUpActivity.class);
//                                    activityResultLaunch.launch(intent);
                                }

                                @Override
                                public void onCancel() {

                                }
                            }, getString(R.string.customer_not_signup),getString(R.string.btn_ok),getString(R.string.cancel), false);
                            customDialog.show();
                        }
                        else{
                            CustomDialog customDialog = new CustomDialog(TopupActivity.this, new CustomDialog.OKListener() {
                                @Override
                                public void onOk() {
                                    Intent intent = new Intent(TopupActivity.this.getApplicationContext(), RegisterActivity.class);
//                                    activityResultLaunch.launch(intent);
                                }

                                @Override
                                public void onCancel() {

                                }
                            }, getString(R.string.cannot_find_customer_info_register),getString(R.string.btn_ok),getString(R.string.cancel), false);
                            customDialog.show();
                        }
                    }
                    else {
                        AppUtils.msg(TopupActivity.this, getString(R.string.system_error), null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            else if(!exception && !isFoundCustomer){
                AppUtils.msg(TopupActivity.this, getString(R.string.system_error), null, false);
            }
        }
    }

    private class MakeTopupTask extends AsyncTask<String, Void, Void> {
        String msg = getString(R.string.system_error);
        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(TopupActivity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
//            String outletId = Storage.getOutlet(Spend1Fragment.this.TopupActivity.this);
            try {
                CrmServices services = new CrmServices(TopupActivity.this);
                result = services.makeTopup(customer.getFinancials().getWallet().getId(), amountTopup, AppUtils.get16digitRandom(), ""+System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(TopupActivity.this,TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    msg = getString(R.string.cannot_connect_server);
                    //AppUtils.msg(TopupActivity.this, getString(R.string.cannot_connect_server), null);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    msg = getString(R.string.connect_timeout);
                    //AppUtils.msg(TopupActivity.this, getString(R.string.connect_timeout), null);
                }else {
                    msg = getString(R.string.system_error);
                    //AppUtils.msg(TopupActivity.this, getString(R.string.system_error), null);
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
//                        AppUtils.msg(TopupActivity.this, getString(R.string.spend_sucessful), new CustomDialog.OKListener() {
//                            @Override
//                            public void onOk() {
//                                TopupActivity.this.finish();
//                            }
//
//                            @Override
//                            public void onCancel() {
//
//                            }
//                        }, true);
                        success = true ;
                        // phai put them linh tinh vao de in receipt
                        Intent intent = new Intent(TopupActivity.this, TyxoaActivity.class);
                        startActivity(intent);
                    }
                    else if(code.equals(ResultCode.MANY_REQUESTS.getLabel())){
                        msg = getString(R.string.many_requests);
                    }
                    else {
                        msg = getString(R.string.spend_failed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!success){
                CustomDialog customDialog = new CustomDialog(TopupActivity.this, new CustomDialog.OKListener() {
                    @Override
                    public void onOk() {
//                        checkAndCallDeepLinkCancelTopup();
                    }

                    @Override
                    public void onCancel() {

                    }
                }, msg, getString(R.string.btn_ok),"",false);
                customDialog.setCanceledOnTouchOutside(false);
                customDialog.show();
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