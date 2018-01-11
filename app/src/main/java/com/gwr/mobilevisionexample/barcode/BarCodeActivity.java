package com.gwr.mobilevisionexample.barcode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.gwr.mobilevisionexample.R;

import java.io.IOException;

import me.drakeet.materialdialog.MaterialDialog;

public class BarCodeActivity extends AppCompatActivity {

    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private MaterialDialog materialDialog;

    public static final int REQUEST_CODE = 128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        barcodeInfo = (TextView) findViewById(R.id.code_info);

        barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    startCameraSource();
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    barcodeInfo.post(new Runnable() {
                        public void run() {
                            barcodeInfo.setText(
                                    barcodes.valueAt(0).displayValue
                            );
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE) {
            try {
                startCameraSource();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startCameraSource() throws IOException {

        if (ActivityCompat.checkSelfPermission(BarCodeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(BarCodeActivity.this,Manifest.permission.CAMERA)){
                callDialog("É Preciso a permissão CAMERA para a detectar o Código",new String[] {Manifest.permission.CAMERA} );
            }else{
                ActivityCompat.requestPermissions(BarCodeActivity.this, new String[] {Manifest.permission.CAMERA} ,REQUEST_CODE);
            }
        }else{
            cameraSource.start(cameraView.getHolder());
        }



    }

    private void callDialog(String message, final String[] permissions){

        materialDialog = new MaterialDialog(this)
                .setTitle("Permission")
                .setMessage(message)
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(BarCodeActivity.this,permissions,REQUEST_CODE);
                        materialDialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();

                    }
                });
        materialDialog.show();
    }
}
