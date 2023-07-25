package com.crm.cardlinkmerchant.activity;

import static com.crm.cardlinkmerchant.utils.LanguageUtil.initLanguage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.adapters.OfferAdapter;
import com.crm.cardlinkmerchant.model.Offer;
import com.crm.cardlinkmerchant.model.Organisation;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.ItemClickSupport;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OffersActivity extends AppCompatActivity {
    private List<Offer> offers;
    private OfferAdapter offerAdapter;
    private int page = 1;
    private boolean isLoadmore = false;
    private final String TAG ="OFFERS_ACT";
    @BindView(R.id.rvOffers)
    RecyclerView rvOffers;

    @BindView(R.id.pbOffersLoadMore)
    ProgressBar pbOffersLoadMore;
    @BindView(R.id.tvHeaderTitle)
    TextView tvHeaderTitle;

    CustomProgressDialog customProgressDialog;
    private Offer selectedOffer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguage(this);
        setContentView(R.layout.activity_offers);
        ButterKnife.bind(this);
        initScreen();

        tvHeaderTitle.setText(getString(R.string.offers));
        customProgressDialog = new CustomProgressDialog();
        offers = new ArrayList<>();

        offerAdapter = new OfferAdapter(this, offers, new OfferAdapter.OfferDetail() {
            @Override
            public void onEdit(Offer offer) {
                onEditOffer(offer);
            }
        });


        rvOffers.setAdapter(offerAdapter);
        rvOffers.setLayoutManager(new LinearLayoutManager(this));

        rvOffers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!rvOffers.canScrollVertically(1)){
                    onLoadMore();
                }
            }
        });
        ItemClickSupport.addTo(rvOffers).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Offer offer = offers.get(position);
            }
        });
        if(AppUtils.isNetworkConnected(this)){
            new GetRewardOffersTask().execute("");
        }
    }

     private void onEditOffer(Offer offer){
         AppUtils.console(getApplicationContext(),TAG,  "onUpdateOffer offer = "+ offer.toString());
         selectedOffer = offer;
         if(selectedOffer != null) {
             List<Offer.Award> awards = offer.getAwards();

             Intent intent = new Intent(this, OfferDetailActivity.class);
             intent.putExtra("id", offer.getId());
             intent.putExtra("name", offer.getName());
             intent.putExtra("short_description", offer.getShort_description());
             intent.putExtra("long_description", offer.getLong_description());
             if (awards.size() > 0) {
                 Offer.Award award = awards.get(0);
                 intent.putExtra("amount", award.getAmount());
                 intent.putExtra("amount_type", award.getAmount_type());
             }
             intent.putExtra("life_cycle_state", offer.getLife_cycle_state());
             activityResultLaunch.launch(intent);
         }
     }
    public void onLoadMore(){
        if(!isLoadmore){
            return;
        }
        pbOffersLoadMore.setVisibility(View.VISIBLE);
        page = page+1;
        isLoadmore = false;
        if(AppUtils.isNetworkConnected(this)){
        }

    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_CODE.UPDATE_OFFER_OK) {
                        if(AppUtils.isNetworkConnected(OffersActivity.this))
                            new GetRewardOffersTask().execute("");
                    }
                    else if(result.getResultCode() == RESULT_CODE.EXIT_AND_GO_MAIN){
                        OffersActivity.this.finish();
                    }
                }
            });

    @OnClick({R.id.ivHeaderExit, R.id.ivHeaderBack})
    public void onExit(View view){
        super.finish();
    }

    private class GetRewardOffersTask extends AsyncTask<String, Void, Void> {

        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        int page = 1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(OffersActivity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());
                result = services.getRewardOffers();
                if (result != null && result.getString("code").equals(ResultCode.OK.getLabel())){
                    JSONArray content = result.getJSONArray("content");
                   // List<Offer> offers = new ArrayList<>();
                    if (page == 1){
                        offers.clear();
                    }
                    if(content != null){
                        for (int i=0; i< content.length() ; i++){
                            ObjectMapper mapper = AppUtils.initMapper();
                            JSONObject org = (JSONObject) content.get(i);
                            Offer convertOfr = mapper.readValue(org.toString(), Offer.class);

                            List<Offer.Award> awards = services.getRewardOffersDetail(convertOfr.getId());
                            AppUtils.console(getApplicationContext(), TAG, "GetRewardOffersTask offer awards = "+awards.toString());
                            convertOfr.setAwards(awards);
                            AppUtils.console(getApplicationContext(), TAG, "GetRewardOffersTask offer item = "+convertOfr.toString());
                            offers.add(convertOfr);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(OffersActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(OffersActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(OffersActivity.this, getString(R.string.system_error), null, false);
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
                        offerAdapter.notifyDataSetChanged();
                    }
                    else{
                        AppUtils.msg(OffersActivity.this, getString(R.string.system_error), new CustomDialog.OKListener() {
                            @Override
                            public void onOk() {
                                OffersActivity.this.finish();
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




    private void initScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        // Hide the status bar and navigation bar
        fullSceen(getWindow().getDecorView());
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                fullSceen(getWindow().getDecorView());
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
        {
            fullSceen(getWindow().getDecorView());
        }
        else {
            try {
                Object wmgInstance = Class.forName("android.view.WindowManagerGlobal").getMethod("getInstance").invoke(null);
                Field viewsField = Class.forName("android.view.WindowManagerGlobal").getDeclaredField("mViews");

                viewsField.setAccessible(true);
                ArrayList<View> views = (ArrayList<View>) viewsField.get(wmgInstance);
                for (View view: views) {
                    fullSceen(view);

                    view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            fullSceen(view);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            AppUtils.console(this,TAG, "hasFocus == false");
        }
    }
    private void fullSceen(View view){
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}