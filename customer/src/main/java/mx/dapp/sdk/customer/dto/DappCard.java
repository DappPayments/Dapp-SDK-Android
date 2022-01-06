package mx.dapp.sdk.customer.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import mx.dapp.sdk.customer.callbacks.DappCardCallback;
import mx.dapp.sdk.core.dto.AbstractDappCard;
import mx.dapp.sdk.core.enums.DappCardResult;
import mx.dapp.sdk.core.exceptions.DappCardException;
import mx.dapp.sdk.core.network.DappApi;
import mx.dapp.sdk.core.network.http.DappResponseProcess;

public class DappCard extends AbstractDappCard implements Parcelable {

    private DappCard(JSONObject data){
        super(data);
    }

    public static void add(String cardNumber, String cardHolder, String expMonth, String expYear, String cvv, String email, String phoneNumber, final DappCardCallback callback) throws Exception {
        DappCardResult result = validateCardData(cardNumber, cardHolder, expMonth, expYear, cvv, email, phoneNumber);
        if (result == DappCardResult.RESULT_OK) {
            DappApi api = new DappApi();
            api.card(cardNumber, cardHolder, expMonth, expYear, cvv, email, phoneNumber, new DappResponseProcess(callback) {
                @Override
                public void processSuccess(Object data) {
                    DappCard card = new DappCard((JSONObject)data);
                    callback.onSuccess(card);
                }
            });
        } else {
            throw new DappCardException(result);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.lastFour);
        dest.writeString(this.cardHolder);
        dest.writeString(this.brand);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.lastFour = source.readString();
        this.cardHolder = source.readString();
        this.brand = source.readString();
    }

    protected DappCard(Parcel in) {
        this.id = in.readString();
        this.lastFour = in.readString();
        this.cardHolder = in.readString();
        this.brand = in.readString();
    }

    public static final Parcelable.Creator<DappCard> CREATOR = new Parcelable.Creator<DappCard>() {
        @Override
        public DappCard createFromParcel(Parcel source) {
            return new DappCard(source);
        }

        @Override
        public DappCard[] newArray(int size) {
            return new DappCard[size];
        }
    };
}
