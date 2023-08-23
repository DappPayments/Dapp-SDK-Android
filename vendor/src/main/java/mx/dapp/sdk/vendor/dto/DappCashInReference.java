package mx.dapp.sdk.vendor.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.vendor.callbacks.DappCashInCallback;
import mx.dapp.sdk.vendor.callbacks.DappCashInReferenceCallback;
import mx.dapp.sdk.vendor.network.DappVendorApi;

public class DappCashInReference implements Parcelable {

    private static final String TAG = "DappCashInReference";

    private String reference;

    public DappCashInReference(String reference){
        this.reference = reference;
    }

    public void read(final DappCashInReferenceCallback callback){
        DappVendorApi api = new DappVendorApi();
        api.cashInReferences(this.reference,
                new DappResponseProcess(callback) {
                    @Override
                    public void processSuccess(Object data) {
                        JSONObject jsonData = ((JSONObject) data);

                        String reference = jsonData.optString("reference");
                        Date creationDate = null;
                        Date expirationDate = null;
                        try {
                            creationDate = parseDateString(jsonData.optString("creation_date"));
                        } catch (ParseException e) {
                            Log.e(TAG, "Creation date parse error", e);
                        }
                        try {
                            expirationDate = parseDateString(jsonData.optString("expiration_date"));
                        } catch (ParseException e) {
                            Log.e(TAG, "Expiration date parse error", e);
                        }
                        Double amount = jsonData.optDouble("amount");
                        String currency = jsonData.optString("currency");

                        callback.onSuccess(reference, creationDate, expirationDate, amount, currency);
                    }
                });
    }

    public void charge(Double amount, String store, String pos, final DappCashInCallback callback){
        this.charge(amount, null, store, pos, callback);
    }

    public void charge(Double amount, String merchantReference, String store, String pos, final DappCashInCallback callback){
        DappVendorApi api = new DappVendorApi();
        api.cashIn(this.reference, amount.toString(), merchantReference, store, pos,
                new DappResponseProcess(callback) {
            @Override
            public void processSuccess(Object data) {
                DappCashIn cashIn = new DappCashIn((JSONObject) data);
                callback.onSuccess(cashIn);
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.reference);
    }

    public void readFromParcel(Parcel source) {
        this.reference = source.readString();
    }

    protected DappCashInReference(Parcel in) {
        this.reference = in.readString();
    }

    public static final Creator<DappCashInReference> CREATOR = new Creator<DappCashInReference>() {
        @Override
        public DappCashInReference createFromParcel(Parcel source) {
            return new DappCashInReference(source);
        }

        @Override
        public DappCashInReference[] newArray(int size) {
            return new DappCashInReference[size];
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
