package com.crm.cardlinkmerchant.activity;

import static com.crm.cardlinkmerchant.utils.LanguageUtil.initLanguage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.adapters.TransactionManagementAdapter;
import com.crm.cardlinkmerchant.fragments.Spend1Fragment;
import com.crm.cardlinkmerchant.model.AccountResponse;
import com.crm.cardlinkmerchant.model.ClassificationResponse;
import com.crm.cardlinkmerchant.model.Classifications;
import com.crm.cardlinkmerchant.model.ContactResponse;
import com.crm.cardlinkmerchant.model.Customer;
import com.crm.cardlinkmerchant.model.DeepLinkingResponse;
import com.crm.cardlinkmerchant.model.PurchaseContentResponse;
import com.crm.cardlinkmerchant.model.PurchaseResponse;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.utils.AppConfig;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomProgressDialog;
import com.crm.cardlinkmerchant.utils.DatePickerDialogCustom;
import com.crm.cardlinkmerchant.utils.ItemClickSupport;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;
import com.crm.cardlinkmerchant.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class TransactionManagementActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = "TRANSACTION_MN_ACT";
    private List<PurchaseContentResponse> transactions;
    private TransactionManagementAdapter transactionAdapter;//chưa rõ dữ liệu trả về, nếu giống
    // cardlink kia thì sd TransactionManagementAdapter
    private int page = 1;
    private boolean isLoadmore = false;
    private String classification_id = "";
    private String search_value = "";
    private int delayRefresh = 2500;
    private String phoneNumber = "";
    private Customer customer;
    private DatePickerDialogCustom datepicker;

    @BindView(R.id.tvHeaderTitle)
    TextView tvHeaderTitle;

    @BindView(R.id.rvTransactions)
    RecyclerView rvTransactions;

    @BindView(R.id.pbTransactionsLoadMore)
    ProgressBar pbTransactionsLoadMore;

    @BindView(R.id.edTransactionSearch)
    EditText edTransactionSearch;

    @BindView(R.id.txtTransactionMnFromDate)
    TextView txtTransactionMnFromDate;

    @BindView(R.id.txtTransactionMnToDate)
    TextView txtTransactionMnToDate;

    @BindView(R.id.lyTransactionManagementSwipeRefresh)
    SwipeRefreshLayout lyTransactionManagementSwipeRefresh;

//    @BindView(R.id.imgTransactionMnSearch)
//    ImageView imgTransactionMnSearch;
    @BindView(R.id.btnTransactionMnSearch)
    TextView btnTransactionMnSearch;

    CustomProgressDialog customProgressDialog;

    private String fromDate = null;
    private String toDate = null;
    private Boolean isFilter = false;
    private PurchaseContentResponse transaction;
    ArrayList<PurchaseContentResponse> transactionsFilter = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguage(this);
        setContentView(R.layout.activity_transaction_management);
        ButterKnife.bind(this);
        initScreen();
        customProgressDialog = new CustomProgressDialog();

        tvHeaderTitle.setText(R.string.transaction_management);

        transactions = new ArrayList<>();

        transactionAdapter = new TransactionManagementAdapter(this, transactions);
        rvTransactions.setAdapter(transactionAdapter);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));

        rvTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!rvTransactions.canScrollVertically(1)) {
                    onLoadMore();
                }
            }
        });
        ItemClickSupport.addTo(rvTransactions).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                transaction = transactions.get(position);
                Intent intent = new Intent(TransactionManagementActivity.this,
                        TransactionDetailActivity.class);
                intent.putExtra("id", transaction.getId());
                ContactResponse contact = transaction.getContact();
                intent.putExtra("contact_name", contact.getName());
                AccountResponse account = transaction.getAccount();
                intent.putExtra("account_number", account.getNumber());
                intent.putExtra("contact_id", contact.getId());
                String date =
                        new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(transaction.getCreated_date() * 1000);
                intent.putExtra("created_date", date);
                intent.putExtra("amount", transaction.getTotal_amount());
                intent.putExtra("status", transaction.getState());
                intent.putExtra("reference_number", transaction.getReference_number());
                activityResultLaunch.launch(intent);
            }
        });
        //comment bỏ filter
//        edTransactionSearch.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int keyCode, KeyEvent event) {
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    isFilter = true;
//                    onFilter();
//                    return true;
//                }
//                isFilter = false;
//                return false;
//            }
//        });

