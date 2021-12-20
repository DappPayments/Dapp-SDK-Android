package mx.dapp.sdk.wallet.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import mx.dapp.sdk.core.dto.AbstractDappCard;
import mx.dapp.sdk.core.enums.DappCardResult;
import mx.dapp.sdk.core.exceptions.DappCardException;
import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.wallet.callbacks.WalletDappCardCallback;
import mx.dapp.sdk.wallet.network.DappWalletApi;

public class WalletDappCard extends AbstractDappCard implements Parcelable {

    private WalletDappCard(JSONObject data){
        super(data);
    }

    public static void add(String cardNumber, String cardHolder, String expMonth, String expYear, String cvv, String email, String phoneNumber, final WalletDappCardCallback callback) throws Exception {
        DappCardResult result = validateCardData(cardNumber, cardHolder, expMonth, expYear, cvv, email, phoneNumber);
        if (result == DappCardResult.RESULT_OK) {
            DappWalletApi api = new DappWalletApi();
            api.card(cardNumber, cardHolder, expMonth, expYear, cvv, email, phoneNumber, new DappResponseProcess(callback) {
                @Override
                public void processSuccess(Object data) {
                    WalletDappCard card = new WalletDappCard((JSONObject)data);
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

    protected WalletDappCard(Parcel in) {
        this.id = in.readString();
        this.lastFour = in.readString();
        this.cardHolder = in.readString();
        this.brand = in.readString();
    }

    public static final Parcelable.Creator<WalletDappCard> CREATOR = new Parcelable.Creator<WalletDappCard>() {
        @Override
        public WalletDappCard createFromParcel(Parcel source) {
            return new WalletDappCard(source);
        }

        @Override
        public WalletDappCard[] newArray(int size) {
            return new WalletDappCard[size];
        }
    };
}
