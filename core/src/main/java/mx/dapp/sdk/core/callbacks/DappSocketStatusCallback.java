package mx.dapp.sdk.core.callbacks;

import org.json.JSONObject;

public interface DappSocketStatusCallback extends DappCallback{
    void onMessage(JSONObject data);
}
