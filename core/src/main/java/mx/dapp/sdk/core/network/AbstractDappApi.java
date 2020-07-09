package mx.dapp.sdk.core.network;

import android.content.Context;

import com.google.android.gms.security.ProviderInstaller;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.net.ssl.SSLContext;

import mx.dapp.sdk.core.Dapp;
import mx.dapp.sdk.core.network.http.DappHttpClient;
import mx.dapp.sdk.core.network.http.DappResponseProcess;

public abstract class AbstractDappApi {

    static {
        initializeSSLContext(Dapp.getContext());
    }

    protected static final String URL_VERSION = "v1/";

    private static void initializeSSLContext(Context mContext) {
        try {
            SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            ProviderInstaller.installIfNeeded(mContext.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        } catch (NoClassDefFoundError ndf) {
            ndf.printStackTrace();
        }
    }

    private void execute(String method, HashMap<String, String> postValues, String endpoint, DappResponseProcess responseProcess){
        if(Dapp.getApiKey() == null || Dapp.getApiKey().isEmpty()){
            responseProcess.onError(-1, "Invalid API KEY");
        }else{
            DappHttpClient dappHttpClient = new DappHttpClient(method, postValues, getHeader(), responseProcess);
            dappHttpClient.execute(getHttpUrl() + endpoint);
        }
    }

    protected void execute(HashMap<String, String> postValues, String endpoint, DappResponseProcess responseProcess){
        execute("POST", postValues, endpoint, responseProcess);
    }

    protected void execute(String endpoint, DappResponseProcess responseProcess){
        execute("GET", null, endpoint, responseProcess);
    }

    protected void execute(String method, String endpoint, DappResponseProcess responseProcess){
        execute(method, null, endpoint, responseProcess);
    }

    public abstract String getHeader();

    public abstract String getHttpUrl();

    public abstract String getSocketUrl();

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
}
