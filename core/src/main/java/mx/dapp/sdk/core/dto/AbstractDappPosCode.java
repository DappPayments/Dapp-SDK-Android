package mx.dapp.sdk.core.dto;

import org.json.JSONObject;

import mx.dapp.sdk.core.callbacks.DappPosCodeCallback;
import mx.dapp.sdk.core.network.DappApi;
import mx.dapp.sdk.core.network.http.DappResponseProcess;

public abstract class AbstractDappPosCode extends AbstractDappCode {

    protected String qrText;

    public AbstractDappPosCode(Double amount, String description, String reference) {
        this.amount = amount;
        this.description = description;
        this.reference = reference;
    }

    public AbstractDappPosCode(String dappId) {
        this.dappId = dappId;
    }

    public void create(final DappPosCodeCallback callback) {
        if (dappId != null) {
            callback.onSuccess();
        } else {
            DappApi dappApi = new DappApi();
            dappApi.dappCode(amount.toString(), description, reference, new DappResponseProcess(callback) {
                @Override
                public void processSuccess(Object data) {
                    JSONObject result = ((JSONObject)data);
                    dappId = result.optString("id");
                    qrText = result.optString("qr_str");
                    callback.onSuccess();
                }
            });
        }
    }

    public String getQrText() {
        return qrText;
    }
}
