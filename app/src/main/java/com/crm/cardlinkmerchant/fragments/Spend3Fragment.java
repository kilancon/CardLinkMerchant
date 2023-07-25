package com.crm.cardlinkmerchant.fragments;

import android.content.Intent;
import android.os.Bundle;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.activity.BarcodeScannerActivity;
import com.crm.cardlinkmerchant.activity.Spend3Activity;
import com.crm.cardlinkmerchant.model.Outlet;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;
import com.crm.cardlinkmerchant.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Spend3Fragment extends Fragment {
    Unbinder unbinder;
    @BindView(R.id.edFragmentSpend3PassCode)
    EditText edFragmentSpend3PassCode;

    @BindView(R.id.ivFragmentSpend3ScanQRCode)
    ImageView ivFragmentSpend3ScanQRCode;

    @BindView(R.id.edFragmentSpend3Amount)
    EditText edFragmentSpend3Amount;

    @BindView(R.id.tvFragmentSpend3EWalletBalance)
    TextView tvFragmentSpend3EWalletBalance;

    @BindView(R.id.snFragmentSpend3SelectOutlet)
    Spinner snFragmentSpend3SelectOutlet;

    @BindView(R.id.edFragmentSpend3EWallet)
    EditText edFragmentSpend3EWallet;

    @BindView(R.id.swFragmentSpend3EWallet)
    Switch swFragmentSpend3EWallet;

    @BindView(R.id.tvFragmentSpend3AmountDue)
    TextView tvFragmentSpend3AmountDue;

    @BindView(R.id.rbFragmentSpend3MethodCash)
    RadioButton rbFragmentSpend3MethodCash;

    @BindView(R.id.rbFragmentSpend3MethodCard)
    RadioButton rbFragmentSpend3MethodCard;

    @BindView(R.id.btFragmentSpend3VerifyOtp)
    TextView btFragmentSpend3VerifyOtp;

    @BindView(R.id.btFragmentSpend3ProcessPurchase)
    TextView btFragmentSpend3ProcessPurchase;

    private String cardNumber = null;
    private Double eWalletBalance = 123.00;
    private Boolean isVerifyOtp = false;
    private List<Outlet> outlets;
    private Outlet outletSelected;

    @OnClick({R.id.rbFragmentSpend3MethodCash, R.id.rbFragmentSpend3MethodCard})
    public void onSelectMethod(View view){
        if (view.getId() == R.id.rbFragmentSpend3MethodCash){
            rbFragmentSpend3MethodCard.setChecked(false);
        }
        else{
            rbFragmentSpend3MethodCash.setChecked(false);
        }
        isEnablePurchaseButton();
    }


    @OnClick(R.id.btFragmentSpend3VerifyOtp)
    public void onVerifyOTP(){

    }

    @OnClick(R.id.btFragmentSpend3ProcessPurchase)
    public void onSubmit(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spend_3, container, false);
        unbinder = ButterKnife.bind(this, view);


        tvFragmentSpend3EWalletBalance.setText(getString(R.string.euro_symbol)+ StringUtils.customFormat(eWalletBalance));
        tvFragmentSpend3AmountDue.setText(getString(R.string.euro_symbol)+ StringUtils.customFormat(123.00));
        edFragmentSpend3EWallet.setText(getString(R.string.euro_symbol)+ "11.00");
        edFragmentSpend3PassCode.setText( "CR33VVV");
        edFragmentSpend3Amount.setText( getString(R.string.euro_symbol)+ "0.00");

        swFragmentSpend3EWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("onCheckedChanged isChecked="+isChecked);
                btFragmentSpend3VerifyOtp.setText(getString(R.string.verify_otp));
                isVerifyOtp = false;
                if(isChecked){
                    edFragmentSpend3EWallet.setEnabled(true);
                    edFragmentSpend3EWallet.setTextColor(getActivity().getColor(R.color.textBlack));
                    edFragmentSpend3EWallet.setBackgroundColor(getActivity().getColor(R.color.Transparent));
                    btFragmentSpend3VerifyOtp.setEnabled(true);
                    btFragmentSpend3VerifyOtp.setBackground(getActivity().getDrawable(R.drawable.keyboard_ok_button));
                    rbFragmentSpend3MethodCard.setEnabled(false);
                    rbFragmentSpend3MethodCash.setEnabled(false);
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
                    edFragmentSpend3EWallet.setEnabled(false);
                    edFragmentSpend3EWallet.setTextColor(getActivity().getColor(R.color.textGray));
                    btFragmentSpend3VerifyOtp.setEnabled(false);

                    btFragmentSpend3VerifyOtp.setTextColor(getActivity().getColor(R.color.textTertiary));
                    btFragmentSpend3VerifyOtp.setBackgroundColor(getActivity().getColor(R.color.buttonDisable));
                    rbFragmentSpend3MethodCard.setEnabled(true);
                    rbFragmentSpend3MethodCash.setEnabled(true);

                }
                rbFragmentSpend3MethodCard.setChecked(false);
                rbFragmentSpend3MethodCash.setChecked(false);

                isEnablePurchaseButton();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Spend3Activity activity = (Spend3Activity) getActivity();


        snFragmentSpend3SelectOutlet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    snFragmentSpend3SelectOutlet.setAdapter(langAdapter);
                }
            }
        });

    }
    private Boolean isEnablePurchaseButton(){
        Boolean isEnable = true;
        if(edFragmentSpend3Amount.getText().toString().isEmpty()){
            isEnable = false;
        }
        if(edFragmentSpend3PassCode.getText().toString().isEmpty()){
            isEnable = false;
        }

        if(swFragmentSpend3EWallet.isChecked()){
            if(!isVerifyOtp){
                isEnable = false;
            }
            double eWalletAmount = Double.parseDouble(edFragmentSpend3EWallet.getText().toString().replace(getString(R.string.euro_symbol), ""));

            if(eWalletAmount > eWalletBalance){
                isEnable = false;
            }
        }
        if(!rbFragmentSpend3MethodCash.isChecked() && !rbFragmentSpend3MethodCard.isChecked()){
            isEnable = false;
        }
        if(isEnable){
            btFragmentSpend3ProcessPurchase.setEnabled(true);
            btFragmentSpend3ProcessPurchase.setBackground(getActivity().getDrawable(R.drawable.keyboard_ok_button));
        }
        else{
            btFragmentSpend3ProcessPurchase.setEnabled(false);
            btFragmentSpend3ProcessPurchase.setBackgroundColor(getActivity().getColor(R.color.buttonDisable));
        }
        return isEnable;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_CODE.VERIFY_OTP_OK) {
                        isVerifyOtp = true;
                        btFragmentSpend3VerifyOtp.setText(getString(R.string.otp_verified));
                        btFragmentSpend3VerifyOtp.setTextColor(getActivity().getColor(R.color.textActive));
                        btFragmentSpend3VerifyOtp.setBackgroundColor(getActivity().getColor(R.color.buttonDisable));
                        btFragmentSpend3VerifyOtp.setEnabled(false);
                        rbFragmentSpend3MethodCard.setEnabled(true);
                        rbFragmentSpend3MethodCash.setEnabled(true);
                    } else if(result.getResultCode() == RESULT_CODE.VERIFY_OTP_FAILED) {
                        isVerifyOtp = false;
                    }
                    else if(result.getResultCode() == RESULT_CODE.SCANNER_QR_CODE){

                    }
                    isEnablePurchaseButton();
                }
            });
    @OnClick(R.id.ivFragmentSpend3ScanQRCode)
    public void onClickScannerQRCode(){
        Intent intent = new Intent(getActivity().getApplicationContext(), BarcodeScannerActivity.class);
        activityResultLaunch.launch(intent);
    }
}