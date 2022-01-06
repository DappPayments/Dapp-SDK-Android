package mx.dapp.sdk.core.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mx.dapp.sdk.core.enums.DappPaymentType;

/**
 * Created by carlos on 3/03/17.
 */

public abstract class AbstractDappPayment {

    private static final String TAG = "AbstractDappPayment";

    protected String id;
    protected Double amount;
    protected Double tip;
    protected String currency;
    protected String reference;
    protected String description;
    protected Date date;
    protected String client;
    protected String cardLastFour;
    protected DappPaymentType paymentType;

    protected AbstractDappPayment(){}

    protected AbstractDappPayment(JSONObject data) {

        id = data.optString("id");
        amount = data.optDouble("amount");
        tip = data.optDouble("tip");
        currency = data.optString("currency");
        reference = data.optString("reference");
        description = data.optString("description");
        try {
            date =paserDateString(data.optString("date"));
        } catch (ParseException e) {
            Log.e(TAG, "Date parse error", e);
        }
        client = data.optJSONObject("client").optString("name");
        paymentType = DappPaymentType.fromRawValue(data.optJSONObject("payment").optInt("type"));
        cardLastFour = paymentType != DappPaymentType.BALANCE ? data.optString("last_4") : "";
    }


    public String getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getTip() {
        return tip;
    }

    public String getCurrency() {
        return currency;
    }

    public String getReference() {
        return reference;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getClient() {
        return client;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }

    public DappPaymentType getPaymentType() {
        return paymentType;
    }

    private Date paserDateString(String strDate) throws ParseException {
        SimpleDateFormat sdf;
        Locale locale = Locale.US;
        if (strDate.endsWith("Z")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", locale);
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", locale);
        }
        return sdf.parse(strDate);
    }
}
