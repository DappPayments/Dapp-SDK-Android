package mx.dapp.sdk.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.dapp.sdk.DappCardCallback;
import mx.dapp.sdk.DappEnviroment;
import mx.dapp.sdk.DappException;
import mx.dapp.sdk.DappPaymentCallback;
import mx.dapp.sdk.dto.DappCard;
import mx.dapp.sdk.dto.DappPayment;
import mx.dapp.sdk.network.NetConnection;
import mx.dapp.sdk.network.DappResponseProcess;

/**
 * Created by carlos on 31/05/17.
 */

public class Dapp {

    private static final String DAPP_PACKAGE_NAME = "mx.dapp";
    public static final TimeZone tz = TimeZone.getTimeZone("UTC");
    private static final String TAG = "Dapp";
    private static final String DAPP_CODE = "dapp-code";
    public static final String TRANSACTION_BUNDLE = "transaction";
    private static final int DAPP_REQUEST = 555;
    private static final int RESULT_CODE_ERROR = -1;
    private static final String RESULT_CODE_ERROR_MESSAGE = "An error has occurred processing the payment.";
    public static final int RESULT_CANCEL = 0;
    private static final String RESULT_CANCEL_MESSAGE = "The payment has been canceled.";
    public static final int RESULT_OK = 1;
    private static final int RESULT_DAPP_NOT_INSTALLED = -999;
    private static final String RESULT_DAPP_NOT_INSTALLED_MESSAGE = "DAPP is not installed.";
    private static final int RESULT_NOT_SYNC = -2;
    private static final String RESULT_NOT_SYNC_MESSAGE = "DAPP is not synchronized.";
    private static final int RESULT_NOT_INIT = -3;
    private static final String RESULT_NOT_INIT_MESSAGE = "DAPP has not been initialized.";
    public static final int RESULT_RESPONSE_ERROR = -4;
    private static final String RESULT_RESPONSE_ERROR_MESSAGE = "An error has occurred processing the server response.";
    private static final int RESULT_INVALID_DATA = -5;
    private static final String RESULT_INVALID_DATA_MESSAGE = "Invalid data.";
    private static final String RESULT_ERROR_DEFAULT_MESSAGE = "An error has occurred.";
    private static final int RESULT_INVALID_CARD_NUMBER = -6;
    private static final String RESULT_INVALID_CARD_NUMBER_MESSAGE = "Invalid card number.";
    private static final int RESULT_INVALID_CARD_HOLDER = -7;
    private static final String RESULT_INVALID_CARD_HOLDER_MESSAGE = "Invalid cardholder.";
    private static final int RESULT_INVALID_CARD_EXPMONTH = -8;
    private static final String RESULT_INVALID_CARD_EXPMONTH_MESSAGE = "Invalid expiration month.";
    private static final int RESULT_INVALID_CARD_EXPYEAR = -9;
    private static final String RESULT_INVALID_CARD_EXPYEAR_MESSAGE = "Invalid expiration year.";
    private static final int RESULT_INVALID_CARD_CVV = -10;
    private static final String RESULT_INVALID_CARD_CVV_MESSAGE = "Invalid CVV.";
    private static final int RESULT_EXIPERD_CARD = -11;
    private static final String RESULT_EXIPERD_CARD_MESSAGE = "Card expired.";
    private static final int RESULT_INVALID_MAIL = -12;
    private static final String RESULT_INVALID_MAIL_MESSAGE = "Invalid email.";
    private static final int RESULT_INVALID_PHONE = -13;
    private static final String RESULT_INVALID_PHONE_MESSAGE = "Invalid phone number. Length must be 10 digits.";

    private static String api;
    private static String merchant;
    private static Activity context;
    private static DappPaymentCallback transactionCallback;
    private static DappCardCallback cardCallback;
    private static DappEnviroment env;

    public static void init(Activity activity, String apiKey, String merchantId, DappEnviroment enviroment) {
        api = apiKey;
        merchant = merchantId;
        context = activity;
        env = enviroment;
    }

    public static String getApiKey() {
        return api;
    }

    public static String getMerchantId() {
        return merchant;
    }

