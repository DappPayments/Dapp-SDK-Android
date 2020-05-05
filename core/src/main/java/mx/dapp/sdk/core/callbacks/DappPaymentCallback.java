package mx.dapp.sdk.core.callbacks;

import mx.dapp.sdk.core.dto.DappPayment;

public interface DappPaymentCallback extends DappCallback {
    void onSuccess(DappPayment payment);
}
