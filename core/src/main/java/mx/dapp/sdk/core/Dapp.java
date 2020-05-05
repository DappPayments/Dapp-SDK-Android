package mx.dapp.sdk.core;

import android.content.Context;

import java.util.TimeZone;

import mx.dapp.sdk.core.enums.DappEnviroment;

public class Dapp {

    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");
    public static final String DAPP_CALLBACK_URL_REQUEST = "DAPP_CALLBACK_URL_REQUEST";
    public static final int DAPP_REQUEST = 555;
    public static final String DAPP_HOST = "https://dapp.mx/";

    private static String apiKey;
    private static DappEnviroment enviroment;
    private static Context context;

    public static void init(String apiKey, DappEnviroment enviroment, Context context) {
        Dapp.apiKey = apiKey;
        Dapp.enviroment = enviroment;
        Dapp.context = context;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static DappEnviroment getEnviroment() {
        return enviroment;
    }

    public static Context getContext() {
        return context;
    }
}
