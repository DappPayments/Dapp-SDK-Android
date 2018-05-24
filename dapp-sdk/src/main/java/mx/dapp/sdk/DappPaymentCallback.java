package mx.dapp.sdk;

import mx.dapp.sdk.dto.DappPayment;

/**
 * Created by carlos on 2/06/17.
 */

public interface DappPaymentCallback {
    void onSuccess(DappPayment transaction);

    void onError(DappException exception);
}