//        if(AppUtils.isNetworkConnected(this)){
//            customProgressDialog.show(this);
//            (new GetClassificationsTask(1)).execute();
//        }
        lyTransactionManagementSwipeRefresh.setOnRefreshListener(this);
        //search call api with toDate, fromDate
        long date = (new Date()).getTime();
        long last7Day = date - 604800000;
        toDate = "" + (date / 1000);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(last7Day));
        try {
            fromDate =
                    "" + new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse((calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR) + " 00:00:00").getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();

            String stackTrace = Log.getStackTraceString(e);
            AppUtils.console(getApplicationContext(), TAG, "Exception stackTrace: " + stackTrace);
            AppUtils.msg(TransactionManagementActivity.this, getString(R.string.system_error),
                    null, false);
        }
        String fromDateStr = new java.text.SimpleDateFormat("dd/MM/yyyy").format(last7Day);
        String toDateStr = new java.text.SimpleDateFormat("dd/MM/yyyy").format(date);

        txtTransactionMnFromDate.setText(fromDateStr);
        txtTransactionMnToDate.setText(toDateStr);
        onGetPurchares();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void onGetPurchares() {
        phoneNumber = edTransactionSearch.getText().toString();
        if(phoneNumber !=null && !phoneNumber.isEmpty()){
            if(phoneNumber !=null && phoneNumber.length()<8){
                AppUtils.msg(this,getString(R.string.input_must_8_digis), null,false);
            }
            else{
                if (AppUtils.isNetworkConnected(this)) {
                    customProgressDialog.show(this);
                    (new GetCustomerTask()).execute();
                }
            }
        }else{
            if (AppUtils.isNetworkConnected(this)) {
                customProgressDialog.show(this);
                page = 1;
                (new GetPurchaseTask( "" + fromDate, "" + toDate, null, page)).execute();
            }
        }
    }

    @OnClick({R.id.ivHeaderBack, R.id.ivHeaderExit})
    public void onBack(View view) {
        super.finish();
    }

