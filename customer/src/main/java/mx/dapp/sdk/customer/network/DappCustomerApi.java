package mx.dapp.sdk.customer.network;

import java.util.HashMap;

import mx.dapp.sdk.core.network.DappApi;
import mx.dapp.sdk.core.network.http.DappResponseProcess;

public class DappCustomerApi extends DappApi {

    public void paymentInfo(String id, DappResponseProcess responseHandler) {
        execute("/payments/" + id + "/", responseHandler);
    }
}
