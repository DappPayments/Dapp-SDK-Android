package mx.dapp.sdk.wallet.callbacks;

import java.util.Date;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.core.dto.DappPayment;

public interface DappRPCodeStatusCallback extends DappCallback {

    void onPay(DappPayment payment);

    void onRenew(String qrCode, Date readExpiration, Date renewExpiration, int timesRenew);

    void onExpire();

    void onReadExpire();

    void onDelete();
}
