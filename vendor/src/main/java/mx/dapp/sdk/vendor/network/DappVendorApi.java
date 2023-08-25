package mx.dapp.sdk.vendor.network;


import androidx.annotation.Nullable;

import java.util.HashMap;

import mx.dapp.sdk.core.callbacks.DappSocketStatusCallback;
import mx.dapp.sdk.core.network.DappApi;
import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.core.network.ws.DappWsClient;
import okhttp3.WebSocket;

public class DappVendorApi extends DappApi {

    public void paymentCode(String qrCode, String amount, String tip,
                            String description, String reference,
                            String pos, String pin,
                            DappResponseProcess responseHandler) {
        HashMap<String, String> postValues = new HashMap<>();
        postValues.put("amount", amount);
        if (tip != null) {
            postValues.put("tip", tip);
        }
        postValues.put("description", description);
        postValues.put("reference", reference);
        if (pos != null) {
            postValues.put("pos", pos);
        }
        if (pin != null) {
            postValues.put("pin", pin);
        }

        execute(postValues, "/payments/code/" + qrCode, responseHandler);
    }

    public void dappCode(String amount, String description, String reference, DappResponseProcess responseHandler) {
        HashMap<String, String> postValues = new HashMap<>();
        postValues.put("amount", amount);
        postValues.put("description", description);
        postValues.put("reference", reference);

        execute(postValues, "/dapp-codes", responseHandler);
    }

    public void paymentStatusByService(String code, DappResponseProcess responseHandler) {
        execute("/dapp-codes/" + code + "/payment", responseHandler);
    }

    public void sendPushNotification(String code, String phoneNumber, DappResponseProcess responseHandler) {
        HashMap<String, String> postValues = new HashMap<>();
        postValues.put("phone", phoneNumber);

        execute(postValues, "/dapp-codes/" + code + "/codi/push", responseHandler);
    }

    public void dappCodePush(String code, String phoneNumber, String destinationId, DappResponseProcess responseHandler) {
        HashMap<String, String> postValues = new HashMap<>();
        postValues.put("phone", phoneNumber);
        postValues.put("destination", destinationId);

        execute(postValues, "/dapp-codes/" + code + "/push", responseHandler);

    }

    public void dappCodePushDestinations(DappResponseProcess responseHandler) {
        execute("/dapp-codes/push/destinations", responseHandler);
    }

    public WebSocket paymentStatusBySocket(String code, DappSocketStatusCallback callback) {
        DappWsClient dappWsClient = new DappWsClient(getSocketUrl() + "dapp-code/" + code, getHeader(), callback);
        return dappWsClient.createSocket();
    }

    public void dappCodesWallets(DappResponseProcess responseHandler) {
        execute("/dapp-codes/wallets", responseHandler);
    }

    public void getPayments(String fecha_inicio, String fecha_fin, DappResponseProcess responseHandler) {
        execute("/payments?start_date=" + fecha_inicio + "&end_date=" + fecha_fin, responseHandler);
    }

    public void cashInReferences(String reference, DappResponseProcess responseHandler) {
        execute("/cashin/references/" + reference, responseHandler);
    }

    public void cashIn(String reference, String amount, @Nullable String merchantReference, String store, String pos, DappResponseProcess responseHandler) {
        HashMap<String, String> postValues = new HashMap<>();
        postValues.put("reference", reference);
        postValues.put("amount", amount);
        postValues.put("store", store);
        postValues.put("pos", pos);

        if (merchantReference != null) {
            postValues.put("merchant_reference", merchantReference);
        }

        execute(postValues, "/cashin", responseHandler);
    }

}
