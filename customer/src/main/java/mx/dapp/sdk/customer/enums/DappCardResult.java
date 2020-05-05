package mx.dapp.sdk.customer.enums;

public enum DappCardResult {
    RESULT_OK(1, "OK"),
    RESULT_INVALID_CARD_NUMBER(-6, "Invalid card number."),
    RESULT_INVALID_CARD_HOLDER(-7, "Invalid cardholder."),
    RESULT_INVALID_CARD_EXPMONTH(-8, "Invalid expiration month."),
    RESULT_INVALID_CARD_EXPYEAR(-9, "Invalid expiration year."),
    RESULT_INVALID_CARD_CVV(-10, "Invalid CVV."),
    RESULT_EXIPERD_CARD(-11, "Card expired."),
    RESULT_INVALID_MAIL(-12, "Invalid E-mail."),
    RESULT_INVALID_PHONE(-13, "Invalid phone number. Length must be 10 digits.");

    private int code;
    private String message;

    DappCardResult(int code, String result) {
        this.code = code;
        this.message = result;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
