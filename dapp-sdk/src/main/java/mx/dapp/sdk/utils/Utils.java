package mx.dapp.sdk.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import java.util.Map;

public class Utils {
    private static final String TAG = "Utils";
    private static Map<Character, Character> MAP_NORM;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static void createAlert(Activity activity, String mensaje) {
        createAlert(activity, null, mensaje);
    }

    public static void createAlert(Activity activity, String mensaje, DialogInterface.OnClickListener ocl) {
        createAlert(activity, null, mensaje, ocl);
    }

    public static void createAlert(Activity activity, String titulo, String mensaje) {
        createAlert(activity, titulo, mensaje, null);
    }

    public static void createAlert(Activity activity, String titulo, String mensaje,
                                   DialogInterface.OnClickListener ocl) {
        createAlert(activity, titulo, mensaje, false, ocl);
    }

    public static void createAlert(Activity activity, String titulo, String mensaje, boolean cancelButton, DialogInterface.OnClickListener ocl) {
        try {
            if (activity == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(titulo)
                    .setMessage(mensaje)
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", ocl);
            if (cancelButton) {
                builder.setNegativeButton("Cancelar", null);
            }
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            Log.w(TAG, "Error al crear alerta", e);
        }
    }

    public static String formatCardNumber(String s) {
        String formatNumber = "";
        for (int i = 1; i <= s.length(); i++) {
            if (i % 4 == 0) {
                formatNumber += s.charAt(i - 1) + " ";
            } else {
                formatNumber += s.charAt(i - 1);
            }
        }
        return formatNumber;
    }

    public static CardType getCardType(String number) {
        String ptVisa = "^4[0-9]{6,}$";
        String ptMasterCard = "^5[1-5][0-9]{5,}$";
        String ptAmeExp = "^3[47][0-9]{5,}$";

        if (number.matches(ptVisa)) {
            return CardType.VISA;
        } else if (number.matches(ptMasterCard)) {
            return CardType.MASTERCARD;
        } else if (number.matches(ptAmeExp)) {
            return CardType.AMEX;
        }
        return CardType.UNKNOWN;
    }

    public enum CardType {
        VISA("Visa"),
        MASTERCARD("MasterCard"),
        AMEX("American Express"),
        UNKNOWN("No valida");

        private String texto;

        CardType(String texto) {
            this.texto = texto;
        }

        public String getTexto() {
            return texto;
        }
    }

}
