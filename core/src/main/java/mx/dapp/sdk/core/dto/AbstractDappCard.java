package mx.dapp.sdk.core.dto;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.dapp.sdk.core.enums.DappCardResult;

/**
 * Created by carlos on 9/06/17.
 */

public abstract class AbstractDappCard {

    private String id;
    private String lastFour;
    private String cardHolder;
    private String brand;

    public AbstractDappCard(JSONObject data) {
        this.id = data.optString("id");
        this.lastFour = data.optString("last_4");
        this.cardHolder = data.optString("cardholder");
        this.brand = data.optString("brand");
    }

    public static DappCardResult validateCardData(String cardNumber, String cardHolder, String expMonth, String expYear, String cvv, String email, String phone_number) {
        if (cardNumber == null || cardNumber.length() < 16) {
            return DappCardResult.RESULT_INVALID_CARD_NUMBER;
        } else if (cardHolder == null || cardHolder.length() == 0) {
            return DappCardResult.RESULT_INVALID_CARD_HOLDER;
        } else if (expMonth == null || !isValidMonth(expMonth)) {
            return DappCardResult.RESULT_INVALID_CARD_EXPMONTH;
        } else if (expYear == null || expYear.length() != 2 || !isValidYear(expYear)) {
            return DappCardResult.RESULT_INVALID_CARD_EXPYEAR;
        } else if (cvv == null || cvv.length() == 0 || cvv.length() > 4) {
            return DappCardResult.RESULT_INVALID_CARD_CVV;
        } else if (isExpired(expMonth, expYear)) {
            return DappCardResult.RESULT_EXIPERD_CARD;
        } else if (!isValidEmail(email)) {
            return DappCardResult.RESULT_INVALID_MAIL;
        } else if (phone_number.length() != 10) {
            return DappCardResult.RESULT_INVALID_PHONE;
        }
        return DappCardResult.RESULT_OK;
    }

    public static boolean isValidMonth(String expMonth) {
        try {
            int month = Integer.parseInt(expMonth);
            return month > 0 && month <= 12;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        Pattern regex = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
        Matcher mat = regex.matcher(email);
        return (mat.matches());
    }

    public static boolean isExpired(String expMonth, String expYear) {
        int month = Integer.parseInt(expMonth);
        int year = Integer.parseInt(expYear) + 2000;

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        if (year < currentYear) {
            return true;
        } else if (year == currentYear && month < Calendar.getInstance().get(Calendar.MONTH)) {
            return true;
        }
        return false;
    }

    public static boolean isValidYear(String expYear) {
        try {
            Integer.parseInt(expYear);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public String getId() {
        return id;
    }

    public String getLastFour() {
        return lastFour;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public String getBrand() {
        return brand;
    }
}
