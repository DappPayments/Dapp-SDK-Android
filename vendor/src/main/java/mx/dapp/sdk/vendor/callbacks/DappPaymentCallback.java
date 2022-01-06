package mx.dapp.sdk.vendor.callbacks;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.vendor.dto.DappPayment;

public interface DappPaymentCallback extends DappCallback {
    void onSuccess(DappPayment payment);
}
