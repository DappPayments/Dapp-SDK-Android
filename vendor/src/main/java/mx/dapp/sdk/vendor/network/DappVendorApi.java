package mx.dapp.sdk.vendor.network;


import java.util.HashMap;

import mx.dapp.sdk.core.callbacks.DappSocketStatusCallback;
import mx.dapp.sdk.core.network.DappApi;
import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.core.network.ws.DappWsClient;
import okhttp3.WebSocket;

public class DappVendorApi extends DappApi {

    public void paymentCode(String qrCode, String amount, String description, String reference, DappResponseProcess responseHandler) {
        HashMap<String, String> postValues = new HashMap<>();
        postValues.put("amount", amount);
        postValues.put("description", description);
        postValues.put("reference", reference);

        execute(postValues, "/payments/code/" + qrCode, responseHandler);
    }

    public void dappCode(String amount, String description, String reference, DappResponseProcess responseHandler) {
        HashMap<String, String> postValues = new HashMap<>();
        postValues.put("amount", amount);
        postValues.put("description", description);
        postValues.put("reference", reference);

        execute(postValues, "/dapp-codes/", responseHandler);
    }

    public void paymentStatusByService(String code, DappResponseProcess responseHandler) {
        execute("/dapp-codes/" + code + "/payment/", responseHandler);
    }

    public void sendPushNotification(String code, String phoneNumber, DappResponseProcess responseHandler){
        HashMap<String, String> postValues = new HashMap<>();
        postValues.put("phone", phoneNumber);

        execute(postValues, "/dapp-codes/" + code + "/codi/push/", responseHandler);
    }

    public void dappCodePush(String code, String phoneNumber, String destinationId, DappResponseProcess responseHandler){
        HashMap<String, String> postValues = new HashMap<>();
        postValues.put("phone", phoneNumber);
        postValues.put("destination", destinationId);

        execute(postValues, "/dapp-codes/" + code + "/push/", responseHandler);

    }

    public void dappCodePushDestinations(DappResponseProcess responseHandler){
        execute("/dapp-codes/push/destinations/", responseHandler);
    }

    public WebSocket paymentStatusBySocket(String code, DappSocketStatusCallback callback) {
        DappWsClient dappWsClient = new DappWsClient(getSocketUrl() + "dapp-code/" + code + "/", getHeader(), callback);
        return dappWsClient.createSocket();
    }
}
