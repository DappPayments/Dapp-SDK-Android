package mx.dapp.sdk.core.network;

import android.util.Base64;

import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import mx.dapp.sdk.core.Dapp;
import mx.dapp.sdk.core.network.http.DappResponseProcess;


public class DappApi extends AbstractDappApi {

    @Override
    public String getHeader() {
        byte[] data = new byte[0];
        try {
            data = (":" + Dapp.getApiKey()).getBytes("UTF-8");
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
                base = "https://sandbox.dapp.mx/";
                break;
            case PRODUCTION:
                base = "https://api.dapp.mx/";
                break;
        }
        return base + URL_VERSION;
    }

    @Override
    public String getSocketUrl() {
        switch (Dapp.getEnviroment()) {
            case SANDBOX:
                return "wss://sandbox.dapp.mx/sockets/";
            case PRODUCTION:
                return "wss://api.dapp.mx/sockets/";
            default:
                return null;
        }
    }

    public void dappCode(String amount, String tip, String description, @Nullable String reference,
                         int qrSource, int expirationMinutes, DappResponseProcess responseHandler) {
        dappCode(amount, tip, null, null, description, reference, qrSource, expirationMinutes, responseHandler);
    }

    public void dappCode(String amount, String tip, @Nullable String pos, @Nullable String pin,
                         String description, @Nullable String reference, int qrSource,
                         int expirationMinutes, DappResponseProcess responseHandler) {
        HashMap<String, String> postValues = new HashMap<>();
        postValues.put("amount", amount);
        postValues.put("tip", tip);
        postValues.put("description", description);

        if (pos != null) {
            postValues.put("pos", pos);
        }
        if (pin != null) {
            postValues.put("pin", pin);
        }
        if (reference != null) {
            postValues.put("reference", reference);
        }
        if (expirationMinutes > 0) {
            postValues.put("expiration_minutes", Integer.toString(expirationMinutes));
        }
        if (qrSource != -1) {
            postValues.put("qr_source", Integer.toString(qrSource));
        }
        execute(postValues, "/dapp-codes", responseHandler);
    }
}
