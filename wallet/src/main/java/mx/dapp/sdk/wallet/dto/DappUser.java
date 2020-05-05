package mx.dapp.sdk.wallet.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class DappUser implements Parcelable {

     private String name;
     private String image;
     private String address;
     private boolean suggestTip;

     public DappUser(JSONObject data){
         this.name = data.optString("name");
         this.image = data.optString("image");
         this.address = data.optString("address");
         this.suggestTip = data.optBoolean("suggest_tip");
     }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public boolean isSuggestTip() {
        return suggestTip;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "DappUser{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", address='" + address + '\'' +
                ", suggestTip=" + suggestTip +
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
        dest.writeByte(this.suggestTip ? (byte) 1 : (byte) 0);
    }

    protected DappUser(Parcel in) {
        this.name = in.readString();
        this.image = in.readString();
        this.address = in.readString();
        this.suggestTip = in.readByte() != 0;
    }

    public static final Parcelable.Creator<DappUser> CREATOR = new Parcelable.Creator<DappUser>() {
        @Override
        public DappUser createFromParcel(Parcel source) {
            return new DappUser(source);
        }

        @Override
        public DappUser[] newArray(int size) {
            return new DappUser[size];
        }
    };
}
