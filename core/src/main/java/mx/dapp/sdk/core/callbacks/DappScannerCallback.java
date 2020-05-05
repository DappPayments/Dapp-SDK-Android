package mx.dapp.sdk.core.callbacks;

public interface DappScannerCallback extends DappCallback {
    void onScan(String result);
    void onClose();
}
