package mx.dapp.sdk.core.enums;

public enum DappPaymentResult {
    RESULT_NO_ACTIVITY_TO_HANDLE(-18, "No Intent available to handle DAPP action.");

    int code;
    String message;

    DappPaymentResult(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
