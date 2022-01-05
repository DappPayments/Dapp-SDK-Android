package mx.dapp.sdk.vendor.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class DappWallet implements Parcelable {
    public String id;
    private String name;
    private String image;
    private int qrSource;
    private boolean pushNotification;

    public DappWallet(JSONObject data) {
        this.id = data.optString("id");
        this.name = data.optString("name");
        this.image = data.optString("image", null);
        this.qrSource = data.optInt("qr");
        this.pushNotification = data.optBoolean("push_notification");
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getQrSource() {
        return qrSource;
    }

    public boolean isPushNotification() {
        return pushNotification;
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.name = source.readString();
        this.image = source.readString();
        this.qrSource = source.readInt();
        this.pushNotification = source.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeInt(this.qrSource);
        dest.writeByte(this.pushNotification ? (byte) 1 : (byte) 0);
    }

    protected DappWallet(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.image = in.readString();
        this.qrSource = in.readInt();
        this.pushNotification = in.readByte() != 0;
    }

    public static final Creator<DappWallet> CREATOR = new Creator<DappWallet>() {
        @Override
        public DappWallet createFromParcel(Parcel source) {
            return new DappWallet(source);
        }

        @Override
        public DappWallet[] newArray(int size) {
            return new DappWallet[size];
        }
    };
}
