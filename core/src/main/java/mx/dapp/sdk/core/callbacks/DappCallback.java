package mx.dapp.sdk.core.callbacks;

import mx.dapp.sdk.core.exceptions.DappException;

public interface DappCallback {
    void onError(DappException exception);
}
