package mx.dapp.sdk.wallet.handler;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mx.dapp.sdk.core.callbacks.DappSocketStatusCallback;
import mx.dapp.sdk.core.enums.DappResult;
import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.core.handler.AbstractStatusHandler;
import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.wallet.callbacks.DappRPCodeStatusCallback;
import mx.dapp.sdk.wallet.dto.DappPayment;
import mx.dapp.sdk.wallet.dto.DappRPCode;
import mx.dapp.sdk.wallet.network.DappWalletApi;

public class RPCodeStatusHandler extends AbstractStatusHandler {

    private DappRPCode dappRPCode;
    private DappRPCodeStatusCallback callback;
    private Handler expirationTimeHandler;
    private Runnable expirationTimerRunnable;
    private Handler readeExpirationTimeHandler;
    private Runnable readExpirationTimerRunnable;

    public RPCodeStatusHandler(DappRPCode dappRPCode, DappRPCodeStatusCallback callback) {
        super(dappRPCode.getId());
        this.dappRPCode = dappRPCode;
        this.callback = callback;
    }

    @Override
    public void startRequest() {
        setExpirationTimer();
        setReadExpirationTimer();
        isListening = true;
        DappWalletApi dappWalletApi = new DappWalletApi();

        ws = dappWalletApi.getPaymentCodeSocket(code, new DappSocketStatusCallback() {
            @Override
            public void onMessage(JSONObject data) {
                processTextMessage(data);
            }

            @Override
            public void onError(DappException exception) {
                if (isListening) {
                    getStatusBySchedule();
                }
            }
        });
    }

    private void getStatusBySchedule() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                getStatusBySchedule();
            }
        };

        DappWalletApi dappWalletApi = new DappWalletApi();
        dappWalletApi.paymentStatus(code, new DappResponseProcess(callback) {
            @Override
            public void processSuccess(Object data) {
                if (data == null) {
                    if (timerHandler == null) {
                        timerHandler = new Handler();
                    }
                    timerHandler.postDelayed(timerRunnable, AUTOMATIC_SECONDS * 1000);
                } else {
                    removeExpirationTimer();
                    removeReadExpirationTimer();
                    DappPayment payment = new DappPayment((JSONObject)data);
                    callback.onPay(payment);
                }
            }

            @Override
            public void processFailed(Exception e) {
                if (scheduleErrorCount == 2) {
                    super.processFailed(e);
                    stopRequest();
                    removeExpirationTimer();
                    removeReadExpirationTimer();
                } else {
                    scheduleErrorCount += 1;
                }
            }
        });
    }

    public void renew() {
        if (isListening) {
            socketSendAction(EAction.RENEW);
        } else {
            renewByApi();
        }
    }

    private void renewByApi() {
        DappWalletApi dappWalletApi = new DappWalletApi();
        dappWalletApi.renewPaymentCode(code, new DappResponseProcess(callback) {
            @Override
            public void processSuccess(Object data) {
                if (data == null) {
                    callback.onError(new DappException(DappResult.RESULT_RESPONSE_ERROR));
                } else {
                    try {
                        getRenewedCodeInfo((JSONObject)data);
                    } catch (Exception e) {
                        callback.onError(new DappException(DappResult.RESULT_DATE_PARSE_ERROR));
                    }
                }
            }
        });
    }

    public void delete() {
        if (isListening) {
            socketSendAction(EAction.DELETE);
        } else {
            deleteByApi();
        }
        removeReadExpirationTimer();
        removeExpirationTimer();
    }

    private void socketSendAction(EAction action) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", action.getAction());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ws.send(jsonObject.toString());
    }

    private void deleteByApi() {
        DappWalletApi dappWalletApi = new DappWalletApi();
        dappWalletApi.deletePaymentCode(code, new DappResponseProcess(callback) {
            @Override
            public void processSuccess(Object data) {
                if (!isListening) {
                    callback.onDelete();
                }
            }
        });
    }

    private void processTextMessage(JSONObject data) {
        if (data.isNull("rc")) {
            callback.onError(new DappException(DappResult.RESULT_RESPONSE_ERROR));
        } else {
            int rc = data.optInt("rc");
            if (rc == 0) {
                callback.onPay(new DappPayment(data.optJSONObject("data")));
                removeExpirationTimer();
                removeReadExpirationTimer();
                stopRequest();
            } else if (rc == 1) {
                try {
                    getRenewedCodeInfo(data.optJSONObject("data"));
                } catch (Exception e) {
                    callback.onError(new DappException(DappResult.RESULT_DATE_PARSE_ERROR));
                }
            } else if (rc == 20) {
                callback.onDelete();
                stopRequest();
            } else {
                callback.onError(new DappException(DappResult.RESULT_RESPONSE_ERROR));
            }
        }
    }

    private void getRenewedCodeInfo(JSONObject data) throws ParseException {
        String qrCode = data.optString("qr_code");
        int impresionNum = data.optInt("impresion_num");
        Date readExpirationDate = paserDateString(data.optString("read_expiration"));
        Date renewExpirationDate = paserDateString(data.optString("renew_expiration"));
        callback.onRenew(qrCode, readExpirationDate, renewExpirationDate, impresionNum);
        setExpirationTimer();
        if (impresionNum < dappRPCode.MAX_TIMES_RENEWED) {
            setReadExpirationTimer();
        }else{
            removeReadExpirationTimer();
        }
    }


    private enum EAction {
        RENEW("renew"),
        DELETE("delete");

        private String action;

        EAction(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }

    private Date paserDateString(String strDate) throws ParseException {
        SimpleDateFormat sdf;
        Locale locale = Locale.US;
        if (strDate.endsWith("Z")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", locale);
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", locale);
        }
        return sdf.parse(strDate);
    }

    private void setExpirationTimer() {
        final long TIME_IN_MILLIS = dappRPCode.getRenewExpiration().getTime() - System.currentTimeMillis();
        if (TIME_IN_MILLIS < 0) {
            stopRequest();
            callback.onExpire();
        } else {
            if (expirationTimeHandler == null) {
                expirationTimeHandler = new Handler();
            } else {
                expirationTimeHandler.removeCallbacks(expirationTimerRunnable);
            }
            expirationTimerRunnable = new Runnable() {
                @Override
                public void run() {
                    stopRequest();
                    callback.onExpire();
                }
            };
            expirationTimeHandler.postDelayed(expirationTimerRunnable, TIME_IN_MILLIS);
        }

    }

    private void removeExpirationTimer() {
        expirationTimeHandler.removeCallbacks(expirationTimerRunnable);
    }

    private void setReadExpirationTimer() {
        final long TIME_IN_MILLIS = dappRPCode.getReadExpitation().getTime() - System.currentTimeMillis();
        if (TIME_IN_MILLIS < 0) {
            callback.onReadExpire();
        } else {
            if (readeExpirationTimeHandler == null) {
                readeExpirationTimeHandler = new Handler();
            } else {
                readeExpirationTimeHandler.removeCallbacks(readExpirationTimerRunnable);
            }
            readExpirationTimerRunnable = new Runnable() {
                @Override
                public void run() {
                    callback.onReadExpire();
                }
            };
            readeExpirationTimeHandler.postDelayed(readExpirationTimerRunnable, TIME_IN_MILLIS);
        }

    }

    private void removeReadExpirationTimer() {
        readeExpirationTimeHandler.removeCallbacks(readExpirationTimerRunnable);
    }
}
