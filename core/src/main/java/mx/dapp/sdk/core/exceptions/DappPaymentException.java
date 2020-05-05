package mx.dapp.sdk.core.exceptions;

import mx.dapp.sdk.core.enums.DappPaymentResult;

public class DappPaymentException extends DappException {

    private DappPaymentResult result;

    public DappPaymentException(DappPaymentResult result) {
        super(result.getMessage(), result.getCode());
        this.result = result;
    }

    public DappPaymentResult getResult() {
        return result;
    }
}
