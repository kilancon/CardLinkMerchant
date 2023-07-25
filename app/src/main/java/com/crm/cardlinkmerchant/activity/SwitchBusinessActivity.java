package com.crm.cardlinkmerchant.activity;

import static com.crm.cardlinkmerchant.utils.LanguageUtil.initLanguage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.model.Organisation;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class SwitchBusinessActivity extends AppCompatActivity {
    private final String TAG = "SWITCH_BUSINESS_ACT";
    @BindView(R.id.btSwitchBusinessContinue)
    TextView btSwitchBusinessContinue;


    @BindView(R.id.actvSelectBusiness)
    AutoCompleteTextView actvSelectBusiness;


    CustomProgressDialog customProgressDialog;

    String orgId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguage(this);
        setContentView(R.layout.activity_switch_business);
        ButterKnife.bind(this);
        initScreen();
        customProgressDialog = new CustomProgressDialog();

        List<Organisation> orgs = getCacheOrganisations();
        AppUtils.console(getApplicationContext(),TAG, "onCreate orgs=="+ orgs);

       // String[] items = new String[]{"Buyway 365", "Coffee House", "Costa Coffee", "Mikel"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        String orgExist = Storage.getBusiness(SwitchBusinessActivity.this);
        if(orgs != null){
            ArrayAdapter<Organisation> langAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_text, orgs);
            langAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
            //set the spinners adapter to the previously created one.
            actvSelectBusiness.setAdapter(langAdapter);

            if(!orgExist.isEmpty()){
                for(Organisation org: orgs) {
                    if(orgExist.equals(org.getExternal_id())){
                        actvSelectBusiness.setText(org.getName(), false);
                    }
                }
            }
            else if (orgs.size() > 0){
                Storage.setBusiness(SwitchBusinessActivity.this , orgs.get(0).getExternal_id());
                Storage.setBusinessName(SwitchBusinessActivity.this , orgs.get(0).getName());
                actvSelectBusiness.setText(orgs.get(0).getName(), false);
            }
        }
        actvSelectBusiness.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object s = adapterView.getItemAtPosition(i);
                Organisation orgSelected = (Organisation)   adapterView.getItemAtPosition(i);
                AppUtils.console(getApplicationContext(),TAG, " onItemSelected orgSelected ===" + orgSelected.getExternal_id());

                Storage.setBusiness(SwitchBusinessActivity.this , orgSelected.getExternal_id());
                Storage.setBusinessName(SwitchBusinessActivity.this , orgSelected.getName());
            }
        });
    }

    @OnClick(R.id.btSwitchBusinessContinue)
    public void onClickContinue(){
        String cacheBusiness = Storage.getBusiness(SwitchBusinessActivity.this);
        if(cacheBusiness != null && !cacheBusiness.isEmpty()){
            orgId = cacheBusiness;
            customProgressDialog.show(this);
            if(AppUtils.isNetworkConnected(this)){
                (new SwitchBusinessActivity.SwitchBusinessTask()).execute("");
            }
        }else{
            AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.select_business_warn), null, false);
        }
    }

    private void initScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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

    private List<Organisation> getCacheOrganisations(){
        List<Organisation> listOrgs = new ArrayList<>();
        try {
           // listOrgs.add(0, new Organisation(getString(R.string.select_business), "0", ""));
            String cacheOrgs = Storage.getOrganisations(SwitchBusinessActivity.this);
            if(cacheOrgs != null){
                JSONArray orgs = new JSONArray(cacheOrgs);
                for (int i=0; i< orgs.length() ; i++){
                    ObjectMapper mapper = initMapper();
                    JSONObject org = (JSONObject)orgs.get(i);
                    Organisation convertOrg = mapper.readValue(org.toString(), Organisation.class);
                    listOrgs.add(convertOrg);
                }
            }
        }
        catch(JSONException e){
            e.printStackTrace();
            String stackTrace = Log.getStackTraceString(e);
            AppUtils.console(getApplicationContext(),TAG, "JSONException stackTrace: " + stackTrace);
        } catch (JsonMappingException e) {
            e.printStackTrace();
            String stackTrace = Log.getStackTraceString(e);
            AppUtils.console(getApplicationContext(),TAG, "JsonMappingException stackTrace: " + stackTrace);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            String stackTrace = Log.getStackTraceString(e);
            AppUtils.console(getApplicationContext(),TAG, "JsonProcessingException stackTrace: " + stackTrace);
        }
        return listOrgs;
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

    @Override
    public void onBackPressed() {
        Boolean fromHome = getIntent().getBooleanExtra("from_home", false);
        if(fromHome){
            Intent intent = new Intent(SwitchBusinessActivity.this, MainActivity.class);
            startActivity(intent);
            SwitchBusinessActivity.this.finish();
        }else{
            finish();
        }
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

    private class SwitchBusinessTask extends AsyncTask<String, Void, Void> {

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
                result = services.switchBusiness(orgId);
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.system_error), null, false);
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
                    AppUtils.console(getApplicationContext(),TAG, "result code=="+code);
                    if(code.equals(ResultCode.OK.getLabel())){
                        String access_token = result.getString("access_token");
                        String refresh_token = result.getString("refresh_token");
                        String exp = result.getString("exp");
                        Storage.setToken(SwitchBusinessActivity.this, access_token);
                        Storage.setRefreshToken(SwitchBusinessActivity.this, refresh_token);
                        Storage.setExpTime(SwitchBusinessActivity.this, exp);
                        JSONArray organisations = result.getJSONArray("organisations");
                        Storage.setOrganisations(SwitchBusinessActivity.this, organisations.toString());
                        Storage.setOrganisationSelected(SwitchBusinessActivity.this, orgId);
                        new GetOutLetTask().execute("");
                        //
//                        Intent intent = new Intent(SwitchBusinessActivity.this, MainActivity.class);
//                        startActivity(intent);
                    }
                    else{
                        AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.system_error), null, false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String stackTrace = Log.getStackTraceString(e);
                    AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);
                }

            }
            else if(!exception){
                AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.login_failed), null, false);
            }
        }
    }

    private class GetOutLetTask extends AsyncTask<String, Void, Void> {

        boolean success = false;
        boolean exception = false;
        JSONObject result = null;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());
                result = services.getOrganisationOutlet(orgId);
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(SwitchBusinessActivity.this,TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.system_error), null, false);
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
                        if(content != null){
                            Storage.setOutletsJSON(SwitchBusinessActivity.this,content.toString());
                            Intent intent = new Intent(SwitchBusinessActivity.this, SelectOutletActivity.class);
                            startActivity(intent);
                        }
                    }
                    else {
                        AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.system_error), null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(!exception){
                AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.system_error), null, false);
            }
        }
    }
    //ty xoa
    private class GetConfigBusinessTask extends AsyncTask<String, Void, Void> {

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
                result = services.getOrganisationConfig();
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.system_error), null, false);
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
                    AppUtils.console(getApplicationContext(),TAG, "GetConfigBusinessTask result code=="+code);
                    if(code.equals(ResultCode.OK.getLabel())){
                        JSONArray dataContent = result.getJSONArray("content");
                        Log.e(TAG, "dataContent code=="+dataContent);

                        Intent intent = new Intent(SwitchBusinessActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else if(code.equals(ResultCode.INVALIDLOGINEXCEPTION.getLabel())){
                        AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.invalid_login), null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String stackTrace = Log.getStackTraceString(e);
                    AppUtils.console(getApplicationContext(),TAG, "GetConfigBusinessTask Exception stackTrace: " + stackTrace);
                }
            }
            else if(!exception){
                AppUtils.msg(SwitchBusinessActivity.this, getString(R.string.login_failed), new CustomDialog.OKListener() {
                    @Override
                    public void onOk() {
                        SwitchBusinessActivity.this.finish();
                    }
                    @Override
                    public void onCancel() {

                    }
                }, false);
            }
        }
    }
}