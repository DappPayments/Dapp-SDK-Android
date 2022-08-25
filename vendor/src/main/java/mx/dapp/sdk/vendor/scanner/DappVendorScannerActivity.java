package mx.dapp.sdk.vendor.scanner;

import static android.app.Activity.RESULT_OK;

import android.os.Bundle;

import org.json.JSONObject;

import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.core.scanner.DappScannerActivity;
import mx.dapp.sdk.vendor.dto.DappPayment;
import mx.dapp.sdk.vendor.network.DappVendorApi;

public class DappVendorScannerActivity extends DappScannerActivity {

    public static final String AMOUNT = "amount";
    public static final String TIP = "tip";
    public static final String DESCRIPTION = "description";
    public static final String REFERENCE = "reference";
    public static final String POS = "pos";
    public static final String PIN = "pin";
    public static final int RESULT_INVALID_AMOUNT = -10;
    public static final String PAYMENT = "payment";

    private Double amount;
    private Double tip;
    private String description;
    private String reference;
    private String pos;
    private String pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            amount = extras.getDouble(AMOUNT, 0);
            tip = extras.getDouble(TIP, 0);
            description = extras.getString(DESCRIPTION, "");
            reference = extras.getString(REFERENCE, "");
            pos = extras.getString(POS);
            pin = extras.getString(PIN);
        }
        if (amount == null || amount == 0) {
            setResult(RESULT_INVALID_AMOUNT);
            finish();
        }
    }

    @Override
    public void onScan(String result) {
        DappVendorApi api = new DappVendorApi();
        api.paymentCode(result, amount.toString(), tip.toString(), description, reference, pos, pin,
                new DappResponseProcess(this) {
            @Override
            public void processSuccess(Object data) {
                DappPayment payment = new DappPayment((JSONObject)data);
                getIntent().putExtra(PAYMENT, payment);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });
    }


}
