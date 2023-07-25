package com.crm.cardlinkmerchant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SwipeCardActivity extends AppCompatActivity {
    @BindView(R.id.tvHeaderTitle)
    TextView tvHeaderTitle;
    @BindView(R.id.tvScan)
    TextView tvScan;
    @BindView(R.id.tvEnter)
    TextView tvEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_card);
        ButterKnife.bind(this);
        initScreen();
        tvHeaderTitle.setText(getIntent().getExtras().getString("header title"));

    }

    @OnClick(R.id.ivHeaderExit)
    public void onExit(View view) {
        setResult(RESULT_CODE.EXIT_AND_GO_MAIN);
        super.finish();
    }

    @OnClick( R.id.ivHeaderBack)
    public void onBack(){ super.finish();
    }

    @OnClick(R.id.tvScan)
    public void onClickScan(){
        Log.e("Scan card:", "clicked");
    }

    @OnClick(R.id.tvEnter)
    public void onClickEnter(){
        Log.e("Enter card manually", "clicked");
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
        {
            fullSceen();
        }
    }

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