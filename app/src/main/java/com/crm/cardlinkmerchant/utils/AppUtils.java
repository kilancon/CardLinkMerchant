package com.crm.cardlinkmerchant.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.activity.SelectPurchaseMethodActivity;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class AppUtils {


    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean status = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        if(!status){
            CustomDialog.OKListener listener = new CustomDialog.OKListener() {
                @Override
                public void onOk() {
                    Intent in = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS );
                    context.startActivity(in);
                }
                @Override
                public void onCancel() {

                }
            };
            final CustomDialog dialog = new CustomDialog(context,listener, context.getString(R.string.network_not_connect), context.getString(R.string.menu_setting), context.getString(R.string.cancel), false);
            dialog.show();
        }
        return status;
    }
    public static void msg(Activity activity, String str, CustomDialog.OKListener listener, Boolean isSuccess){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomDialog customDialog = new CustomDialog(activity,listener, str, activity.getString(R.string.btn_ok), "", isSuccess);
                customDialog.show();
            }
        });

    }

    public static void paymentDialog(Activity activity, String amount, View.OnClickListener listener){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.CustomAlertDialog);
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_alert_dialog, viewGroup, false);
//                Button buttonOk=dialogView.findViewById(R.id.buttonOk);
        TextView tvAmount = dialogView.findViewById(R.id.tvAmount);
        TextView btnCash = dialogView.findViewById(R.id.tvCash);
        TextView btnCard = dialogView.findViewById(R.id.tvCard);
        tvAmount.setText("€"+amount);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        btnCash.setOnClickListener(listener);
        btnCard.setOnClickListener(listener);
        alertDialog.show();
    }

    public static void console(Context context, String tag, String msg){
        if(context != null){
            String date = new java.text.SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date());
           // CardlinkDatabase db = CardlinkDatabase.getInstance(context);
           // db.consoleDao().insertConsole(new Console(date, tag, msg));
        }
        Log.d(tag,msg);
    }

    public static ObjectMapper initMapper() {
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
    public static String getRamdomBaseOnDatetime() {
        String transref = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        transref += sdf.format(System.currentTimeMillis());
        transref += String.format("%08d", getRamdom());
        return transref;
    }
    public static int getRamdom() {
        try {
            Random rand = new Random();
            int num = rand.nextInt(10000000);
            return num;
        } catch (Exception e) {
            return 0;
        }
    }

    public static String get16digitRandom() {
        long smallest = 1000_0000_0000_0000L;
        long biggest =  9999_9999_9999_9999L;
        long random = ThreadLocalRandom.current().nextLong(smallest, biggest+1);
        return ""+random;
    }

    public static void msgSystemError(Activity activity){
        msg(activity, activity.getString(R.string.system_error));
    }

    public static void msg(Activity activity, String str){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomDialog customDialog = new CustomDialog(activity,null, str, activity.getString(R.string.btn_ok), "", false);
                customDialog.show();
            }
        });

    }
}
