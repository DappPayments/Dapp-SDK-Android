package mx.dapp.sdk.wallet.callbacks;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.wallet.dto.WalletDappCard;

public interface WalletDappCardCallback extends DappCallback {
    void onSuccess(WalletDappCard card);
}
