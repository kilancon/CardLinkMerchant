package com.crm.cardlinkmerchant.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.telephony.euicc.DownloadableSubscription;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.activity.BarcodeScannerActivity;
import com.crm.cardlinkmerchant.activity.RegisterActivity;
import com.crm.cardlinkmerchant.activity.SelectPurchaseMethodActivity;
import com.crm.cardlinkmerchant.activity.SignUpActivity;
import com.crm.cardlinkmerchant.activity.Spend3Activity;
import com.crm.cardlinkmerchant.activity.SplashActivity;
import com.crm.cardlinkmerchant.activity.SwitchBusinessActivity;
import com.crm.cardlinkmerchant.activity.TransactionManagementActivity;
import com.crm.cardlinkmerchant.activity.TyxoaActivity;
import com.crm.cardlinkmerchant.activity.VerifyOTPActivity;
import com.crm.cardlinkmerchant.model.Customer;
import com.crm.cardlinkmerchant.model.DeepLinkingResponse;
import com.crm.cardlinkmerchant.model.Offer;
import com.crm.cardlinkmerchant.model.Organisation;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppConfig;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.Constants;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.LanguageUtil;
import com.crm.cardlinkmerchant.utils.MoneyTextWatcher;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;
import com.crm.cardlinkmerchant.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Spend1Fragment extends Fragment {
    Unbinder unbinder;
    @BindView(R.id.edFragmentSpend1PhoneNumber)
    EditText edFragmentSpend1PhoneNumber;

    @BindView(R.id.ivFragmentSpend1ScanQRCode)
    ImageView ivFragmentSpend1ScanQRCode;

    @BindView(R.id.edFragmentSpend1Amount)
    EditText edFragmentSpend1Amount;

    @BindView(R.id.tvFragmentSpend1EWalletBalance)
    TextView tvFragmentSpend1EWalletBalance;

    @BindView(R.id.edFragmentSpend1EWallet)
    EditText edFragmentSpend1EWallet;

    @BindView(R.id.tvFragmentSpend1AmountDue)
    TextView tvFragmentSpend1AmountDue;

    @BindView(R.id.swFragmentSpend1EWallet)
    Switch swFragmentSpend1EWallet;

    @BindView(R.id.btFragmentSpend1VerifyOtp)
    TextView btFragmentSpend1VerifyOtp;

    @BindView(R.id.btFragmentSpend1ProcessPurchase)
    TextView btFragmentSpend1ProcessPurchase;

    private CustomProgressDialog customProgressDialog;
    private String cardNumber = null;
    private Double eWalletBalance = 0.00;
    private Boolean isVerifyOtp = false;
    private final String TAG = "SPEND_1_FRAG";
    private String customerValue = "";
    private Customer customer;
    private double amountSpend = 0.00;
    private String spendMethod = "Cash"; //khum biet
    private Double requestAmount = 0.0;
    private Double amountDue= 0.00;
    private String clientTransactionId;
    private DeepLinkingResponse deepLinkingResponse;
    private Boolean isCancelTopupDeeplink = false;

    @OnClick(R.id.btFragmentSpend1ProcessPurchase)
    public void onSubmit(){
        if (customer == null){
            AppUtils.msg(getActivity(), getString(R.string.cannot_find_customer_info), null, false);
            return;
        }

        if (swFragmentSpend1EWallet.isChecked()){
            String requestAmountStr = edFragmentSpend1EWallet.getText().toString().replace(",","");
            if (!requestAmountStr.isEmpty()){
                requestAmount = Double.parseDouble(requestAmountStr);
                if (requestAmount > amountSpend){
                    AppUtils.msg(getActivity(),getString(R.string.request_amount_not_more_purchase),null, false);
                    return;
                }
                if (requestAmount > eWalletBalance){
                    AppUtils.msg(getActivity(),getString(R.string.request_amount_not_more_balance),null, false);
                    return;
                }
            }

        }

        String amountStr = edFragmentSpend1Amount.getText().toString().replace(",","");
        amountSpend = Double.parseDouble(amountStr);
        AppUtils.console(getContext(), TAG, "amount spend=="+ amountSpend);
        isCancelTopupDeeplink = false;
        if (amountSpend > 0){
            if (spendMethod.equals("Card") && amountDue>0){
                callDeeplinkTopup(amountDue);
            }
            else{
                AppUtils.paymentDialog(getActivity(), amountStr, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(view.getId() == R.id.tvCash) {
                            if (AppUtils.isNetworkConnected(getContext()))
                                new MakePurchaseTask().execute("");
                        }
                        else if(view.getId() == R.id.tvCard) {
                            callDeeplinkTopup(amountDue);
                        }
                    }
                });

            }

        }
    }

    public void callDeeplinkTopup(Double topupAmount){
        try{
            AppUtils.console(getContext(),TAG, "processPaymentCard topup amount: "+ topupAmount);
            String amount = "€"+StringUtils.customFormatPriceForGr(topupAmount);
            AppUtils.console(getContext(),TAG, "processPaymentCard topup amount: "+ amount);

            Intent paymentIntent = new Intent(Constants.SdkConfig.POS_APP_TOPUP_OPENLINK);
            JSONObject obj = new JSONObject();
            clientTransactionId = AppUtils.getRamdomBaseOnDatetime();
            try {
                obj.put("clientTransactionId", clientTransactionId);
                obj.put("type",Constants.SdkConfig.TOPUP_SALE);
                obj.put("currency","EUR");
                obj.put("price", amount);//"12,00"
                obj.put("tip","0,00");
                obj.put("installments",0);
                obj.put("language", LanguageUtil.getCurrentLanguage(getActivity()));
                obj.put("appLinkPackageName","com.crm.cardlinkmerchant.activity.Spend3Activity");
                obj.put("serviceId","");
                obj.put("referenceId","");
                obj.put("allowPOSsiblePrint",1);
                paymentIntent.putExtra("payment", obj.toString());
                AppUtils.console(getContext(),TAG, "putExtra payment: "+ obj.toString());
                activityResultLaunch.launch(paymentIntent);
                AppUtils.console(getContext(), TAG,"Call deeplink code="+Constants.SdkConfig.DEEPLINK_TOPUP_CODE+ " amount="+topupAmount + " type="+Constants.SdkConfig.TOPUP_SALE);
                AppUtils.console(getContext(), TAG,"Call deeplink code="+Constants.SdkConfig.DEEPLINK_TOPUP_CODE+ " deeplink amount="+(topupAmount * 100) + " type="+Constants.SdkConfig.TOPUP_SALE);
            } catch (JSONException e) {
                e.printStackTrace();
                AppUtils.console(getContext(), TAG,"Call deeplink code="+Constants.SdkConfig.DEEPLINK_TOPUP_CODE+ " exception msg="+e.getMessage());
                AppUtils.msg(getActivity(), getString(R.string.cannot_open_deeplink_app), null, false);
            }

        }
        catch (Exception e){
            AppUtils.console(getContext(), TAG, "Error open deeplink: "+ e.getMessage());
            AppUtils.msg(getActivity(), getString(R.string.cannot_open_deeplink_app), null, false);
        }
    }
    private void checkAndCallDeepLinkCancelTopup(){
        if(!spendMethod.equals("Card")){
            return;
        }
        try{
            AppUtils.console(getContext(),TAG, "callDeepLinkCancelTopup topup amount: "+ amountDue);
            Intent paymentIntent = new Intent(Constants.SdkConfig.POS_APP_TOPUP_OPENLINK);
            JSONObject obj = new JSONObject();
            try {
                obj.put("clientTransactionId", clientTransactionId);
                obj.put("type",Constants.SdkConfig.TOPUP_REVERSAL);
                obj.put("currency", "EUR");
                obj.put("bankId", deepLinkingResponse.getBankId());
                obj.put("posDateTime", deepLinkingResponse.getTransactionDate());
                obj.put("batchId",deepLinkingResponse.getBatchNumber());
                obj.put("posTransactionId",deepLinkingResponse.getTransactionId());
                obj.put("sequenceId",deepLinkingResponse.getReceiptNumber());
                obj.put("language", LanguageUtil.getCurrentLanguage(getActivity()));
                obj.put("appLinkPackageName","com.crm.cardlinkmerchant.Spend3Activity");
                obj.put("serviceId","");
                obj.put("referenceId", "");
            } catch (JSONException e) {
                e.printStackTrace();
                AppUtils.msg(getActivity(),getString(R.string.system_error), null, false);
            }
            paymentIntent.putExtra("reversal", obj.toString());
            isCancelTopupDeeplink = true;
            activityResultLaunch.launch(paymentIntent);
        }
        catch (Exception e){
            String stackTrace = Log.getStackTraceString(e);
            AppUtils.console(getContext(),TAG, " callDeepLinkCancelTopup Exception stackTrace: " + stackTrace);
            AppUtils.msg(getActivity(),getString(R.string.system_error), null, false);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spend_1, container, false);
        unbinder = ButterKnife.bind(this, view);

        customProgressDialog = new CustomProgressDialog();
        tvFragmentSpend1EWalletBalance.setText(getString(R.string.euro_symbol)+ StringUtils.customFormat(eWalletBalance));
        if (!AppConfig.CODE_TEST.isEmpty())
            edFragmentSpend1PhoneNumber.setText( AppConfig.CODE_TEST);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Spend3Activity activity = (Spend3Activity) getActivity();
        SelectPurchaseMethodActivity activity = (SelectPurchaseMethodActivity) getActivity();
        setDisableOtherField();

        // SUA LAI !!!
        ListView lv = view.findViewById(R.id.lvActiveOffers);
        ArrayAdapter<Offer> adapter = new ArrayAdapter<>(getActivity(), R.layout.my_simple_list_item, activity.offers);
        lv.setAdapter(adapter);
        //

        swFragmentSpend1EWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("onCheckedChanged isChecked="+isChecked);
                btFragmentSpend1VerifyOtp.setText(getString(R.string.verify_otp));
                isVerifyOtp = false;
                if(isChecked){
                    edFragmentSpend1EWallet.setEnabled(true);
                    edFragmentSpend1EWallet.setTextColor(getActivity().getColor(R.color.textBlack));
                    edFragmentSpend1EWallet.setBackgroundColor(getActivity().getColor(R.color.Transparent));
                    btFragmentSpend1VerifyOtp.setEnabled(true);
                    btFragmentSpend1VerifyOtp.setBackground(getActivity().getDrawable(R.drawable.keyboard_ok_button));
                    requestAmount = eWalletBalance >= amountSpend ? amountSpend : eWalletBalance;
                }
                else{
                    edFragmentSpend1EWallet.setEnabled(false);
                    edFragmentSpend1EWallet.setTextColor(getActivity().getColor(R.color.textGray));
                    btFragmentSpend1VerifyOtp.setEnabled(false);

                    btFragmentSpend1VerifyOtp.setTextColor(getActivity().getColor(R.color.textTertiary));
                    btFragmentSpend1VerifyOtp.setBackgroundColor(getActivity().getColor(R.color.buttonDisable));
                    requestAmount = 0.0;

                }

                isEnablePurchaseButton();
            }
        });

        edFragmentSpend1EWallet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        //AppUtils.console(getContext(),TAG,"edFragmentSpend1EWallet==" + s.toString());
                        if(!edFragmentSpend1EWallet.getText().toString().isEmpty()){
                            requestAmount = Double.parseDouble(edFragmentSpend1EWallet.getText().toString().replace(",",""));
                        }
                        else{
                            requestAmount = 0.0;
                        }
                        if(requestAmount > amountSpend){
                            AppUtils.msg(getActivity(),getString(R.string.request_amount_not_more_purchase), null, false);
                            requestAmount = amountSpend;
                        }
                        if(requestAmount > eWalletBalance){
                            AppUtils.msg(getActivity(),getString(R.string.request_amount_not_more_balance), null, false);
                            requestAmount = eWalletBalance;
                        }
                        amountDue = amountSpend - requestAmount;
                        if(!requestAmount.equals(0.0)){
                            edFragmentSpend1EWallet.setText(""+ StringUtils.customFormat(requestAmount));
                        }
                        tvFragmentSpend1AmountDue.setText(getString(R.string.euro_symbol)+StringUtils.customFormat(amountDue));

                    return true;
                }
                return false;
            }
        });
        edFragmentSpend1Amount.addTextChangedListener(new TextWatcher() {
            boolean isManualChange = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isEnablePurchaseButton();
                if(amountSpend < eWalletBalance){
                    edFragmentSpend1EWallet.setText(StringUtils.customFormat(amountSpend));
                }
                else{
                    edFragmentSpend1EWallet.setText(StringUtils.customFormat(eWalletBalance));
                }
            }
        });

        edFragmentSpend1PhoneNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                setDisableOtherField();
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        customerValue = edFragmentSpend1PhoneNumber.getText().toString();
                        hideSoftKeyboard(activity);
                        if(customerValue !=null && customerValue.length()<8){
                            AppUtils.msg(getActivity(),getString(R.string.input_must_8_digis), null,false);
                        }
                        else{
                            if(AppUtils.isNetworkConnected(getContext()))
                                new GetCustomerTask().execute("");
                        }

                    return true;
                }
                return false;
            }
        });
