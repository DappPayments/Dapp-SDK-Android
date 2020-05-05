package mx.dapp.sdk.core.exceptions;

import mx.dapp.sdk.core.enums.DappResult;

/**
 * Created by carlos on 7/06/17.
 */

public class DappException extends Exception {

    private int codeError;

    public DappException(String message, int codeError) {
        super(message);
        this.codeError = codeError;
    }

    public DappException(DappResult result) {
        super(result.getMessage());
        codeError = result.getCode();
    }

    public int getCodeError() {
        return codeError;
    }

    public void setCodeError(int codeError) {
        this.codeError = codeError;
    }
}
