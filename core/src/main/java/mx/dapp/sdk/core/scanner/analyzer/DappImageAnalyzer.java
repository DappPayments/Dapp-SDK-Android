package mx.dapp.sdk.core.scanner.analyzer;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class DappImageAnalyzer implements ImageAnalysis.Analyzer {

    private DappImageAnalyzerHandler handler;

    public DappImageAnalyzer(DappImageAnalyzerHandler handler) {
        this.handler = handler;
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        scanBarCode(image);
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private void scanBarCode(final ImageProxy image) {
        InputImage inputImage = InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees());
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_QR_CODE
        ).build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        scanner.process(inputImage).addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
            @Override
            public void onComplete(@NonNull Task<List<Barcode>> task) {
                image.close();
                if (task.isSuccessful()) {
                    readBarcodeData(task.getResult());
                } else {
                    handler.onError(task.getException());
                }
            }
        });
    }

    private void readBarcodeData(List<Barcode> result) {
        if (!result.isEmpty()) {
            for (Barcode barcode : result) {
                handler.onSuccess(barcode.getRawValue());
            }
        }
    }
}

