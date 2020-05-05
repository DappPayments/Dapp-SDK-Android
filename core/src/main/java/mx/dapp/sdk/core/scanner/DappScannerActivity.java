package mx.dapp.sdk.core.scanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import mx.dapp.sdk.core.R;
import mx.dapp.sdk.core.callbacks.DappScannerCallback;
import mx.dapp.sdk.core.exceptions.DappException;

public abstract class DappScannerActivity extends FragmentActivity implements DappScannerCallback {

    public static final int RESULT_CANCELED_BY_USER = -9;


    private DappScannerFragment dappScannerFragment;
    private Double amount;
    private String description;
    private String reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dapp_scanner_activity);

        dappScannerFragment = (DappScannerFragment) getSupportFragmentManager().findFragmentById(R.id.dapp_scanner_fragment);
        assert dappScannerFragment != null;
        dappScannerFragment.setScannerCallback(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        dappScannerFragment.startScanning();
    }

    @Override
    public void onError(DappException exception) {
        dappScannerFragment.stopScanning();
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(exception.getMessage())
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dappScannerFragment.startScanning();
            }
        });
        builder.create().show();
    }


    @Override
    public abstract void onScan(String result);

    @Override
    public void onClose() {
        setResult(RESULT_CANCELED_BY_USER);
        finish();
    }
}
