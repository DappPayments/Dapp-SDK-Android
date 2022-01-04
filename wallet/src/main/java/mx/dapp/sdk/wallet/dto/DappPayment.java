package mx.dapp.sdk.wallet.dto;

import org.json.JSONObject;

import mx.dapp.sdk.core.dto.AbstractDappPayment;

public class DappPayment extends AbstractDappPayment {
    public DappPayment(JSONObject data) {
        super(data);
    }
}
