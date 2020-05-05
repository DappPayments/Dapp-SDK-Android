package mx.dapp.sdk.customer.network;

import java.util.HashMap;

import mx.dapp.sdk.core.network.DappApi;
import mx.dapp.sdk.core.network.http.DappResponseProcess;

public class DappCustomerApi extends DappApi {

    public void card(String cardNumber, String cardHolder, String expMonth, String expYear, String cvv, String email, String telefono, DappResponseProcess responseHandler) {
        HashMap<String, String> postValues = new HashMap<>();

        postValues.put("card_number", DappEncryption.rsaEncrypt(cardNumber));
        postValues.put("cardholder", DappEncryption.rsaEncrypt(cardHolder));
        postValues.put("cvv", DappEncryption.rsaEncrypt(cvv));
        postValues.put("exp_month", DappEncryption.rsaEncrypt(expMonth));
        postValues.put("exp_year", DappEncryption.rsaEncrypt(expYear));
        postValues.put("email", DappEncryption.rsaEncrypt(email));
        postValues.put("phone_number", DappEncryption.rsaEncrypt(telefono));

        execute(postValues, "cards/", responseHandler);
    }

    public void paymentInfo(String id, DappResponseProcess responseHandler) {
        execute("/payments/" + id + "/", responseHandler);
    }
}
