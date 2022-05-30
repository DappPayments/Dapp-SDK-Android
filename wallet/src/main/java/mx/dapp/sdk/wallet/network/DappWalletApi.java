package mx.dapp.sdk.wallet.network;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

import mx.dapp.sdk.core.Dapp;
import mx.dapp.sdk.core.callbacks.DappSocketStatusCallback;
import mx.dapp.sdk.core.network.AbstractDappApi;
import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.core.network.ws.DappWsClient;
import okhttp3.WebSocket;

public class DappWalletApi extends AbstractDappApi {

    @Override
    public String getHeader() {
        byte[] data = new byte[0];
        try {
            data = (Dapp.getApiKey() + ":").getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    @Override
    public String getHttpUrl() {
        String base = "";
        switch (Dapp.getEnviroment()) {
            case SANDBOX:
                base = "https://wallets-sandbox.dapp.mx/";
                break;
            case PRODUCTION:
                base = "https://wallets.dapp.mx/";
                break;
        }
        return base + URL_VERSION;
    }

    @Override
    public String getSocketUrl() {
        switch (Dapp.getEnviroment()) {
            case SANDBOX:
                return "wss://wallets-sandbox.dapp.mx/sockets/";
            case PRODUCTION:
                return "wss://wallets.dapp.mx/sockets/";
            default:
                return null;
        }
    }

    public void dappCode(String code, DappResponseProcess responseHandler) {
        execute("dapp-codes/" + code, responseHandler);
    }

    public WebSocket getPaymentCodeSocket(String code, DappSocketStatusCallback callback) {
        DappWsClient dappWsClient = new DappWsClient(getSocketUrl() + "payments/code/" + code, getHeader(), callback);
        return dappWsClient.createSocket();
    }

    public void paymentStatus(String code, DappResponseProcess responseHandler) {
        execute("payments/code/" + code + "/status", responseHandler);
    }

    public void renewPaymentCode(String code, DappResponseProcess responseHandler) {
        execute("PUT", "payments/code/" + code, responseHandler);
    }

    public void deletePaymentCode(String code, DappResponseProcess responseHandler) {
        execute("DELETE", "payments/code/" + code, responseHandler);
    }
}
