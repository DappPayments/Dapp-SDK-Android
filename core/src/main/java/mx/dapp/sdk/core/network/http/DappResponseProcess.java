package mx.dapp.sdk.core.network.http;

import org.json.JSONObject;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.core.exceptions.DappException;

/**
 * Created by carlos on 9/06/17.
 */

public abstract class DappResponseProcess {
    private DappCallback callback;

    public DappResponseProcess(DappCallback callback) {
        this.callback = callback;
    }

    public abstract void processSuccess(JSONObject data);

    public void onError(int rc, String msg) {
        callback.onError(new DappException(msg, rc));
    }

    public void processFailed(Exception e) {
        callback.onError(new DappException(e.getMessage(), -1));
    }

    public void processStart(){

    }
}
