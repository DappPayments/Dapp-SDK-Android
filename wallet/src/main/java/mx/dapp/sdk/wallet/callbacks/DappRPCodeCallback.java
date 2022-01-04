package mx.dapp.sdk.wallet.callbacks;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.wallet.dto.DappPayment;

public interface DappRPCodeCallback extends DappCallback {
    void onPay(DappPayment payment);

    void onRenew();

    void onDelete();

    void onExpire();

    void onReadExpire();
}
