package mx.dapp.sdk.customer.checkout;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import mx.dapp.sdk.R;
import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.customer.dto.DappPayment;
import mx.dapp.sdk.customer.dto.DappPosCode;

public class DappCheckoutActivity extends FragmentActivity {

    public static final String DAPP_CODE = "dapp_code";
    public static final String DAPP_PAYMENT = "dapp_payment";
    public static final String DAPP_ERROR = "dapp_error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dapp_checkout_activity);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            sendError("Invalid extras");
            return;
        }

        DappPosCode dappPosCode = extras.getParcelable(DAPP_CODE);
        if (dappPosCode == null || dappPosCode.getDappId() == null) {
            sendError("Invalid Dapp Code");
            return;
        }

        DappCheckoutFragment dappCheckoutFragment = (DappCheckoutFragment) getSupportFragmentManager().findFragmentById(R.id.dapp_checkout_fragment);
        assert dappCheckoutFragment != null;
        dappCheckoutFragment.loadDappPosCode(dappPosCode, new DappCheckoutCallback() {

            @Override
            public void onSuccess(DappPayment payment) {
                sendSuccess(payment);
            }

            @Override
            public void onError(DappException exception) {
                sendError(exception.getMessage());
            }
        });
    }

    private void sendError(String errorMsg) {
        getIntent().putExtra(DAPP_ERROR, errorMsg);
        setResult(RESULT_CANCELED, getIntent());
        finish();
    }

    private void sendSuccess(DappPayment payment) {
        getIntent().putExtra(DAPP_PAYMENT, payment);
        setResult(RESULT_OK, getIntent());
        finish();
    }

}
