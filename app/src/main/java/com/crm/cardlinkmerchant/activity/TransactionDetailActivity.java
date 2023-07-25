package com.crm.cardlinkmerchant.activity;

import static com.crm.cardlinkmerchant.utils.LanguageUtil.initLanguage;
import static com.crm.cardlinkmerchant.utils.MoneyTextWatcher.customFormat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.model.Offer;
import com.crm.cardlinkmerchant.model.PurchaseContentResponse;
import com.crm.cardlinkmerchant.model.Transaction;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransactionDetailActivity extends AppCompatActivity {
    private final String TAG = "TRANSACTION_DETAIL_ACT";
    @BindView(R.id.tvHeaderTitle)
    TextView tvHeaderTitle;

    @BindView(R.id.txtTransactionDetailContactName)
    TextView txtTransactionDetailContactName;

    @BindView(R.id.tvTransactionDetailStatus)
    TextView tvTransactionDetailStatus;

    @BindView(R.id.txtTransactionDetailContactId)
    TextView txtTransactionDetailContactId;

    @BindView(R.id.tvTransactionDetailReferenceNumber)
    TextView tvTransactionDetailReferenceNumber;

    @BindView(R.id.txtTransactionDetailReferenceNumberValue)
    TextView txtTransactionDetailReferenceNumberValue;

    @BindView(R.id.txtTransactionDetailCreatedDate)
    TextView txtTransactionDetailCreatedDate;

    @BindView(R.id.txtTransactionDetailAccountNumberValue)
    TextView txtTransactionDetailAccountNumberValue;

    @BindView(R.id.tvTransactionDetailAccountName)
    TextView tvTransactionDetailAccountName;

    @BindView(R.id.btTransactionDetailVoidTransaction)
    TextView btTransactionDetailVoidTransaction;
    CustomProgressDialog customProgressDialog;

    private String id ="";

    private String accountId = null;
    private Double balance = 0.0;
    private String contactId = null;
    private Boolean isCancelled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguage(this);
        setContentView(R.layout.activity_transaction_detail);
        ButterKnife.bind(this);
        initScreen();

        customProgressDialog = new CustomProgressDialog();
        tvHeaderTitle.setText(R.string.transaction_detail);
        String contact_name = getIntent().getStringExtra("contact_name");
        id = getIntent().getStringExtra("id");
        String account_number = getIntent().getStringExtra("account_number");
        Double amount = getIntent().getDoubleExtra("amount", 0);
        String contact_id = getIntent().getStringExtra("contact_id");
        String created_date = getIntent().getStringExtra("created_date");
        String reference_number = getIntent().getStringExtra("reference_number");
        String status = getIntent().getStringExtra("status");
        contactId = contact_id;

        String currency = getString(R.string.euro_symbol);
        if(status.equals("POSTED")){
            btTransactionDetailVoidTransaction.setVisibility(View.VISIBLE);
            tvTransactionDetailStatus.setText(getString(R.string.posted));
            tvTransactionDetailStatus.setBackgroundColor(getColor(R.color.btActiveOffer));
        }else{
            btTransactionDetailVoidTransaction.setVisibility(View.GONE);
            tvTransactionDetailStatus.setText(getString(R.string.cancelled));
            tvTransactionDetailStatus.setBackgroundColor(getColor(R.color.btDeactiveOffer));
        }

        txtTransactionDetailContactName.setText(contact_name);
        txtTransactionDetailContactId.setText(contact_id);
        txtTransactionDetailReferenceNumberValue.setText(reference_number);

        tvTransactionDetailReferenceNumber.setText(currency + customFormat(amount));

        txtTransactionDetailCreatedDate.setText(created_date);

        txtTransactionDetailAccountNumberValue.setText(account_number);
        tvTransactionDetailAccountName.setText(contact_name);
    }

    @Override
    public void onBackPressed() {
        onExit(null);
    }

    @OnClick({ R.id.ivHeaderBack})
    public void onBack(View view){
        if(isCancelled){
            Intent intent = new Intent();
            setResult(RESULT_CODE.CANCEL_SPEND_OK,intent);
        }
        super.finish();
    }
    @OnClick({R.id.ivHeaderExit})
    public void onExit(View view){
        setResult(RESULT_CODE.EXIT_AND_GO_MAIN);
        super.finish();
    }
    @OnClick(R.id.btTransactionDetailVoidTransaction)
    public void onVoidTransaction(){
        Intent intent = new Intent(TransactionDetailActivity.this, VerifyOTPActivity.class);
        intent.putExtra("contact_id", contactId);
        intent.putExtra("is_flow", "void_transaction");
        activityResultLaunch.launch(intent);
//        customProgressDialog.show(TransactionDetailActivity.this);
//        if(AppUtils.isNetworkConnected(TransactionDetailActivity.this)){
//            (new TransactionDetailActivity.CancelPurchaseTask()).execute("");
//        }
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_CODE.VERIFY_OTP_OK) {

                        if(AppUtils.isNetworkConnected(TransactionDetailActivity.this)){
                            new TransactionDetailActivity.CancelPurchaseTask().execute("");
                        }
                    } else if(result.getResultCode() == RESULT_CODE.VERIFY_OTP_FAILED) {
                        AppUtils.msg(TransactionDetailActivity.this, "Verify OTP failed", new CustomDialog.OKListener() {
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

    public class CancelPurchaseTask extends AsyncTask<String, Void, Void> {
        String code = "";
        boolean success = false;
        boolean exception = false;
        JSONObject result = null;
        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = (new CrmServices(getApplicationContext())).cancelPurchase(id);
//                code = result.getString("code");
            } catch (Exception e) {
                e.printStackTrace();

                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(TransactionDetailActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(TransactionDetailActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(TransactionDetailActivity.this, getString(R.string.system_error), null, false);
                }
                exception = true;
                customProgressDialog.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            customProgressDialog.dismiss();

            if (result != null && result.has("code")){
                try {
                    String code = result.getString("code");
                    AppUtils.console(getApplicationContext(),TAG, "onPostExecute void Transaction result code==" + code);
                    if(code.equals(ResultCode.OK.getLabel())){
                        isCancelled = true;
                        AppUtils.msg(TransactionDetailActivity.this, getString(R.string.cancel_purcharse_success), new CustomDialog.OKListener() {
                            @Override
                            public void onOk() {
                                Intent intent = new Intent();
                                setResult(RESULT_CODE.CANCEL_SPEND_OK,intent);
                                TransactionDetailActivity.this.finish();
                            }

                            @Override
                            public void onCancel() {
                            }
                        }, true);
                    }
                    else{
                        AppUtils.msg(TransactionDetailActivity.this, getString(R.string.cancel_purcharse_unsuccess), null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String stackTrace = Log.getStackTraceString(e);
                    AppUtils.console(getApplicationContext(),TAG, "Exception stackTrace: " + stackTrace);
                }
            }
            else if(!exception){
                AppUtils.msg(TransactionDetailActivity.this, getString(R.string.system_error), null, false);
            }
        }
    }
}
