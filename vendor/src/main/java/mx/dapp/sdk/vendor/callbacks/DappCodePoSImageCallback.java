package mx.dapp.sdk.vendor.callbacks;

import android.graphics.Bitmap;

import mx.dapp.sdk.core.callbacks.DappCallback;

public interface DappCodePoSImageCallback extends DappCallback {
    void onSuccess(Bitmap bitmap);
}
