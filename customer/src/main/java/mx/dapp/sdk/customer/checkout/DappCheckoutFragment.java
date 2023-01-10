package mx.dapp.sdk.customer.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import mx.dapp.sdk.R;
import mx.dapp.sdk.core.Dapp;
import mx.dapp.sdk.core.enums.DappEnviroment;
import mx.dapp.sdk.core.enums.DappResult;
import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.customer.dto.DappPayment;
import mx.dapp.sdk.customer.dto.DappPosCode;

public class DappCheckoutFragment extends Fragment {

    private WebView webView;

    private DappCheckoutCallback mCallback;

    public DappCheckoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dapp_checkout, container, false);
        webView = view.findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new DappCheckoutJSInterface(this), "Android");

        return view;
    }

    public void loadDappPosCode(DappPosCode dappPosCode, DappCheckoutCallback callback) {
        mCallback = callback;

        if (dappPosCode == null || dappPosCode.getDappId() == null) {
            DappException de = new DappException("Invalid Dapp Code", DappResult.RESULT_DEFAULT.getCode());
            mCallback.onError(de);
            return;
        }

        String baseURL = "https://dapp.mx/c/";
        if (Dapp.getEnviroment() != DappEnviroment.PRODUCTION) {
            baseURL = "https://sandbox.dapp.mx/c/";
        }
        webView.loadUrl(baseURL + dappPosCode.getDappId());
    }

    protected void paymentCompleted(String jsonStr) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();

            DappException de = new DappException(e.getMessage(), DappResult.RESULT_DEFAULT.getCode());
            mCallback.onError(de);

            return;
        }
        DappPayment dappPayment = new DappPayment(jsonObj);

        mCallback.onSuccess(dappPayment);
    }

}
