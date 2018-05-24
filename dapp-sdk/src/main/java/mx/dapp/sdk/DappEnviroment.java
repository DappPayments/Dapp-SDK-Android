package mx.dapp.sdk;

/**
 * Created by carlos on 21/06/17.
 */

public enum DappEnviroment {
    PRODUCTION("https://api.dapp.mx/"),
    SANDBOX("https://sandbox.dapp.mx/");

    private String target;

    DappEnviroment(String target){
        this.target = target;
    }

    public String getTarget(){
        return target;
    }
}
