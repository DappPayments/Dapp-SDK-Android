/**
 * Clase para encriptar y desencriptar por medio de RSA
 * con llave publica
 *
 * @autor Jorge Alfaro
 */

package mx.dapp.sdk.core.network;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import mx.dapp.sdk.core.Dapp;
import mx.dapp.sdk.core.R;
import mx.dapp.sdk.core.enums.DappEnviroment;

class DappEncryption {

    private static final String TAG = "RSACrypt";

    private static PublicKey getPublicKey() throws Exception {
        InputStream is = Dapp.getEnviroment() == DappEnviroment.SANDBOX ? Dapp.getContext().getResources().openRawResource(R.raw.dapp_sandbox) : Dapp.getContext().getResources().openRawResource(R.raw.dapp_production);
        byte[] keyBytes = readBytes(is);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    public static String rsaEncrypt(String plain) {
        try {
            plain = plain.replace("á", "a");
            plain = plain.replace("é", "e");
            plain = plain.replace("í", "i");
            plain = plain.replace("ó", "o");
            plain = plain.replace("ú", "u");
            plain = plain.replace("ä", "a");
            plain = plain.replace("ë", "e");
            plain = plain.replace("ï", "i");
            plain = plain.replace("ö", "o");
            plain = plain.replace("ü", "u");
            plain = plain.replace("Á", "a");
            plain = plain.replace("É", "e");
            plain = plain.replace("Í", "i");
            plain = plain.replace("Ó", "o");
            plain = plain.replace("Ú", "u");
            plain = plain.replace("Ä", "a");
            plain = plain.replace("Ë", "e");
            plain = plain.replace("ï", "i");
            plain = plain.replace("Ö", "o");
            plain = plain.replace("Ü", "u");
            plain = plain.replace("ñ", "n");
            byte[] encryptedBytes;
            Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding");
            PublicKey publicKey = getPublicKey();
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encryptedBytes = cipher.doFinal(plain.getBytes("UTF-8"));
            return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(TAG, "Error encriptando", e);
        }
        return "";
    }
}