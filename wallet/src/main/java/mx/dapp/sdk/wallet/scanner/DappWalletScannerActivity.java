package mx.dapp.sdk.wallet.scanner;

import android.os.Bundle;

import mx.dapp.sdk.core.scanner.DappScannerActivity;
import mx.dapp.sdk.wallet.callbacks.DappCodeReadCallback;
import mx.dapp.sdk.wallet.dto.DappPosCode;

public class DappWalletScannerActivity extends DappScannerActivity implements DappCodeReadCallback {

    public static final String DAPP_CODE = "dapp_code";
    private DappPosCode dappPosCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onScan(String result) {
        dappPosCode = new DappPosCode(result);
        dappPosCode.read(this);
    }

    @Override
    public void onSuccess() {
        getIntent().putExtra(DAPP_CODE, dappPosCode);
        setResult(RESULT_OK, getIntent());
        finish();
    }
}
