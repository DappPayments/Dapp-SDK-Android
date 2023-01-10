package mx.dapp.sdk.customer.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.Date;

import mx.dapp.sdk.core.dto.AbstractDappPayment;
import mx.dapp.sdk.core.enums.DappPaymentType;

public class DappPayment extends AbstractDappPayment implements Parcelable {

    public DappPayment(JSONObject data) {
        super(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeValue(this.amount);
        dest.writeValue(this.tip);
        dest.writeString(this.currency);
        dest.writeString(this.reference);
        dest.writeString(this.referenceNum);
        dest.writeString(this.description);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.client);
        dest.writeString(this.cardLastFour);
        dest.writeInt(this.paymentType == null ? -1 : this.paymentType.ordinal());
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.amount = (Double) source.readValue(Double.class.getClassLoader());
        this.tip = (Double) source.readValue(Double.class.getClassLoader());
        this.currency = source.readString();
        this.reference = source.readString();
        this.referenceNum = source.readString();
        this.description = source.readString();
        long tmpDate = source.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.client = source.readString();
        this.cardLastFour = source.readString();
        int tmpPaymentType = source.readInt();
        this.paymentType = tmpPaymentType == -1 ? null : DappPaymentType.values()[tmpPaymentType];
    }

    protected DappPayment(Parcel in) {
        this.id = in.readString();
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.tip = (Double) in.readValue(Double.class.getClassLoader());
        this.currency = in.readString();
        this.reference = in.readString();
        this.referenceNum = in.readString();
        this.description = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.client = in.readString();
        this.cardLastFour = in.readString();
        int tmpPaymentType = in.readInt();
        this.paymentType = tmpPaymentType == -1 ? null : DappPaymentType.values()[tmpPaymentType];
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
