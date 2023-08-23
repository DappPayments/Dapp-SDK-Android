package mx.dapp.sdk.vendor.callbacks;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.vendor.dto.DappCashIn;

public interface DappCashInCallback extends DappCallback {
    void onSuccess(DappCashIn cashIn);
}
