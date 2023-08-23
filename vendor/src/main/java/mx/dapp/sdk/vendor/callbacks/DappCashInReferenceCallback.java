package mx.dapp.sdk.vendor.callbacks;

import java.util.Date;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.vendor.dto.DappPayment;

public interface DappCashInReferenceCallback extends DappCallback {
    void onSuccess(String reference, Date creationDate, Date expirationDate, Double amount, String currency);
}
