package mx.dapp.sdk.core.scanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mx.dapp.sdk.core.R;
import mx.dapp.sdk.core.callbacks.DappScannerCallback;
import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.core.scanner.analyzer.DappImageAnalyzer;
import mx.dapp.sdk.core.scanner.analyzer.DappImageAnalyzerHandler;

public class DappScannerFragment extends Fragment implements DappImageAnalyzerHandler {

    private DappScannerCallback callback;
    private boolean scanning = false;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;

    public DappScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dapp_scanner, container, false);
        previewView = view.findViewById(R.id.previewView);

        return view;
    }

    public void setScannerCallback(DappScannerCallback callback) {
        this.callback = callback;
    }

    public void startScanning() {
        checkCameraPermission();
    }

    public void stopScanning() {
        stopCamera();
    }

    public boolean isScanning() {
        return scanning;
    }

    private void checkCameraPermission() {
        int permissionCheck = ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_ACCESS_CAMERA = 225;
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_ACCESS_CAMERA);
        } else {
            starCamera();
        }
    }

    private void starCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                try {
                    ProcessCameraProvider provider = cameraProviderFuture.get();
                    bindPreview(provider);
                } catch (ExecutionException | InterruptedException e) {
                    callback.onError(new DappException(e.getMessage(), e.hashCode()));
                }
            }
        }, ContextCompat.getMainExecutor(requireContext()));
        scanning = true;
    }

    private void stopCamera() {
        cameraExecutor.shutdown();
        scanning = false;
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        DappImageAnalyzer analyzer = new DappImageAnalyzer(this);
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer);

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                imageAnalysis,
                preview
        );
    }

    @Override
    public void onSuccess(String value) {
        callback.onScan(value);
        stopCamera();
    }

    @Override
    public void onError(Exception exception) {
        callback.onError(new DappException(exception.getMessage(), exception.hashCode()));
    }
}
