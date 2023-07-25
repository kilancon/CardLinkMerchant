package com.crm.cardlinkmerchant.activity;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.model.Offer;
import com.crm.cardlinkmerchant.model.Organisation;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;
import com.crm.cardlinkmerchant.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OfferDetailActivity extends AppCompatActivity {

    @BindView(R.id.tvHeaderTitle)
    TextView tvHeaderTitle;

    @BindView(R.id.tvOfferDetailName)
    TextView tvOfferDetailName;

    @BindView(R.id.tvOfferDetailDesc)
    TextView tvOfferDetailDesc;

    @BindView(R.id.tvItemOfferStatus)
    TextView tvItemOfferStatus;
    @BindView(R.id.etOfferDetailAmount)
    EditText etOfferDetailAmount;

    private final String TAG = "OFFER_DETAIL_ACT";

    private Offer offer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguage(this);
        setContentView(R.layout.activity_offer_detail);
        ButterKnife.bind(this);
        initScreen();
        tvHeaderTitle.setText(getString(R.string.offer_detail));

        String id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        String short_description = getIntent().getStringExtra("short_description");
        String long_description = getIntent().getStringExtra("long_description");
        double amount = getIntent().getDoubleExtra("amount", 0.00);
        String amount_type = getIntent().getStringExtra("amount_type");
        String life_cycle_state = getIntent().getStringExtra("life_cycle_state");

        offer = new Offer(id, name, short_description, long_description, life_cycle_state, amount, amount_type);
        tvOfferDetailName.setText(name);
        if(long_description == null) long_description = "";
        if(short_description == null) short_description = "";
        tvOfferDetailDesc.setText(short_description + "\n\n" + long_description);

        if (life_cycle_state.equals("ACTIVE")) {
            tvItemOfferStatus.setBackgroundColor(getResources().getColor(R.color.btActiveOffer));
            tvItemOfferStatus.setText(getResources().getString(R.string.active));
        }
        else{
            tvItemOfferStatus.setBackgroundColor(getResources().getColor(R.color.btDeactiveOffer));
            tvItemOfferStatus.setText(getResources().getString(R.string.deactive));
        }
        if (amount_type.equals("PERCENTAGE")){
            int amountInt = (int) amount;
            etOfferDetailAmount.setText(""+amountInt+"%");
        }

        else if(amount_type.equals("FIXED")){
            int amountInt = (int) amount;
            etOfferDetailAmount.setText(getResources().getString(R.string.euro_symbol)+amountInt);
        }

    }



    @OnClick({R.id.ivHeaderBack})
    public void onBack(View view){
//        setResult(RESULT_CODE.UPDATE_OFFER_OK);
        super.finish();
    }
    @OnClick({R.id.ivHeaderExit})
    public void onExit(View view){
        setResult(RESULT_CODE.EXIT_AND_GO_MAIN);
        super.finish();
    }

    private void initScreen() {
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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