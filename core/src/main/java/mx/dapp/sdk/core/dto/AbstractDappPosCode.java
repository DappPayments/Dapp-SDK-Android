package mx.dapp.sdk.core.dto;

import org.json.JSONObject;

import mx.dapp.sdk.core.callbacks.DappPosCodeCallback;
import mx.dapp.sdk.core.network.DappApi;
import mx.dapp.sdk.core.network.http.DappResponseProcess;

public abstract class AbstractDappPosCode extends AbstractDappCode {
    public AbstractDappPosCode(Double amount, String description, String reference) {
        this.amount = amount;
        this.description = description;
        this.reference = reference;
    }

    public void create(final DappPosCodeCallback callback) {
        DappApi dappApi = new DappApi();
        dappApi.dappCode(amount.toString(), description, reference, new DappResponseProcess(callback) {
            @Override
            public void processSuccess(JSONObject data) {
                dappId = data.optString("id");
                callback.onSuccess();
            }
        });
    }
}
