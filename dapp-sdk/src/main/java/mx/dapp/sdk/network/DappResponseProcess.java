package mx.dapp.sdk.network;

/**
 * Created by carlos on 9/06/17.
 */

public interface DappResponseProcess {
    void processStart();
    void processSuccess(String json);
    void processFailed(Exception e);
}
