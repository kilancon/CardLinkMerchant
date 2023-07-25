package com.crm.cardlinkmerchant.fragments;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.activity.BarcodeScannerActivity;
import com.crm.cardlinkmerchant.activity.Spend3Activity;
import com.crm.cardlinkmerchant.model.Outlet;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;
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
import butterknife.Unbinder;

public class Spend2Fragment extends Fragment {
    Unbinder unbinder;
    @BindView(R.id.edFragmentSpend2CustomerCode)
    EditText edFragmentSpend2CustomerCode;

    @BindView(R.id.tvFragmentSpend2EWalletBalance)
    TextView tvFragmentSpend2EWalletBalance;

    @BindView(R.id.edFragmentSpend2Amount)
    EditText edFragmentSpend2Amount;

    @BindView(R.id.snFragmentSpend2SelectOutlet)
    Spinner snFragmentSpend2SelectOutlet;

    @BindView(R.id.edFragmentSpend2EWallet)
    EditText edFragmentSpend2EWallet;

    @BindView(R.id.swFragmentSpend2EWallet)
    Switch swFragmentSpend2EWallet;

    @BindView(R.id.tvFragmentSpend2AmountDue)
    TextView tvFragmentSpend2AmountDue;

    @BindView(R.id.rbFragmentSpend2MethodCash)
    RadioButton rbFragmentSpend2MethodCash;

    @BindView(R.id.rbFragmentSpend2MethodCard)
    RadioButton rbFragmentSpend2MethodCard;

    @BindView(R.id.btFragmentSpend2VerifyOtp)
    TextView btFragmentSpend2VerifyOtp;


    @BindView(R.id.btFragmentSpend2ProcessPurchase)
    TextView btFragmentSpend2ProcessPurchase;


    private String cardNumber = null;
    private Double eWalletBalance = 123.00;
    private Boolean isVerifyOtp = false;
    private Outlet outletSelected;
    private List<Outlet> outlets;
    private CustomProgressDialog customProgressDialog;
    private String contactOTP;
    @OnClick({R.id.rbFragmentSpend2MethodCash, R.id.rbFragmentSpend2MethodCard})
    public void onSelectMethod(View view){
        if (view.getId() == R.id.rbFragmentSpend2MethodCash){
            rbFragmentSpend2MethodCard.setChecked(false);
        }
        else{
            rbFragmentSpend2MethodCash.setChecked(false);
        }
        isEnablePurchaseButton();
    }


    @OnClick(R.id.btFragmentSpend2VerifyOtp)
    public void onVerifyOTP(){

    }

    @OnClick(R.id.btFragmentSpend2ProcessPurchase)
    public void onSubmit(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spend_2, container, false);
        unbinder = ButterKnife.bind(this, view);
        customProgressDialog = new CustomProgressDialog();

        tvFragmentSpend2EWalletBalance.setText(getString(R.string.euro_symbol)+ StringUtils.customFormat(eWalletBalance));
        tvFragmentSpend2AmountDue.setText(getString(R.string.euro_symbol)+ StringUtils.customFormat(123.00));
        edFragmentSpend2EWallet.setText(getString(R.string.euro_symbol)+ "11.00");
        edFragmentSpend2CustomerCode.setText( "1000011111");
        edFragmentSpend2Amount.setText( getString(R.string.euro_symbol)+ "0.00");

        swFragmentSpend2EWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("onCheckedChanged isChecked="+isChecked);
                btFragmentSpend2VerifyOtp.setText(getString(R.string.verify_otp));
                isVerifyOtp = false;
                if(isChecked){
                    edFragmentSpend2EWallet.setEnabled(true);
                    edFragmentSpend2EWallet.setTextColor(getActivity().getColor(R.color.textBlack));
                    edFragmentSpend2EWallet.setBackgroundColor(getActivity().getColor(R.color.Transparent));
                    btFragmentSpend2VerifyOtp.setEnabled(true);
                    btFragmentSpend2VerifyOtp.setBackground(getActivity().getDrawable(R.drawable.keyboard_ok_button));
                    rbFragmentSpend2MethodCard.setEnabled(false);
                    rbFragmentSpend2MethodCash.setEnabled(false);
//                    if(!isVerifyOtp){
//                        rbSpendMethodCard.setEnabled(false);
//                        rbSpendMethodCash.setEnabled(false);
//                    }
//                    else{
//                        rbSpendMethodCard.setEnabled(true);
//                        rbSpendMethodCash.setEnabled(true);
//                    }
                }
                else{
                    edFragmentSpend2EWallet.setEnabled(false);
                    edFragmentSpend2EWallet.setTextColor(getActivity().getColor(R.color.textGray));
                    btFragmentSpend2VerifyOtp.setEnabled(false);

                    btFragmentSpend2VerifyOtp.setTextColor(getActivity().getColor(R.color.textTertiary));
                    btFragmentSpend2VerifyOtp.setBackgroundColor(getActivity().getColor(R.color.buttonDisable));
                    rbFragmentSpend2MethodCard.setEnabled(true);
                    rbFragmentSpend2MethodCash.setEnabled(true);

                }
                rbFragmentSpend2MethodCard.setChecked(false);
                rbFragmentSpend2MethodCash.setChecked(false);

                isEnablePurchaseButton();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    private Boolean isEnablePurchaseButton(){
        Boolean isEnable = true;
        if(edFragmentSpend2Amount.getText().toString().isEmpty()){
            isEnable = false;
        }
        if(edFragmentSpend2CustomerCode.getText().toString().isEmpty()){
            isEnable = false;
        }

        if(swFragmentSpend2EWallet.isChecked()){
            if(!isVerifyOtp){
                isEnable = false;
            }
            double eWalletAmount = Double.parseDouble(edFragmentSpend2EWallet.getText().toString().replace(getString(R.string.euro_symbol), ""));

            if(eWalletAmount > eWalletBalance){
                isEnable = false;
            }
        }
        if(!rbFragmentSpend2MethodCash.isChecked() && !rbFragmentSpend2MethodCard.isChecked()){
            isEnable = false;
        }
        if(isEnable){
            btFragmentSpend2ProcessPurchase.setEnabled(true);
            btFragmentSpend2ProcessPurchase.setBackground(getActivity().getDrawable(R.drawable.keyboard_ok_button));
        }
        else{
            btFragmentSpend2ProcessPurchase.setEnabled(false);
            btFragmentSpend2ProcessPurchase.setBackgroundColor(getActivity().getColor(R.color.buttonDisable));
        }
        return isEnable;
    }
    @OnClick(R.id.btFragmentSpend2ProcessPurchase)
    public void onClickSubmit(){

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Spend3Activity activity = (Spend3Activity) getActivity();


        snFragmentSpend2SelectOutlet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                outletSelected = (Outlet) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        activity.outlets.observe(getViewLifecycleOwner(), new Observer<List<Outlet>>() {
            @Override
            public void onChanged(List<Outlet> _outlets) {
                outlets = _outlets;
                if(outlets != null) {
                    //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                    //There are multiple variations of this, but this is the basic variant.
                    ArrayAdapter<Outlet> langAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner_text, outlets);
                    langAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
                    //set the spinners adapter to the previously created one.
                    snFragmentSpend2SelectOutlet.setAdapter(langAdapter);
                }
            }
        });

    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}