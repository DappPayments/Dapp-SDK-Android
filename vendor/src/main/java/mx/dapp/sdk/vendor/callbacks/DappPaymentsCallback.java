package mx.dapp.sdk.vendor.callbacks;

import java.util.List;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.vendor.dto.DappPayment;

public interface DappPaymentsCallback extends DappCallback {
    void onSuccess(List<DappPayment> payments);
}