//        edFragmentSpend1PhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if(!hasFocus){
//                    customerValue = edFragmentSpend1PhoneNumber.getText().toString();
//                    if(!customerValue.isEmpty()){
//                        if(AppUtils.isNetworkConnected(getContext()))
//                            new GetCustomerTask().execute("");
//                    }
//                }
//            }
//        });
        edFragmentSpend1Amount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String value = edFragmentSpend1Amount.getText().toString().replace(",","");
                    if(!value.isEmpty()){
                        Double d = Double.parseDouble(value);
                        edFragmentSpend1Amount.setText(StringUtils.customFormat(d));
                        edFragmentSpend1Amount.setSelection(StringUtils.customFormat(d).length());
                        hideSoftKeyboard(activity);
                        return true;
                    }
                    hideSoftKeyboard(activity);
                    return false;
                }
                return false;
            }
        });
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private Boolean isEnablePurchaseButton(){
        Boolean isEnable = true;

        String spendAmount = edFragmentSpend1Amount.getText().toString().replace(getString(R.string.euro_symbol), "").replace(",","");
        if(spendAmount.isEmpty()){
            isEnable = false;
            amountSpend = 0.00;
        }
        else{
            amountSpend = Double.parseDouble(spendAmount);
        }
        if(amountSpend == 0){
            isEnable = false;
        }
        if(customer == null){
            isEnable = false;
        }

        if(swFragmentSpend1EWallet.isChecked()){
            if(!isVerifyOtp){
                isEnable = false;
            }

            if(requestAmount > eWalletBalance){
                isEnable = false;
            }
        }
        else{
            requestAmount = 0.0;
        }



        if(amountSpend < requestAmount){
            requestAmount = amountSpend;
        }
        amountDue = amountSpend - requestAmount;

        if(isEnable){
            btFragmentSpend1ProcessPurchase.setEnabled(true);
            btFragmentSpend1ProcessPurchase.setBackground(getActivity().getDrawable(R.drawable.keyboard_ok_button));
        }
        else{
            btFragmentSpend1ProcessPurchase.setEnabled(false);
            btFragmentSpend1ProcessPurchase.setBackgroundColor(getActivity().getColor(R.color.buttonDisable));
        }
        tvFragmentSpend1AmountDue.setText(getString(R.string.euro_symbol)+ StringUtils.customFormat(amountDue));

        if(!requestAmount.equals(0.0))
            edFragmentSpend1EWallet.setText(StringUtils.customFormat(requestAmount));
        else{
            if (!swFragmentSpend1EWallet.isChecked()){
                if(amountSpend < eWalletBalance){
                    edFragmentSpend1EWallet.setText(StringUtils.customFormat(amountSpend));
                }
                else{
                    edFragmentSpend1EWallet.setText(StringUtils.customFormat(eWalletBalance));
                }

            }
        }


        return isEnable;
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
            customProgressDialog.show(getContext());
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getContext());
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
                                //AppUtils.msg(getActivity(), getString(R.string.cannot_find_customer_info), null, false);
                            }
                        }
                        else {
                            AppUtils.msg(getActivity(), getString(R.string.system_error), null, false);
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
                AppUtils.console(getActivity(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(getActivity(), getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(getActivity(), getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(getActivity(), getString(R.string.system_error), null, false);
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
                    tvFragmentSpend1EWalletBalance.setText(getString(R.string.euro_symbol) + StringUtils.customFormat(eWalletBalance));
                }

                setEnableOtherField();

            }
            else if (result2 != null && result2.has("code")){
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
                            CustomDialog customDialog = new CustomDialog(getActivity(), new CustomDialog.OKListener() {
                                @Override
                                public void onOk() {
                                    Intent intent = new Intent(getActivity().getApplicationContext(), SignUpActivity.class);
                                    activityResultLaunch.launch(intent);
                                }

                                @Override
                                public void onCancel() {

                                }
                            }, getString(R.string.customer_not_signup),getString(R.string.btn_ok),getString(R.string.cancel), false);
                            customDialog.show();
                        }
                        else{
                            CustomDialog customDialog = new CustomDialog(getActivity(), new CustomDialog.OKListener() {
                                @Override
                                public void onOk() {
                                    Intent intent = new Intent(getActivity().getApplicationContext(), RegisterActivity.class);
                                    activityResultLaunch.launch(intent);
                                }

                                @Override
                                public void onCancel() {

                                }
                            }, getString(R.string.cannot_find_customer_info_register),getString(R.string.btn_ok),getString(R.string.cancel), false);
                            customDialog.show();
                        }
                    }
                    else {
                        AppUtils.msg(getActivity(), getString(R.string.system_error), null, false);
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
                AppUtils.msg(getActivity(), getString(R.string.system_error), null, false);
            }
        }
    }
    private void setEnableOtherField(){
        swFragmentSpend1EWallet.setEnabled(true);
        edFragmentSpend1Amount.setEnabled(true);
       // rbFragmentSpend1MethodCard.setEnabled(is);
       // rbFragmentSpend1MethodCash.setEnabled(is);
    }
    private void setDisableOtherField(){
        swFragmentSpend1EWallet.setEnabled(false);
        edFragmentSpend1Amount.setEnabled(false);
    }
    private class MakePurchaseTask extends AsyncTask<String, Void, Void> {
        String msg = getString(R.string.system_error);
        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(getContext());
        }

        @Override
        protected Void doInBackground(String... strings) {
            String outletId = Storage.getOutlet(Spend1Fragment.this.getActivity());
            try {
                CrmServices services = new CrmServices(getContext());
                result = services.makePurchase(customer.getId(), outletId, amountSpend, spendMethod, swFragmentSpend1EWallet.isChecked() ? requestAmount : 0);
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getActivity(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    msg = getString(R.string.cannot_connect_server);
                    //AppUtils.msg(getActivity(), getString(R.string.cannot_connect_server), null);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    msg = getString(R.string.connect_timeout);
                    //AppUtils.msg(getActivity(), getString(R.string.connect_timeout), null);
                }else {
                    msg = getString(R.string.system_error);
                    //AppUtils.msg(getActivity(), getString(R.string.system_error), null);
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
//                        AppUtils.msg(getActivity(), getString(R.string.spend_sucessful), new CustomDialog.OKListener() {
//                            @Override
//                            public void onOk() {
//                                getActivity().finish();
//                            }
//
//                            @Override
//                            public void onCancel() {
//
//                            }
//                        }, true);
                        success = true ;
                        // phai put them linh tinh vao de in receipt
                        Intent intent = new Intent(getActivity(), TyxoaActivity.class);
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
                CustomDialog customDialog = new CustomDialog(getContext(), new CustomDialog.OKListener() {
                    @Override
                    public void onOk() {
                        checkAndCallDeepLinkCancelTopup();
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



    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_CODE.VERIFY_OTP_OK) {
                        isVerifyOtp = true;
                        btFragmentSpend1VerifyOtp.setText(getString(R.string.otp_verified));
                        btFragmentSpend1VerifyOtp.setTextColor(getActivity().getColor(R.color.textActive));
                        btFragmentSpend1VerifyOtp.setBackgroundColor(getActivity().getColor(R.color.buttonDisable));
                        btFragmentSpend1VerifyOtp.setEnabled(false);
                        isEnablePurchaseButton();
                    } else if(result.getResultCode() == RESULT_CODE.VERIFY_OTP_FAILED) {
                        isVerifyOtp = false;
                        isEnablePurchaseButton();
                    }
                    else if(result.getResultCode() == RESULT_CODE.SCANNER_QR_CODE){
                        Log.e(TAG, "customer code=="+ result.getData().getStringExtra("customercode"));
                        if(result.getData() != null){
                            customerValue = result.getData().getStringExtra("customercode");
                            edFragmentSpend1PhoneNumber.setText(customerValue);
                            if (AppUtils.isNetworkConnected(getContext()))
                                new GetCustomerTask().execute("");
                        }
                    }
                    // result from deeplink
                    else if(result.getResultCode() == Activity.RESULT_OK){
                        if (isCancelTopupDeeplink){
                            AppUtils.msg(getActivity(), getString(R.string.cancel_payment_success), null, true);
                        }
                        else{
                            Intent data = result.getData();
                            String payment = data.getStringExtra("payment");
                            String msg = data.getStringExtra("description");
                            AppUtils.console(getContext(),TAG,"onActivityResult resultCode="+result.getResultCode()+ " payment="+payment);
                            String paymentData = data.getStringExtra("data");
                            AppUtils.console(getContext(),TAG,"onActivityResult resultCode="+result.getResultCode()+ " data="+paymentData);
                            if (payment != null){
                                if(payment.equals("Approved")){
                                    if (AppUtils.isNetworkConnected(getContext()))
                                        new MakePurchaseTask().execute("");
                                    else{
                                        checkAndCallDeepLinkCancelTopup();
                                    }

                                    ObjectMapper mapper = new ObjectMapper();
                                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                                    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

                                    mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                                            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                                            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                                            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                                            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

                                    try {
                                        deepLinkingResponse = mapper.readValue(paymentData, DeepLinkingResponse.class);
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                        String stackTrace = Log.getStackTraceString(e);
                                        AppUtils.console(getContext(),TAG, "Exception stackTrace: " + stackTrace);
                                    }
                                    //processPaymentCardSuccess();
                                }
                                else if (AppConfig.SIMULATE_SUCCESS_DEEPLINK == 1){
                                    if (AppUtils.isNetworkConnected(getContext()))
                                        new MakePurchaseTask().execute("");
                                    else{
                                        deepLinkingResponse = new DeepLinkingResponse();
                                        deepLinkingResponse.setBankId("12345");
                                        deepLinkingResponse.setTransactionDate("1638951456");
                                        deepLinkingResponse.setBatchNumber("123456");
                                        deepLinkingResponse.setTransactionId("123456");
                                        deepLinkingResponse.setReceiptNumber("123456");
                                        checkAndCallDeepLinkCancelTopup();
                                    }



                                }
                                else {
                                    if(msg != null && !msg.isEmpty() && msg.equals("User Cancel - exit button")){
                                        msg = getString(R.string.user_cancel_exit_button);

                                    }
                                    else if(msg != null && !msg.isEmpty() && msg.equals("User Cancel - on back pressed")){
                                        msg = getString(R.string.user_cancel_on_back_press);

                                    }
                                    else if(msg != null && !msg.isEmpty() && msg.equals("Transaction Timeout")){
                                        msg = getString(R.string.transaction_timeout);
                                    }
                                    else{
                                        msg = getString(R.string.transaction_decline);
                                    }
                                    AppUtils.msg(getActivity(), msg , null, false);
                                }
                            }
                        }

                    }
                    else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        if (isCancelTopupDeeplink){
                            AppUtils.msg(getActivity(), getString(R.string.cannot_cancel_payment), null, false);
                        }
                        else {
                          //  AppUtils.msg(getActivity(), getString(R.string.cannot_payment), null);
                        }


                    }

                }

            });
    @OnClick(R.id.ivFragmentSpend1ScanQRCode)
    public void onClickScannerQRCode(){
        Intent intent = new Intent(getActivity().getApplicationContext(), BarcodeScannerActivity.class);
        activityResultLaunch.launch(intent);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btFragmentSpend1VerifyOtp)
    public void onVerifyOTP(){
        String requestAmountStr = edFragmentSpend1EWallet.getText().toString().replace(",", "");
        if (!requestAmountStr.isEmpty()){
            requestAmount = Double.parseDouble(requestAmountStr);
            if (requestAmount > amountSpend){
                AppUtils.msg(getActivity(),getString(R.string.request_amount_not_more_purchase),null, false);
                return;
            }
            if (requestAmount > eWalletBalance){
                AppUtils.msg(getActivity(),getString(R.string.request_amount_not_more_balance),null, false);
                return;
            }
        }
        Intent intent = new Intent(getActivity(), VerifyOTPActivity.class);
        intent.putExtra("contact_id", customer.getId());
        intent.putExtra("is_flow", "purchase");
        activityResultLaunch.launch(intent);
    }
}