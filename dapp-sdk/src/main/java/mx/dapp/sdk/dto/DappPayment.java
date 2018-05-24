package mx.dapp.sdk.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mx.dapp.sdk.tools.Dapp;

/**
 * Created by carlos on 3/03/17.
 */

public class DappPayment implements Parcelable {

    private static final String TAG = "DappPayment";
    private SimpleDateFormat sdf;

    private static final SimpleDateFormat sdfShow = new SimpleDateFormat("dd/MMMM/yyyy hh:mm a", Locale.getDefault());

    private String id;
    private String amount;
    private String tip;
    private String currency;
    private String reference;
    private String description;
    private Date date;
    private String client;
    private DappPaymentType payment;

    public DappPayment(JSONObject data) {
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        sdf.setTimeZone(Dapp.tz);

        this.id = data.optString("id");
        this.amount = data.optString("amount");
        this.tip = data.optString("tip");
        this.currency = data.optString("currency");
        this.reference = data.optString("reference");
        this.description = data.optString("description");
        try {
            this.date = sdf.parse(data.optString("date"));
        } catch (ParseException e) {
            Log.e(TAG, "Date parse error", e);
        }
        this.client = data.optJSONObject("client").optString("name");
        this.payment = new DappPaymentType(data.optJSONObject("payment"));
    }

    public String getId() {
        return id;
    }

    public String getAmount() {
        return amount;
    }

    public String getTip() {
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

    public DappPaymentType getPayment() {
        return payment;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.sdf);
        dest.writeString(this.id);
        dest.writeString(this.amount);
        dest.writeString(this.tip);
        dest.writeString(this.currency);
        dest.writeString(this.reference);
        dest.writeString(this.description);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.client);
        dest.writeParcelable(this.payment, flags);
    }

    protected DappPayment(Parcel in) {
        this.sdf = (SimpleDateFormat) in.readSerializable();
        this.id = in.readString();
        this.amount = in.readString();
        this.tip = in.readString();
        this.currency = in.readString();
        this.reference = in.readString();
        this.description = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.client = in.readString();
        this.payment = in.readParcelable(DappPaymentType.class.getClassLoader());
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
}
