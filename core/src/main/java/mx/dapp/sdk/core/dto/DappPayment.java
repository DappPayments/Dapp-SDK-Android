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

public class DappPayment implements Parcelable {

    private static final String TAG = "DappPayment";

    private String id;
    private Double amount;
    private Double tip;
    private String currency;
    private String reference;
    private String description;
    private Date date;
    private String client;
    private String cardLastFour;
    private DappPaymentType paymentType;

    public DappPayment(JSONObject data) {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeDouble(this.amount);
        dest.writeDouble(this.tip);
        dest.writeString(this.currency);
        dest.writeString(this.reference);
        dest.writeString(this.description);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.client);
        dest.writeInt(this.paymentType == null ? -1 : this.paymentType.ordinal());
        dest.writeString(this.cardLastFour);
    }

    protected DappPayment(Parcel in) {
        this.id = in.readString();
        this.amount = in.readDouble();
        this.tip = in.readDouble();
        this.currency = in.readString();
        this.reference = in.readString();
        this.description = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.client = in.readString();
        int tmpPayment = in.readInt();
        this.paymentType = tmpPayment == -1 ? null : DappPaymentType.values()[tmpPayment];
    }

    public static final Creator<DappPayment> CREATOR = new Creator<DappPayment>() {
        @Override
        public DappPayment createFromParcel(Parcel source) {
            return new DappPayment(source);
        }

        @Override
        public DappPayment[] newArray(int size) {
            return new DappPayment[size];
        }
    };


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
