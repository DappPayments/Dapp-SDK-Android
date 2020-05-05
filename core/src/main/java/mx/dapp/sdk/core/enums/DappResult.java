package mx.dapp.sdk.core.enums;

public enum DappResult {
    RESULT_DEFAULT(-1, "An error has occurred"),
    RESULT_RESPONSE_ERROR(-4, "An error has occurred processing the server response."),
    RESULT_DATE_PARSE_ERROR(-88, "An error has occurred parsing dates.");
    int code;
    String message;

    DappResult(int code, String message){
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
