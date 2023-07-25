package com.crm.cardlinkmerchant.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;


import com.crm.cardlinkmerchant.R;

public class CustomProgressDialog {
    private CustomDialog dialog;
    private Context context;
   // public void CustomProgressDialog(){}

    public Dialog show(Context context) {
        this.context = context;
        return show(context, null);
    }

    public Dialog show(Context context, String title) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_progress_dialog, null);
        TextView txtTitle = view.findViewById(R.id.cp_title);
        CardView cardView = view.findViewById(R.id.cp_cardview);
        ProgressBar progressBar = view.findViewById(R.id.cp_pbar);
        if (title != null) {
            txtTitle.setText(title);
        }

        // Card Color
        //cardView.setCardBackgroundColor(Color.parseColor("#70000000"));
        progressBar.getIndeterminateDrawable();
        // Progress Bar Color
        setColorFilter(progressBar.getIndeterminateDrawable(), ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, null));

        // Text Color
        //view.cp_title.setTextColor(Color.WHITE)

        dialog = new CustomDialog(context,R.style.CustomDialogTheme);
        dialog.setContentView(view);


        try{
            Activity activity = (Activity) context;
            if (activity != null && !activity.isFinishing()){
                dialog.show();
            }
        }
        catch (Exception e){

        }

        return dialog;
    }
    public void dismiss(){
        try{
            Activity activity = (Activity) context;
            if (activity != null && !activity.isFinishing()){
                dialog.dismiss();
            }
        }
        catch (Exception e){

        }

    }
    private void setColorFilter(Drawable drawable, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    class CustomDialog extends Dialog {

        public CustomDialog(@NonNull Context context, int themeResId) {
            super(context, themeResId);
            getWindow().getDecorView().getRootView().setBackgroundResource(R.color.transparentBlack);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            getWindow().getDecorView().setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    insets.consumeSystemWindowInsets();
                    return insets;
                }
            });
//            window?.decorView?.rootView?.setBackgroundResource(R.color.mdtp_transparent_black)
//            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
//                    insets.consumeSystemWindowInsets()
            }

        @Override
        public void show() {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            super.show();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            this.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        }

    }



}
