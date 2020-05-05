package mx.dapp.sdk.wallet.dto;

import java.util.Date;

import mx.dapp.sdk.core.dto.AbstractDappRPCode;
import mx.dapp.sdk.core.dto.DappPayment;
import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.wallet.callbacks.DappRPCodeCallback;
import mx.dapp.sdk.wallet.callbacks.DappRPCodeStatusCallback;
import mx.dapp.sdk.wallet.handler.RPCodeStatusHandler;

public class DappRPCode extends AbstractDappRPCode implements DappRPCodeStatusCallback {

    public final int MAX_TIMES_RENEWED = 5;

    private String id;
    private Date readExpitation;
    private Date renewExpiration;
    private int timesRenewed;
    private DappRPCodeCallback callback;
    private RPCodeStatusHandler handler;


    public DappRPCode(String id, String qrString, Date readExpiration, Date renewExpiration, DappRPCodeCallback callback) {
        this.id = id;
        this.qrString = qrString;
        this.renewExpiration = renewExpiration;
        this.readExpitation = readExpiration;
        this.callback = callback;
        handler = new RPCodeStatusHandler(this, this);
    }

    public void listen() {
        handler.startRequest();
    }

    public void stopListening() {
        handler.stopRequest();
    }

    public void renew() {
        handler.renew();
    }

    public void delete() {
        handler.delete();
    }

    public boolean isRenewable() {
        return renewExpiration.after(new Date()) && timesRenewed < MAX_TIMES_RENEWED;
    }

    public String getId() {
        return id;
    }

    public Date getReadExpitation() {
        return readExpitation;
    }

    public Date getRenewExpiration() {
        return renewExpiration;
    }

    @Override
    public void onPay(DappPayment payment) {
        callback.onPay(payment);
    }

    @Override
    public void onRenew(String qrString, Date readExpiration, Date renewExpiration, int timesRenew) {
        this.qrString = qrString;
        this.renewExpiration = renewExpiration;
        this.readExpitation = readExpiration;
        this.timesRenewed = timesRenew;
        callback.onRenew();
    }


    @Override
    public void onExpire() {
        callback.onExpire();
    }

    @Override
    public void onReadExpire() {
        callback.onReadExpire();
    }

    @Override
    public void onDelete() {
        callback.onDelete();
    }

    @Override
    public void onError(DappException exception) {
        callback.onError(exception);
    }
}
