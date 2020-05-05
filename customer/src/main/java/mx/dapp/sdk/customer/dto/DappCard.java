package mx.dapp.sdk.customer.dto;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.customer.callbacks.DappCardCallback;
import mx.dapp.sdk.customer.enums.DappCardResult;
import mx.dapp.sdk.customer.exceptions.DappCardException;
import mx.dapp.sdk.customer.network.DappCustomerApi;

/**
 * Created by carlos on 9/06/17.
 */

public class DappCard {

    private String id;
    private String lastFour;
    private String cardHolder;
    private String brand;

    private DappCard(JSONObject data) {
        this.id = data.optString("id");
        this.lastFour = data.optString("last_4");
        this.cardHolder = data.optString("cardholder");
        this.brand = data.optString("brand");
    }

    public static void add(String cardNumber, String cardHolder, String expMonth, String expYear, String cvv, String email, String phoneNumber, final DappCardCallback callback) throws Exception {
        DappCardResult result = validateCardData(cardNumber, cardHolder, expMonth, expYear, cvv, email, phoneNumber);
        if (result == DappCardResult.RESULT_OK) {
            DappCustomerApi api = new DappCustomerApi();
            api.card(cardNumber, cardHolder, expMonth, expYear, cvv, email, phoneNumber, new DappResponseProcess(callback) {
                @Override
                public void processSuccess(JSONObject data) {
                    DappCard card = new DappCard(data);
                    callback.onSuccess(card);
                }
            });
        } else {
            throw new DappCardException(result);
        }
    }

    private static DappCardResult validateCardData(String cardNumber, String cardHolder, String expMonth, String expYear, String cvv, String email, String phone_number) {
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

    private static boolean isValidMonth(String expMonth) {
        try {
            int month = Integer.parseInt(expMonth);
            return month > 0 && month <= 12;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isValidEmail(String email) {
        Pattern regex = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
        Matcher mat = regex.matcher(email);
        return (mat.matches());
    }

    private static boolean isExpired(String expMonth, String expYear) {
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

    private static boolean isValidYear(String expYear) {
        try {
            Integer.parseInt(expYear);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
