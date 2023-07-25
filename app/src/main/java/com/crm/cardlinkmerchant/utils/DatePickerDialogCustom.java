package com.crm.cardlinkmerchant.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DatePickerDialogCustom extends DatePickerDialog {
    private Context context;

    public DatePickerDialogCustom(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
        this.context = context;
    }

    public DatePickerDialogCustom(@NonNull Context context, int themeResId, @Nullable OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, themeResId, listener, year, monthOfYear, dayOfMonth);
        this.context = context;
    }

    @Override
    public void show() {
        try{
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            Activity activity = (Activity) context;
            if (activity != null && !activity.isFinishing()){
                super.show();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
                this.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
        }
        catch (Exception e){

        }
    }

    @Override
    public Button getButton(int whichButton) {
        return super.getButton(whichButton);
    }

    @Override
    public void setButton(int whichButton, CharSequence text, Message msg) {
        super.setButton(whichButton, text, msg);
    }

}
