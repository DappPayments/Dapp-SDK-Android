package mx.dapp.sdk.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by carlos on 28/06/18.
 */

public class DappPaymentType implements Parcelable {
    private EDappPaymentType payment;
    private String last4;

    public DappPaymentType(JSONObject data){
        this.payment = EDappPaymentType.fromRawValue(data.optInt("type"));
        this.last4 = payment != EDappPaymentType.SALDO ? data.optString("last_4") : "";
    }

    public EDappPaymentType getPaymentType() {
        return payment;
    }

    public String getLast4() {
        return last4;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.payment == null ? -1 : this.payment.ordinal());
        dest.writeString(this.last4);
    }

    protected DappPaymentType(Parcel in) {
        int tmpPayment = in.readInt();
        this.payment = tmpPayment == -1 ? null : EDappPaymentType.values()[tmpPayment];
        this.last4 = in.readString();
    }

    public static final Creator<DappPaymentType> CREATOR = new Creator<DappPaymentType>() {
        @Override
        public DappPaymentType createFromParcel(Parcel source) {
            return new DappPaymentType(source);
        }

        @Override
        public DappPaymentType[] newArray(int size) {
            return new DappPaymentType[size];
        }
    };
}
