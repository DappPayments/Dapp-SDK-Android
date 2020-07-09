package mx.dapp.sdk.wallet.dto;

import org.json.JSONObject;

import mx.dapp.sdk.core.dto.AbstractDappCard;
import mx.dapp.sdk.core.enums.DappCardResult;
import mx.dapp.sdk.core.exceptions.DappCardException;
import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.wallet.callbacks.WalletDappCardCallback;
import mx.dapp.sdk.wallet.network.DappWalletApi;

public class WalletDappCard extends AbstractDappCard {

    private WalletDappCard(JSONObject data){
        super(data);
    }

    public static void add(String cardNumber, String cardHolder, String expMonth, String expYear, String cvv, String email, String phoneNumber, final WalletDappCardCallback callback) throws Exception {
        DappCardResult result = validateCardData(cardNumber, cardHolder, expMonth, expYear, cvv, email, phoneNumber);
        if (result == DappCardResult.RESULT_OK) {
            DappWalletApi api = new DappWalletApi();
            api.card(cardNumber, cardHolder, expMonth, expYear, cvv, email, phoneNumber, new DappResponseProcess(callback) {
                @Override
                public void processSuccess(JSONObject data) {
                    WalletDappCard card = new WalletDappCard(data);
                    callback.onSuccess(card);
                }
            });
        } else {
            throw new DappCardException(result);
        }
    }
}
