package com.crm.cardlinkmerchant.activity;


import static com.crm.cardlinkmerchant.utils.LanguageUtil.initLanguage;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.RESULT_CODE;
import com.google.zxing.Result;

public class BarcodeScannerActivity extends AppCompatActivity {
    private final String TAG = "BARCODE_SCANNER_ACT";
    private CodeScanner mCodeScanner;
    CodeScannerView scannerView;
    private static final int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguage(this);
        setContentView(R.layout.activity_barcode_scanner);

        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);

        int apiVersion = Build.VERSION.SDK_INT;
        if (apiVersion >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        mCodeScanner.startPreview();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                showMessage("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessage(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(BarcodeScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "end scan qr code by camera ======");

        if(checkPermission()){
            mCodeScanner.startPreview();
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent=new Intent();
                            Log.e(TAG, "result code=="+ result.getText());
                            intent.putExtra("customercode", result.getText());
                            setResult(RESULT_CODE.SCANNER_QR_CODE,intent);
                            finish();
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}