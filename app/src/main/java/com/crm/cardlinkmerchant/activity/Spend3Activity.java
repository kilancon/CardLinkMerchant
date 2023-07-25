package com.crm.cardlinkmerchant.activity;

import static com.crm.cardlinkmerchant.utils.LanguageUtil.initLanguage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.adapters.ViewPager2Adapter;
import com.crm.cardlinkmerchant.model.DeepLinkingResponse;
import com.crm.cardlinkmerchant.model.Outlet;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.Constants;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.LanguageUtil;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;
import com.crm.cardlinkmerchant.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Spend3Activity extends AppCompatActivity {

    @BindView(R.id.tvHeaderTitle)
    TextView tvHeaderTitle;

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private CustomProgressDialog customProgressDialog;
    private String orgId;
    private final String TAG = "SPEND_ACT";
    public MutableLiveData<List<Outlet>> outlets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguage(this);
        setContentView(R.layout.activity_spend_3);
        ButterKnife.bind(this);
        initScreen();
        customProgressDialog = new CustomProgressDialog();


        tvHeaderTitle.setText(getString(R.string.spend));

        tabLayout = (TabLayout) findViewById(R.id.SpendTabLayout);
        viewPager2 = (ViewPager2) findViewById(R.id.SpendViewPager2);

        //outlets = new ArrayList<>();
        outlets = new MutableLiveData<>();

        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(viewPager2Adapter);
        viewPager2.setUserInputEnabled(false);// disable swipe tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        orgId = Storage.getOrganisationSelected(getApplicationContext());
        AppUtils.console(getApplicationContext(),TAG, "get OrgId = "+ orgId);
        if(orgId != null && !orgId.isEmpty()){
            if(AppUtils.isNetworkConnected(this))
                new GetOutLetTask().execute("");
        }

    }




    private class GetOutLetTask extends AsyncTask<String, Void, Void> {

        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show(Spend3Activity.this);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());
                result = services.getOrganisationOutlet(orgId);
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(Spend3Activity.this,TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(Spend3Activity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(Spend3Activity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(Spend3Activity.this, getString(R.string.system_error), null, false);
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
                        JSONArray content = result.getJSONArray("content");
                         List<Outlet> _outlets = new ArrayList<>();
                        //outlets.clear();
                        if(content != null){
                            for (int i=0; i< content.length() ; i++){
                                ObjectMapper mapper = AppUtils.initMapper();
                                JSONObject org = (JSONObject) content.get(i);
                                Outlet outlet = mapper.readValue(org.toString(), Outlet.class);
                                _outlets.add(outlet);
                            }
                            outlets.setValue(_outlets);
                        }
                    }
                    else {
                        AppUtils.msg(Spend3Activity.this, getString(R.string.system_error), null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            else if(!exception){
                AppUtils.msg(Spend3Activity.this, getString(R.string.system_error), null, false);
            }
        }
    }
    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

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