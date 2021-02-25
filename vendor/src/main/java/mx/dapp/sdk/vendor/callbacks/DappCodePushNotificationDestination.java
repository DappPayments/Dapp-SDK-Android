package mx.dapp.sdk.vendor.callbacks;

import java.util.List;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.vendor.dto.DappWallet;

public interface DappCodePushNotificationDestination extends DappCallback {
    void onSuccess(List<DappWallet> destinations);
}
