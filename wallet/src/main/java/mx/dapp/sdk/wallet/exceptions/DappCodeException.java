package mx.dapp.sdk.wallet.exceptions;

import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.wallet.enums.DappCodeResult;

public class DappCodeException extends DappException {

    private DappCodeResult result;

    public DappCodeException(DappCodeResult result) {
        super(result.getMessage(), result.getCode());
        this.result = result;
    }

    public DappCodeResult getResult() {
        return result;
    }
}
