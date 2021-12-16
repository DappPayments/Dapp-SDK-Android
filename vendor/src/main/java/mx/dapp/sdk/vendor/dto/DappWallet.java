package mx.dapp.sdk.vendor.dto;

import org.json.JSONObject;

public class DappWallet {
    public String id;
    private String name;
    private String image;

    public DappWallet(JSONObject data) {
        this.id = data.optString("id");
        this.name = data.optString("name");
        this.image = data.optString("image", null);
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
