package mx.dapp.sdk.vendor.dto;

import org.json.JSONObject;

public class DappWallet {
    public String id;
    private String name;

    public DappWallet(JSONObject data) {
        this.id = data.optString("id");
        this.name = data.optString("name");
    }

    public String getName() {
        return name;
    }
}
