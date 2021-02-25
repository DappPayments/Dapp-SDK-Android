package mx.dapp.sdk.customer.dto;

import org.json.JSONObject;

import mx.dapp.sdk.customer.callbacks.DappCardCallback;
import mx.dapp.sdk.core.dto.AbstractDappCard;
import mx.dapp.sdk.core.enums.DappCardResult;
import mx.dapp.sdk.core.exceptions.DappCardException;
import mx.dapp.sdk.core.network.DappApi;
import mx.dapp.sdk.core.network.http.DappResponseProcess;

public class DappCard extends AbstractDappCard {

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
}