    private static boolean isDappAvailable(Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(DAPP_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isInit() {
        return api != null && merchant != null;
    }

    public static void requestPayment(Double amount, String description, String reference, DappPaymentCallback userCallback) {
        transactionCallback = userCallback;
        if (amount != null && amount > 0 && description != null && description.length() > 0) {
            if (isInit()) {
                if (isDappAvailable(context)) {
                    generateCode(amount + "", description, reference);
                } else {
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + DAPP_PACKAGE_NAME)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + DAPP_PACKAGE_NAME)));
                    }
                    DappException e = manageResultError(RESULT_DAPP_NOT_INSTALLED);
                    transactionCallback.onError(e);
                }
            } else {
                DappException e = manageResultError(RESULT_NOT_INIT);
                transactionCallback.onError(e);
            }
        } else {
            DappException e = manageResultError(RESULT_INVALID_DATA);
            transactionCallback.onError(e);
        }
    }

    public static void addCard(String cardNumber, String cardHolder, String expMonth, String expYear, String cvv, String email, String phone_number, DappCardCallback userCallback) {
        cardCallback = userCallback;
        int result = validateCardData(cardNumber, cardHolder, expMonth, expYear, cvv, email, phone_number);
        if (result == RESULT_OK) {
            NetConnection.addCard(cardNumber, cardHolder, expMonth, expYear, cvv, email, phone_number, new DappResponseProcess() {
                @Override
                public void processStart() {

                }

                @Override
                public void processSuccess(String json) {
                    addCardSuccess(json);
                }

                @Override
                public void processFailed(Exception e) {
                    DappException de = new DappException(e.getMessage(), RESULT_RESPONSE_ERROR);
                    cardCallback.onError(de);
                }
            });
        } else {
            DappException e = manageResultError(result);
            cardCallback.onError(e);
        }

    }

    private static void addCardSuccess(String responseBody) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(responseBody);
        } catch (JSONException e) {
            DappException de = new DappException(RESULT_ERROR_DEFAULT_MESSAGE, RESULT_RESPONSE_ERROR);
            cardCallback.onError(de);
            return;
        }
        Integer rc = jsonObject.optInt("rc", -1);
        String msg = jsonObject.optString("msg", RESULT_RESPONSE_ERROR_MESSAGE);
        Object data = jsonObject.opt("data");
        if (rc != 0) {
            DappException e = new DappException(msg, RESULT_RESPONSE_ERROR);
            cardCallback.onError(e);
        } else {
            DappCard card = new DappCard((JSONObject) data);
            cardCallback.onSuccess(card);
        }
    }

    private static int validateCardData(String cardNumber, String cardHolder, String expMonth, String expYear, String cvv, String email, String phone_number) {
        if (cardNumber == null || cardNumber.length() < 16) {
            return RESULT_INVALID_CARD_NUMBER;
        } else if (cardHolder == null || cardHolder.length() == 0) {
            return RESULT_INVALID_CARD_HOLDER;
        } else if (expMonth == null || !isValidMonth(expMonth)) {
            return RESULT_INVALID_CARD_EXPMONTH;
        } else if (expYear == null || expYear.length() != 2 || !isValidYear(expYear)) {
            return RESULT_INVALID_CARD_EXPYEAR;
        } else if (cvv == null || cvv.length() == 0 || cvv.length() > 4) {
            return RESULT_INVALID_CARD_CVV;
        } else if (isExpired(expMonth, expYear)) {
            return RESULT_EXIPERD_CARD;
        } else if (!isValidEmail(email)){
            return RESULT_INVALID_MAIL;
        } else if (phone_number.length() != 10){
            return RESULT_INVALID_PHONE;
        }
        return RESULT_OK;
    }

    private static boolean isValidMonth(String expMonth) {
        try {
            int month = Integer.parseInt(expMonth);
            return month > 0 && month <= 12;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidEmail(String email){
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

    private static boolean isValidYear(String expYear) {
        try {
            Integer.parseInt(expYear);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static void generateCode(String amount, String description, String reference) {
        NetConnection.getCode(amount, description, reference, new DappResponseProcess() {
            @Override
            public void processStart() {
            }

            @Override
            public void processSuccess(String json) {
                onSuccess(json);
            }

            @Override
            public void processFailed(Exception e) {
                DappException de = new DappException(e.getMessage(), RESULT_RESPONSE_ERROR);
                transactionCallback.onError(de);
            }
        });
    }

    private static void onSuccess(String responseBody) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(responseBody);
        } catch (JSONException e) {
            DappException de = new DappException(RESULT_ERROR_DEFAULT_MESSAGE, RESULT_RESPONSE_ERROR);
            transactionCallback.onError(de);
            return;
        }
        Integer rc = jsonObject.optInt("rc", -1);
        String msg = jsonObject.optString("msg", RESULT_RESPONSE_ERROR_MESSAGE);
        Object data = jsonObject.opt("data");
        if (rc != 0) {
            DappException e = new DappException(msg, RESULT_RESPONSE_ERROR);
            transactionCallback.onError(e);
        } else {
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage(DAPP_PACKAGE_NAME);
            intent.putExtra(Dapp.DAPP_CODE, data.toString());
            intent.setFlags(0);
            context.startActivityForResult(intent, Dapp.DAPP_REQUEST);
        }
    }

    public static void onResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Dapp.DAPP_REQUEST) {
            if (resultCode == Dapp.RESULT_OK) {
                String json = data.getExtras().getString(TRANSACTION_BUNDLE);
                try {
                    JSONObject object = new JSONObject(json);
                    DappPayment dappPayment = new DappPayment(object);
                    transactionCallback.onSuccess(dappPayment);
                } catch (JSONException e) {
                    DappException de = new DappException(e.getMessage(), RESULT_RESPONSE_ERROR);
                    transactionCallback.onError(de);
                }
            } else {
                DappException e = manageResultError(resultCode);
                transactionCallback.onError(e);
            }
        }
    }

    private static DappException manageResultError(int resultCode) {
        String message;
        switch (resultCode) {
            case RESULT_CANCEL:
                message = RESULT_CANCEL_MESSAGE;
                break;
            case RESULT_CODE_ERROR:
                message = RESULT_CODE_ERROR_MESSAGE;
                break;
            case RESULT_INVALID_DATA:
                message = RESULT_INVALID_DATA_MESSAGE;
                break;
            case RESULT_DAPP_NOT_INSTALLED:
                message = RESULT_DAPP_NOT_INSTALLED_MESSAGE;
                break;
            case RESULT_NOT_INIT:
                message = RESULT_NOT_INIT_MESSAGE;
                break;
            case RESULT_NOT_SYNC:
                message = RESULT_NOT_SYNC_MESSAGE;
                break;
            case RESULT_RESPONSE_ERROR:
                message = RESULT_RESPONSE_ERROR_MESSAGE;
                break;
            case RESULT_INVALID_CARD_NUMBER:
                message = RESULT_INVALID_CARD_NUMBER_MESSAGE;
                break;
            case RESULT_INVALID_CARD_HOLDER:
                message = RESULT_INVALID_CARD_HOLDER_MESSAGE;
                break;
            case RESULT_INVALID_CARD_EXPMONTH:
                message = RESULT_INVALID_CARD_EXPMONTH_MESSAGE;
                break;
            case RESULT_INVALID_CARD_EXPYEAR:
                message = RESULT_INVALID_CARD_EXPYEAR_MESSAGE;
                break;
            case RESULT_INVALID_CARD_CVV:
                message = RESULT_INVALID_CARD_CVV_MESSAGE;
                break;
            case RESULT_EXIPERD_CARD:
                message = RESULT_EXIPERD_CARD_MESSAGE;
                break;
            case RESULT_INVALID_MAIL:
                message = RESULT_INVALID_MAIL_MESSAGE;
                break;
            case RESULT_INVALID_PHONE:
                message = RESULT_INVALID_PHONE_MESSAGE;
                break;
            default:
                message = RESULT_ERROR_DEFAULT_MESSAGE;
                break;
        }
        return new DappException(message, resultCode);
    }

    public static Context getContext() {
        return context;
    }

    public static DappEnviroment getEnviroment() {
        return env;
    }
}
