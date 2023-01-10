package mx.dapp.sdk.customer.checkout;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.customer.dto.DappPayment;

public interface DappCheckoutCallback extends DappCallback {
    void onSuccess(DappPayment payment);
}
