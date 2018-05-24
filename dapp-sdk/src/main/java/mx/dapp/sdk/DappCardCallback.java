package mx.dapp.sdk;

import mx.dapp.sdk.dto.DappCard;

/**
 * Created by carlos on 9/06/17.
 */

public interface DappCardCallback {
    void onSuccess(DappCard card);

    void onError(DappException exception);
}
