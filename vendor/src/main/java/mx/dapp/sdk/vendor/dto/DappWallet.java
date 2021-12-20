package mx.dapp.sdk.vendor.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class DappWallet implements Parcelable {
    public String id;
    private String name;
    private String image;

    public DappWallet(JSONObject data) {
        this.id = data.optString("id");
        this.name = data.optString("name");
        this.image = data.optString("image", null);
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
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
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.name = source.readString();
        this.image = source.readString();
    }

    protected DappWallet(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.image = in.readString();
    }

    public static final Parcelable.Creator<DappWallet> CREATOR = new Parcelable.Creator<DappWallet>() {
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
