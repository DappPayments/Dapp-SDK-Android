package mx.dapp.sdk.dto;

import org.json.JSONObject;

/**
 * Created by carlos on 9/06/17.
 */

public class DappCard {

    private String id;
    private String lastFour;
    private String cardHolder;
    private String brand;

    public DappCard(JSONObject data) {
        this.id = data.optString("id");
        this.lastFour = data.optString("last_4");
        this.cardHolder = data.optString("cardholder");
        this.brand = data.optString("brand");
    }

    public String getId() {
        return id;
    }

    public String getLastFour() {
        return lastFour;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public String getBrand() {
        return brand;
    }
}
