package com.crm.cardlinkmerchant.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.crm.cardlinkmerchant.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hongdthaui on 7/9/2020.
 */
public class ChosseDialog extends Dialog {
    public interface OKListener{
        public void onOk();
        public void onCancel();
    }
    private Context context;
    private OKListener okListener;
    private String message;
    private String txtOK;
    private String txtCancel;
    private boolean isSuccess = false;

    @BindView(R.id.txtCustomDialogMessage)
    TextView txtCustomDialogMessage;
    @BindView(R.id.txtChosseDialogProceed)
    TextView txtChosseDialogProceed;
    @BindView(R.id.txtChosseDialogCancel)
    TextView txtChosseDialogCancel;
    @BindView(R.id.imgNoticeSpentCardApproved)
    ImageView imgNoticeSpentCardApproved;

    public ChosseDialog(@NonNull Context context, OKListener listener) {
        super(context);
        this.context = context;
        this.okListener = listener;
    }
//    public CustomDialog(@NonNull Context context, CustomDialog.OKListener listener, String message, String txtOk, String txtCancel) {
//        super(context);
//        this.context = context;
//        this.okListener = listener;
//        this.message = message;
//        this.txtOK = txtOk;
//        this.txtCancel = txtCancel;
//    }
    public ChosseDialog(@NonNull Context context, OKListener listener, String message, String txtOk, String txtCancel, boolean isSuccess) {
        super(context);
        this.context = context;
        this.okListener = listener;
        this.message = message;
        this.txtOK = txtOk;
        this.txtCancel = txtCancel;
        this.isSuccess = isSuccess;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chosse_dialog);

        ButterKnife.bind(this);
        if(message != null)
            txtCustomDialogMessage.setText(message);
        if(txtOK !=null)
            txtChosseDialogProceed.setText(txtOK);

        if(txtCancel!=null && txtCancel.equals("")){
            txtChosseDialogCancel.setVisibility(View.GONE);
        }
        else if(txtCancel != null){
            txtChosseDialogCancel.setText(txtCancel);
        }

        if(isSuccess){
            imgNoticeSpentCardApproved.setImageDrawable(context.getDrawable(R.mipmap.ic_tick));
        }

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
    public void dismiss() {
        try{
            Activity activity = (Activity) context;
            if (activity != null && !activity.isFinishing()){
                super.dismiss();
            }
        }
        catch (Exception e){

        }

    }

    @OnClick(R.id.txtChosseDialogProceed)
    public void onClickProceed(){
        dismiss();
        if(okListener!=null)
            okListener.onOk();
    }

    @OnClick(R.id.txtChosseDialogCancel)
    public void onClickCancel(){
        dismiss();
        if(okListener!=null)
            okListener.onCancel();

    }
}