//    @OnClick(R.id.txTransactionManagermentSearch)
//    public void onFilter(){
//        search_value = edTransactionSearch.getText().toString().toLowerCase();
//        if(!search_value.isEmpty()){
//            isFilter = true;
//            filterTransactions(search_value);
//            Collections.sort(transactionsFilter);
//            transactionAdapter.setTransactionList(transactionsFilter);
//            transactionAdapter.notifyDataSetChanged();
//        }else{
//            isFilter = false;
//            transactionAdapter.setTransactionList(transactions);
//            transactionAdapter.notifyDataSetChanged();
//        }
//        hideSoftKeyboard(TransactionManagementActivity.this);
//    }

    @OnClick({R.id.txtTransactionMnFromDate, R.id.txtTransactionMnToDate})
    public void onClickChosseDate(View view) {
        if (view.getId() == R.id.txtTransactionMnFromDate) {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            datepicker = new DatePickerDialogCustom(TransactionManagementActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                            String month = (monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) :
                                    "" + (monthOfYear + 1);
                            txtTransactionMnFromDate.setText(day + "/" + month + "/" + year);
                            try {
                                fromDate = "" + new java.text.SimpleDateFormat("MM/dd/yyyy " +
                                        "HH:mm:ss").parse((monthOfYear + 1) + "/" + dayOfMonth +
                                        "/" + year + " 00:00:00").getTime() / 1000;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, year, month, day);
            datepicker.show();
            datepicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.buttonEnable));
            datepicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.black));
        }

        if (view.getId() == R.id.txtTransactionMnToDate) {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            datepicker = new DatePickerDialogCustom(TransactionManagementActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                            String month = (monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) :
                                    "" + (monthOfYear + 1);
                            txtTransactionMnToDate.setText(day + "/" + month + "/" + year);
                            try {
                                toDate = "" + new java.text.SimpleDateFormat("MM/dd/yyyy " +
                                        "HH:mm:ss").parse((monthOfYear + 1) + "/" + dayOfMonth +
                                        "/" + year + " 23:59:59").getTime() / 1000;

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, year, month, day);
            datepicker.show();
            datepicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.buttonEnable));
            datepicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.black));
        }
    }

    @OnClick(R.id.btnTransactionMnSearch)
    public void onClickSearch() {
        onGetPurchares();
    }

    public void onLoadMore() {
        AppUtils.console(this,TAG,"onLoadMore=====");
        if (!isLoadmore) {
            pbTransactionsLoadMore.setVisibility(View.GONE);
            return;
        }
        pbTransactionsLoadMore.setVisibility(View.VISIBLE);
        page = page + 1;
        isLoadmore = false;
        if (AppUtils.isNetworkConnected(this)) {
            if(customer!=null){
                TransactionManagementActivity.GetPurchaseTask getPurchaseTask =
                        new GetPurchaseTask(fromDate, toDate,customer.getId().toString(), page);
                getPurchaseTask.execute();
            }else{
                TransactionManagementActivity.GetPurchaseTask getPurchaseTask =
                        new GetPurchaseTask(fromDate, toDate, null, page);
                getPurchaseTask.execute();
            }
        }
    }

    @Override
    public void onRefresh() {
        AppUtils.console(getApplicationContext(), TAG, "start refresh transactions ====");
        customProgressDialog.dismiss();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onGetPurchares();
                lyTransactionManagementSwipeRefresh.setRefreshing(false);
                AppUtils.console(getApplicationContext(), TAG, "end refresh transactions ====");
            }
        }, 0);
    }

    private void initScreen() {
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager
        // .LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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

    private ArrayList<PurchaseContentResponse> filterTransactions(String search_value) {
        if (!search_value.isEmpty()) {
            transactionsFilter.clear();
            if (transactions != null && transactions.size() > 0) {
                for (PurchaseContentResponse tran : transactions) {
                    if ((tran.getAccount() != null && tran.getAccount().getNumber().toLowerCase().contains(search_value)) ||
                            tran.getContact() != null && tran.getContact().getName().toLowerCase().contains(search_value) ||
                            tran.getContact() != null && tran.getContact().getCode().toLowerCase().contains(search_value)) {
                        transactionsFilter.add(tran);
                    }
                }
            }
        }
        return transactionsFilter;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            fullSceen();
        }
    }

    private void fullSceen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private String filterClassifications(ArrayList<ClassificationResponse> data, String type) {
        ArrayList<ClassificationResponse> newData = new ArrayList<>();
        if (!type.isEmpty()) {
            type = type.toLowerCase();
        }
        if (data != null && data.size() > 0) {
            for (ClassificationResponse classification : data) {
                if (!classification.getName().isEmpty()) {
                    if (classification.getName().toLowerCase().equals(type)) {
                        newData.add(classification);
                    }
                }
            }
        }
        if (newData != null && newData.size() > 0) {
            return newData.get(0).getId();
        }
        return null;
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_CODE.CANCEL_SPEND_OK) {
                        customProgressDialog.show(TransactionManagementActivity.this);
                        new GetPurchaseTask(""+fromDate, ""+toDate, customer.getId().toString(), 1).execute("");
                    } else if (result.getResultCode() == RESULT_CODE.EXIT_AND_GO_MAIN) {
                        TransactionManagementActivity.this.finish();
                    }
                }

            });

    private class GetPurchaseTask extends AsyncTask<String, Void, Void> {
        PurchaseResponse purchaseResponse = new PurchaseResponse();
        String fromDate;
        String toDate;
        String customerId = null;
        int page = 1;

        public GetPurchaseTask() {
        }

        public GetPurchaseTask(String fromDate, String toDate, String customerId, int page) {
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.customerId = customerId;
            this.page = page > this.page ? page : this.page;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                CrmServices services = new CrmServices(getApplicationContext());
                JSONObject result = services.getPurchases(page, classification_id, customerId,
                        fromDate,
                        toDate);
                AppUtils.console(getApplicationContext(), TAG, "getPurchases result====" + result);
                purchaseResponse = (PurchaseResponse) result.get("purchases");
            } catch (Exception e) {
                e.printStackTrace();

                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(getApplicationContext(), TAG,
                        "Exception stackTrace: " + stackTrace);

                if (e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())) {
                    AppUtils.msg(TransactionManagementActivity.this,
                            getString(R.string.cannot_connect_server), null, false);
                } else if (e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())) {
                    AppUtils.msg(TransactionManagementActivity.this,
                            getString(R.string.connect_timeout), null, false);
                } else {
                    AppUtils.msg(TransactionManagementActivity.this,
                            getString(R.string.system_error), null, false);
                }
                customProgressDialog.dismiss();
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            customProgressDialog.dismiss();
            pbTransactionsLoadMore.setVisibility(View.GONE);

            if (page == 1)
                transactions.clear();

            if (purchaseResponse != null) {
                List<PurchaseContentResponse> listPurchase = purchaseResponse.getContent();
                for (PurchaseContentResponse item : listPurchase) {
                    transactions.add(item);
                }
                if (purchaseResponse.getPaging() != null && purchaseResponse.getPaging().getSize() != null && purchaseResponse.getPaging().getSize() > 0) {
                    isLoadmore = true;
                }
            }
            Collections.sort(transactions);
            transactionAdapter.notifyDataSetChanged();
        }
    }

    private class GetCustomerTask extends AsyncTask<String, Void, Void> {

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
                CrmServices services = new CrmServices(TransactionManagementActivity.this);
                result = services.getCustomer(phoneNumber, true);
            } catch (Exception e) {
                e.printStackTrace();
                String stackTrace = Log.getStackTraceString(e);
                AppUtils.console(TransactionManagementActivity.this,TAG, "Exception stackTrace: " + stackTrace);

                if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                    AppUtils.msg(TransactionManagementActivity.this, getString(R.string.cannot_connect_server), null, false);
                }
                else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                    AppUtils.msg(TransactionManagementActivity.this, getString(R.string.connect_timeout), null, false);
                }else {
                    AppUtils.msg(TransactionManagementActivity.this, getString(R.string.system_error), null, false);
                }
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
                        transactions.clear();
                        if (_customer.size() > 0){
                            customer = _customer.get(0);
                            if (AppUtils.isNetworkConnected(TransactionManagementActivity.this)) {
                                customProgressDialog.show(TransactionManagementActivity.this);
                                page = 1;
                                (new GetPurchaseTask("" + fromDate,
                                        "" + toDate, customer.getId().toString(),page)).execute();
                            }
                        }
                        else{
                            AppUtils.msg(TransactionManagementActivity.this, getString(R.string.cannot_find_customer_info), null, false);
                            isLoadmore = false;// not call loadmore
                            transactions.clear();
                        }
                    }
                    else {
                        AppUtils.msg(TransactionManagementActivity.this, getString(R.string.system_error), null, false);
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
                AppUtils.msg(TransactionManagementActivity.this, getString(R.string.system_error), null, false);
            }
        }
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
}
