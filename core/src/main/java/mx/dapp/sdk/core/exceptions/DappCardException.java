package mx.dapp.sdk.core.exceptions;

import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.core.enums.DappCardResult;

/**
 * Created by carlos on 7/06/17.
 */

public class DappCardException extends DappException {

    private DappCardResult result;

    public DappCardException(DappCardResult result) {
        super(result.getMessage(), result.getCode());
        this.result = result;
    }

    public DappCardResult getResult() {
        return result;
    }

}
