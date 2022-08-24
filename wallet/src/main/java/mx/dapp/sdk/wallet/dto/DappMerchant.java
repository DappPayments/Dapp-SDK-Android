package mx.dapp.sdk.wallet.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class DappMerchant implements Parcelable {

     private String name;
     private String image;
     private String address;

     public DappMerchant(JSONObject data){
         this.name = data.optString("name");
         this.image = data.optString("image");
         this.address = data.optString("address");
     }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "DappMerchant{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", address='" + address +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeString(this.address);
    }

    protected DappMerchant(Parcel in) {
        this.name = in.readString();
        this.image = in.readString();
        this.address = in.readString();
    }

    public static final Parcelable.Creator<DappMerchant> CREATOR = new Parcelable.Creator<DappMerchant>() {
        @Override
        public DappMerchant createFromParcel(Parcel source) {
            return new DappMerchant(source);
        }

        @Override
        public DappMerchant[] newArray(int size) {
            return new DappMerchant[size];
        }
    };
}
