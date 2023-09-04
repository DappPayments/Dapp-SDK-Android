package mx.dapp.sdk.vendor.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.vendor.callbacks.DappCashInsCallback;
import mx.dapp.sdk.vendor.callbacks.DappPaymentsCallback;
import mx.dapp.sdk.vendor.network.DappVendorApi;

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

    public static void getDappCashIns(String fechaInicio, String fechaFin, int page, int pageSize, final DappCashInsCallback callback){
        DappVendorApi api = new DappVendorApi();
        api.getCashIns(fechaInicio, fechaFin, page, pageSize, new DappResponseProcess(callback) {
            @Override
            public void processSuccess(Object data) {
                JSONArray cashInsJSON = (JSONArray) data;
                List<DappCashIn> results = new ArrayList<>();
                for (int i = 0; i < cashInsJSON.length(); i++) {
                    results.add(new DappCashIn(cashInsJSON.optJSONObject(i)));
                }
                callback.onSuccess(results);
            }
        });
    }

    public static void getDappCashIns(String fechaInicio, String fechaFin, final DappCashInsCallback callback){
        DappCashIn.getDappCashIns(fechaInicio, fechaFin, 0, 0, callback);
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.reference);
        dest.writeString(this.merchantReference);
        dest.writeString(this.authNumber);
        dest.writeValue(this.amount);
        dest.writeValue(this.fee);
        dest.writeValue(this.total);
        dest.writeString(this.currency);
        dest.writeValue(this.refunded);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.walletId);
        dest.writeString(this.walletName);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.reference = source.readString();
        this.merchantReference = source.readString();
        this.authNumber = source.readString();
        this.amount = (Double) source.readValue(Double.class.getClassLoader());
        this.fee = (Double) source.readValue(Double.class.getClassLoader());
        this.total = (Double) source.readValue(Double.class.getClassLoader());
        this.currency = source.readString();
        this.refunded = (Boolean) source.readValue(Boolean.class.getClassLoader());
        long tmpDate = source.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.walletId = source.readString();
        this.walletName = source.readString();
    }

    protected DappCashIn(Parcel in) {
        this.id = in.readString();
        this.reference = in.readString();
        this.merchantReference = in.readString();
        this.authNumber = in.readString();
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.fee = (Double) in.readValue(Double.class.getClassLoader());
        this.total = (Double) in.readValue(Double.class.getClassLoader());
        this.currency = in.readString();
        this.refunded = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.walletId = in.readString();
        this.walletName = in.readString();
    }

    public static final Creator<DappCashIn> CREATOR = new Creator<DappCashIn>() {
        @Override
        public DappCashIn createFromParcel(Parcel source) {
            return new DappCashIn(source);
        }

        @Override
        public DappCashIn[] newArray(int size) {
            return new DappCashIn[size];
        }
    };
}
