package mx.dapp.sdk.wallet.enums;

public enum DappCodeResult {

    RESULT_INVALID_INTENT_DATA(-16, "Invalid Intent data"),
    RESULT_INVALID_INTENT_LINK_DATA(-17, "Invalid Intent app link data"),
    RESULT_INVALID_QR_CODE(-14, "Invalid QR code");

    int code;
    String message;

    DappCodeResult(int code, String message){
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
