package com.crm.cardlinkmerchant.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.adapters.ViewPager2Adapter;
import com.crm.cardlinkmerchant.model.Offer;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectPurchaseMethodActivity extends AppCompatActivity {
    @BindView(R.id.tvHeaderTitle)
    TextView tvHeaderTitle;
    @BindView(R.id.btGiftcard)
    TextView btGiftcard;
    @BindView(R.id.btPhoneQr)
    TextView btPhoneQr;
    @BindView(R.id.btOtpSpend)
    TextView btOtpSpend;
    @BindView(R.id.purchaseViewpager)
    ViewPager2 purchaseViewpager;

    public ArrayList<Offer> offers;
    private CustomProgressDialog customProgressDialog;
    private final String TAG ="SELECT_PURCHASE_METHOD_ACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_purchase_method);
        ButterKnife.bind(this);
        initScreen();

        customProgressDialog = new CustomProgressDialog();
        tvHeaderTitle.setText(getString(R.string.spend));
        //set purchase option
        ViewPager2Adapter vpadapter = new ViewPager2Adapter(getSupportFragmentManager(), getLifecycle());
        purchaseViewpager.setAdapter(vpadapter);
        //get offers
        offers = new ArrayList<>();
        
        if(AppUtils.isNetworkConnected(this)){
            new GetRewardOffersTask().execute("");
        }

    }

    @OnClick(R.id.ivHeaderExit)
    public void onExit(View view) {
        super.finish();
    }

    @OnClick( R.id.ivHeaderBack)
    public void onBack(){
        if(purchaseViewpager.getVisibility() == View.VISIBLE) {
            purchaseViewpager.setVisibility(View.GONE);
            Log.e("Press back in purchase screen1", ""+(purchaseViewpager.getVisibility()==View.VISIBLE));
        }
        else {
            super.finish();
        }
    }

    @OnClick(R.id.btPhoneQr)
    public void onClickPhone(){
        purchaseViewpager.setVisibility(View.VISIBLE);
        Log.e("Press back in purchase screen1", ""+(purchaseViewpager.getVisibility()==View.VISIBLE));
        purchaseViewpager.setCurrentItem(0);
    }

    @OnClick(R.id.btGiftcard)
    public void onClickGiftcard(View v){
        Intent intent = new Intent(this, SwipeCardActivity.class);
        intent.putExtra("header title", getString(R.string.spend));
        activityResultLaunch.launch(intent);
    }

    @OnClick(R.id.btOtpSpend)
    public void onClickOtp(){
//        purchaseViewpager.setVisibility(View.VISIBLE);
//        purchaseViewpager.setCurrentItem(1);
        AppUtils.msg(SelectPurchaseMethodActivity.this, "Chua co !!!");
    }

    private class GetRewardOffersTask extends AsyncTask<String, Void, Void> {

        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        int page = 1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(SelectPurchaseMethodActivity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());
                result = services.getRewardOffers();
                if (result != null && result.getString("code").equals(ResultCode.OK.getLabel())){
                    JSONArray content = result.getJSONArray("content");
                    if (page == 1){
                        offers.clear();
                    }
                    if(content != null){
                        for (int i=0; i< content.length() ; i++){
                            ObjectMapper mapper = AppUtils.initMapper();
                            JSONObject org = (JSONObject) content.get(i);
                            if(org.getString("life_cycle_state").equals("ACTIVE")) {
                                Offer convertOfr = mapper.readValue(org.toString(), Offer.class);
                                List<Offer.Award> awards = services.getRewardOffersDetail(convertOfr.getId());
                                AppUtils.console(getApplicationContext(), TAG, "GetRewardOffersTask offer awards = " + awards.toString());
                                convertOfr.setAwards(awards);
                                AppUtils.console(getApplicationContext(), TAG, "GetRewardOffersTask offer item = " + convertOfr.toString());
                                offers.add(convertOfr);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(SelectPurchaseMethodActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(SelectPurchaseMethodActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(SelectPurchaseMethodActivity.this, getString(R.string.system_error), null, false);
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
                    AppUtils.console(getApplicationContext(), TAG, "GetRewardOffersTask result=="+result);
                    if(code.equals(ResultCode.OK.getLabel())){
//                        offerAdapter.notifyDataSetChanged();
                    }
                    else{
                        AppUtils.msg(SelectPurchaseMethodActivity.this, getString(R.string.system_error), new CustomDialog.OKListener() {
                            @Override
                            public void onOk() {
                                SelectPurchaseMethodActivity.this.finish();
                            }

                            @Override
                            public void onCancel() {
                            }
                        }, false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String stackTrace = Log.getStackTraceString(e);
                    AppUtils.console(getApplicationContext(),TAG, "JSONException stackTrace: " + stackTrace);
                }
            }
        }
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_CODE.EXIT_AND_GO_MAIN) {
                        SelectPurchaseMethodActivity.this.finish();
                    }
                }

            });

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