package mx.dapp.sdk.core.dto;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import mx.dapp.sdk.core.callbacks.DappPosCodeCallback;
import mx.dapp.sdk.core.network.DappApi;
import mx.dapp.sdk.core.network.http.DappResponseProcess;

public abstract class AbstractDappPosCode extends AbstractDappCode {

    protected String qrText;
    protected Double tip;
    protected String urlImage;
    protected int expirationMinutes = -1;

    public AbstractDappPosCode(Double amount, Double tip, String description, @Nullable String reference, int expirationMinutes) {
        this.amount = amount;
        this.tip = tip;
        this.description = description;
        this.reference = reference;
        this.expirationMinutes = expirationMinutes;
    }

    protected AbstractDappPosCode(){}

    public AbstractDappPosCode(String dappId) {
        this.dappId = dappId;
    }

    public void create(int qrSource, final DappPosCodeCallback callback) {
        create(qrSource, null, null, callback);
    }

    public void create(int qrSource, String pos, String pin, final DappPosCodeCallback callback) {
        if (dappId != null) {
            callback.onSuccess();
        } else {
            DappApi dappApi = new DappApi();
            dappApi.dappCode(amount.toString(), tip.toString(), pos, pin, description, reference, qrSource, expirationMinutes, new DappResponseProcess(callback) {
                @Override
                public void processSuccess(Object data) {
                    JSONObject result = ((JSONObject) data);
                    dappId = result.optString("id");
                    qrText = result.optString("qr_str");
                    urlImage = result.optString("qr_image");
                    callback.onSuccess();
                }
            });
        }
    }

    public void create(final DappPosCodeCallback callback){
        create(-1, callback);
    }

    public String getQrText() {
        return qrText;
    }

    public Double getTip() {
        return tip;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public int getExpirationMinutes() {
        return expirationMinutes;
    }
}
