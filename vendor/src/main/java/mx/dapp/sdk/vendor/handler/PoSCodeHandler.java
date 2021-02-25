package mx.dapp.sdk.vendor.handler;

import android.os.Handler;

import org.json.JSONObject;

import mx.dapp.sdk.core.callbacks.DappPaymentCallback;
import mx.dapp.sdk.core.callbacks.DappSocketStatusCallback;
import mx.dapp.sdk.core.dto.DappPayment;
import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.core.handler.AbstractStatusHandler;
import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.vendor.network.DappVendorApi;

public class PoSCodeHandler extends AbstractStatusHandler {

    private DappPaymentCallback callback;

    public PoSCodeHandler(String code, DappPaymentCallback callback) {
        super(code);
        this.callback = callback;
    }

    @Override
    public void startRequest(){
        isListening = true;
        DappVendorApi dappVendorApi = new DappVendorApi();
        dappVendorApi.paymentStatusBySocket(code, new DappSocketStatusCallback() {
            @Override
            public void onMessage(JSONObject data) {
                DappPayment payment = new DappPayment(data);
                callback.onSuccess(payment);
            }

            @Override
            public void onError(DappException exception) {
                if(isListening){
                    getStatusByService();
                }
            }
        });
    }

    private void getStatusByService(){
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                getStatusByService();
            }
        };
        DappVendorApi dappVendorApi = new DappVendorApi();
        dappVendorApi.paymentStatusByService(code, new DappResponseProcess(callback) {
            @Override
            public void processSuccess(Object data) {
                if (data == null) {
                    if (timerHandler == null) {
                        timerHandler = new Handler();
                    }
                    timerHandler.postDelayed(timerRunnable, AUTOMATIC_SECONDS * 1000);
                } else {
                    DappPayment payment = new DappPayment((JSONObject)data);
                    callback.onSuccess(payment);
                }
            }

            @Override
            public void processFailed(Exception e) {
                if (scheduleErrorCount == 2) {
                    super.processFailed(e);
                    stopRequest();
                } else {
                    scheduleErrorCount += 1;
                }
            }
        });
    }
}
