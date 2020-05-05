package mx.dapp.sdk.customer.callbacks;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.customer.dto.DappCard;

/**
 * Created by carlinohm on 9/06/17.
 */

public interface DappCardCallback extends DappCallback {
    void onSuccess(DappCard card);
}
