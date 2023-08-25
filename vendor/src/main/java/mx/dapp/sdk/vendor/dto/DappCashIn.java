package mx.dapp.sdk.vendor.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DappCashIn implements Parcelable {

    private static final String TAG = "DappCashIn";

    private String id;
    private String reference;
    private String merchantReference;
    private String authNumber;
    private Double amount;
    private Double fee;
    private Double total;
    private String currency;
    private Boolean refunded;
    private Date date;
    private String walletId;
    private String walletName;

    public DappCashIn(JSONObject data){
        id = data.optString("id");
        reference = data.optString("reference");
        merchantReference = data.optString("merchant_reference");
        authNumber = data.optString("auth_number");
        amount = data.optDouble("amount");
        fee = data.optDouble("fee");
        total = data.optDouble("total");
        currency = data.optString("currency");
        refunded = data.optBoolean("refunded");
        try {
            date = parseDateString(data.optString("date"));
        } catch (ParseException e) {
            Log.e(TAG, "Date parse error", e);
        }

        JSONObject jsonWallet = data.optJSONObject("wallet");
        if (jsonWallet != null) {
            walletId = jsonWallet.optString("id");
            walletName = jsonWallet.optString("name");
        }
    }


    protected DappCashIn(Parcel in) {
        id = in.readString();
        reference = in.readString();
        merchantReference = in.readString();
        authNumber = in.readString();
        if (in.readByte() == 0) {
            amount = null;
        } else {
            amount = in.readDouble();
        }
        if (in.readByte() == 0) {
            fee = null;
        } else {
            fee = in.readDouble();
        }
        if (in.readByte() == 0) {
            total = null;
        } else {
            total = in.readDouble();
        }
        currency = in.readString();
        byte tmpRefunded = in.readByte();
        refunded = tmpRefunded == 0 ? null : tmpRefunded == 1;
        walletId = in.readString();
        walletName = in.readString();
    }

    public String getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public String getMerchantReference() {
        return merchantReference;
    }

    public String getAuthNumber() {
        return authNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getFee() {
        return fee;
    }

    public Double getTotal() {
        return total;
    }

    public String getCurrency() {
        return currency;
    }

    public Boolean getRefunded() {
        return refunded;
    }

    public Date getDate() {
        return date;
    }

    public String getWalletId() {
        return walletId;
    }

    public String getWalletName() {
        return walletName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(reference);
        dest.writeString(merchantReference);
        dest.writeString(authNumber);
        if (amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(amount);
        }
        if (fee == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(fee);
        }
        if (total == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(total);
        }
        dest.writeString(currency);
        dest.writeByte((byte) (refunded == null ? 0 : refunded ? 1 : 2));
        dest.writeString(walletId);
        dest.writeString(walletName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DappCashIn> CREATOR = new Creator<DappCashIn>() {
        @Override
        public DappCashIn createFromParcel(Parcel in) {
            return new DappCashIn(in);
        }

        @Override
        public DappCashIn[] newArray(int size) {
            return new DappCashIn[size];
        }
    };

    private Date parseDateString(String strDate) throws ParseException {
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
