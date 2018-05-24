package mx.dapp.sdk.network;

import android.content.Context;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import mx.dapp.sdk.crypto.RSACrypt;
import mx.dapp.sdk.tools.Dapp;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.net.ssl.SSLContext;

/**
 * Created by carlos on 31/05/17.
 */

public class NetConnection {

    static {
        initializeSSLContext(Dapp.getContext());
    }

    /**
     * Initialize SSL
     * @param mContext
     */
    public static void initializeSSLContext(Context mContext){
        try {
            SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            ProviderInstaller.installIfNeeded(mContext.getApplicationContext());
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public static void getCode(String amount, String description, String reference, DappResponseProcess responseHandler) {
        HashMap<String, String> postValues = new HashMap<>();
        postValues.put("amount", amount);
        postValues.put("description", description);
        postValues.put("reference", reference);

        DappConnect dp = new DappConnect(postValues, responseHandler);
        dp.execute("/dapp-codes/");
    }

    public static void addCard(String cardNumber, String cardHolder, String expMonth, String expYear, String cvv, String email, String telefono, DappResponseProcess responseHandler) {
        HashMap<String, String> postValues = new HashMap<>();

        postValues.put("card_number", RSACrypt.rsaEncrypt(cardNumber));
        postValues.put("cardholder", RSACrypt.rsaEncrypt(cardHolder));
        postValues.put("cvv", RSACrypt.rsaEncrypt(cvv));
        postValues.put("exp_month", RSACrypt.rsaEncrypt(expMonth));
        postValues.put("exp_year", RSACrypt.rsaEncrypt(expYear));
        postValues.put("email", RSACrypt.rsaEncrypt(email));
        postValues.put("phone_number", RSACrypt.rsaEncrypt(telefono));

        DappConnect dp = new DappConnect(postValues, responseHandler);
        dp.execute("/cards/");
    }
}
