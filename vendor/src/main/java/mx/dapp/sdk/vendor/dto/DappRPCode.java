package mx.dapp.sdk.vendor.dto;

import org.json.JSONObject;

import mx.dapp.sdk.core.callbacks.DappPaymentCallback;
import mx.dapp.sdk.core.dto.AbstractDappRPCode;
import mx.dapp.sdk.core.dto.DappPayment;
import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.vendor.network.DappVendorApi;

public class DappRPCode extends AbstractDappRPCode {

    public DappRPCode(String qrString){
        this.qrString = qrString;
    }

    public void charge(Double amount, String description, String reference, final DappPaymentCallback callback){
        DappVendorApi api = new DappVendorApi();
        api.paymentCode(qrString, amount.toString(), description, reference, new DappResponseProcess(callback) {
            @Override
            public void processSuccess(JSONObject data) {
                DappPayment payment = new DappPayment(data);
                callback.onSuccess(payment);
            }
        });
    }
}
