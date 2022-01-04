package mx.dapp.sdk.vendor.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import mx.dapp.sdk.core.dto.AbstractDappRPCode;
import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.vendor.callbacks.DappPaymentCallback;
import mx.dapp.sdk.vendor.network.DappVendorApi;

public class DappRPCode extends AbstractDappRPCode implements Parcelable {

    public DappRPCode(String qrString){
        this.qrString = qrString;
    }

    public void charge(Double amount, String description, String reference, final DappPaymentCallback callback){
        DappVendorApi api = new DappVendorApi();
        api.paymentCode(qrString, amount.toString(), description, reference, new DappResponseProcess(callback) {
            @Override
            public void processSuccess(Object data) {
                DappPayment payment = new DappPayment((JSONObject) data);
                callback.onSuccess(payment);
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.qrString);
    }

    public void readFromParcel(Parcel source) {
        this.qrString = source.readString();
    }

    protected DappRPCode(Parcel in) {
        this.qrString = in.readString();
    }

    public static final Parcelable.Creator<DappRPCode> CREATOR = new Parcelable.Creator<DappRPCode>() {
        @Override
        public DappRPCode createFromParcel(Parcel source) {
            return new DappRPCode(source);
        }

        @Override
        public DappRPCode[] newArray(int size) {
            return new DappRPCode[size];
        }
    };
}
