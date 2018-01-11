package com.gwr.mobilevisionexample.text;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.gwr.mobilevisionexample.R;
import com.gwr.mobilevisionexample.face.GraphicOverlay;


public class TextActivity extends AppCompatActivity {

    private CameraSource mCameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
    }

    public void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // Create the TextRecognizer
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        // TODO: Set the TextRecognizer's Processor.
        textRecognizer.setProcessor(new OcrDetectorProcessor());
        // Check if the TextRecognizer is operational.
        if (!textRecognizer.isOperational()) {
            Log.w("LOG", "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "SEM BATERIA PARA USAR A CAMERA", Toast.LENGTH_LONG).show();
                Log.w("LOG", "SEM BATERIA SUFICIENTE");
            }
        }

        // Create the mCameraSource using the TextRecognizer.
        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(15.0f)
                        .setAutoFocusEnabled(true)
                        .build();
    }

    public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {
        private GraphicOverlay<OcrGraphic> mGraphicOverlay;

        OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
            mGraphicOverlay = ocrGraphicOverlay;
        }

        @Override
        public void release() {

        }

        @Override
        public void receiveDetections(Detector.Detections<TextBlock> detections) {
            mGraphicOverlay.clear();
            SparseArray<TextBlock> items = detections.getDetectedItems();
            for (int i = 0; i < items.size(); ++i) {
                TextBlock item = items.valueAt(i);
                if (item != null && item.getValue() != null) {
                    Log.d("Processor", "Text detected! " + item.getValue());
                }
                OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                mGraphicOverlay.add(graphic);
            }
        }
    }
}
