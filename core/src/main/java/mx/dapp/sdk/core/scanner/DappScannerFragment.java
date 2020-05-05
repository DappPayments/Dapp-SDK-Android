package mx.dapp.sdk.core.scanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import mx.dapp.sdk.core.R;
import mx.dapp.sdk.core.callbacks.DappScannerCallback;

public class DappScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private DappScannerCallback callback;
    private ZXingScannerView mScannerView;
    private boolean scanning = false;

    public DappScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dapp_scanner, container, false);
        ArrayList<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);

        mScannerView = new ZXingScannerView(requireContext());
        mScannerView.setFormats(formats);
        mScannerView.setAspectTolerance(0.5f);
        ViewGroup contentFrame = view.findViewById(R.id.content_frame);
        contentFrame.addView(mScannerView);

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
        if (mScannerView != null) {
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
            scanning = true;
        }
    }

    private void stopCamera() {
        if (mScannerView != null) {
            mScannerView.stopCamera();
            scanning = false;
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        String code = rawResult.getText();
        callback.onScan(code);
        stopCamera();
    }
}
