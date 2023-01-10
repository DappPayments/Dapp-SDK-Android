package mx.dapp.sdk.customer.checkout;

import android.webkit.JavascriptInterface;

public class DappCheckoutJSInterface {

    DappCheckoutFragment mCheckoutFragment;

    DappCheckoutJSInterface(DappCheckoutFragment f) {
        mCheckoutFragment = f;
    }

    @JavascriptInterface
    public void paymentCompleted(String jsonStr) {
        mCheckoutFragment.paymentCompleted(jsonStr);
    }

}
