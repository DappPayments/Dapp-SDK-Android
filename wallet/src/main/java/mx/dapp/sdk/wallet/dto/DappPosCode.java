package mx.dapp.sdk.wallet.dto;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import mx.dapp.sdk.core.dto.AbstractDappCode;
import mx.dapp.sdk.core.enums.DappPaymentResult;
import mx.dapp.sdk.core.exceptions.DappPaymentException;
import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.wallet.callbacks.DappCodeReadCallback;
import mx.dapp.sdk.wallet.enums.DappCodeResult;
import mx.dapp.sdk.wallet.enums.DappQRType;
import mx.dapp.sdk.wallet.exceptions.DappCodeException;
import mx.dapp.sdk.wallet.network.DappWalletApi;

import static mx.dapp.sdk.core.Dapp.DAPP_HOST;

public class DappPosCode extends AbstractDappCode implements Parcelable {

    public static final String DAPP_CALLBACK_URL_REQUEST = "DAPP_CALLBACK_URL_REQUEST";
    private static final String DAPP_PAYMENT_ID = "DAPP_PAYMENT_ID";

    private String code;
    private String urlCallback;
    private DappUser dappUser;
    private JSONObject jsonObject;

    public DappPosCode(String code) {
        this.code = code;
    }

    public DappPosCode(Intent intent) throws DappCodeException {
        if (intent.getData() != null) {
            Uri appLinkData = intent.getData();
            String linkReferencia = appLinkData.toString();
            if (linkReferencia.contains("mxdapp://")) {
                this.urlCallback = intent.getStringExtra(DAPP_CALLBACK_URL_REQUEST);
                this.code = linkReferencia.replace("mxdapp://", "https://");
            } else {
                throw new DappCodeException(DappCodeResult.RESULT_INVALID_INTENT_LINK_DATA);
            }
        } else {
            throw new DappCodeException(DappCodeResult.RESULT_INVALID_INTENT_DATA);
        }
    }

    public void read(DappCodeReadCallback callback) {
        DappQRType type = getQRType();
        if(type == DappQRType.CODI){
            callback.onError(new DappCodeException(DappCodeResult.RESULT_INVALID_QR_CODE));
        }else{
            String readCode = (type == DappQRType.CODI_DAPP || type == DappQRType.DAPP) ? "https://dapp.mx/c/" + getDappId() : code;
            doRead(Uri.encode(readCode), callback);
        }
    }

    private void doRead(String code, final DappCodeReadCallback callback){
        DappWalletApi dappWalletApi = new DappWalletApi();
        dappWalletApi.dappCode(code, new DappResponseProcess(callback) {
            @Override
            public void processSuccess(Object data) {
                jsonObject = (JSONObject)data;
                dappId = jsonObject.optString("id");
                amount = jsonObject.optDouble("amount");
                currency = jsonObject.optString("currency");
                description = jsonObject.optString("description");
                reference = jsonObject.optString("referencia");
                dappUser = new DappUser(jsonObject.optJSONObject("dapp_user"));
                callback.onSuccess();
            }
        });
    }

    public boolean isValidDappCode() {
        if (code != null && code.length() > 0) {
            String result = getDappId();
            if (result == null) {
                return false;
            }
            return result.length() > 0;
        }
        return false;
    }

    public String getDappId() {
        return code.contains(DAPP_HOST) ? getDappCode() : getCodiDappCode();
    }

    private String getDappCode() {
        try {
            return code.substring(code.lastIndexOf("/") + 1);
        } catch (Exception e) {
            return null;
        }
    }

    private String getCodiDappCode() {
        JSONObject json;
        try {
            json = new JSONObject(code);
        } catch (JSONException e) {
            return null;
        }
        return json.optString("dapp");
    }

    public DappQRType getQRType() {
        DappQRType type = DappQRType.UNKNOWN;
        if (code.contains(DAPP_HOST)) {
            type = DappQRType.DAPP;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(code);
                if (validateJsonCodi(jsonObject)) {
                    if (jsonObject.has("dapp")) {
                        type = DappQRType.CODI_DAPP;
                    } else {
                        type = DappQRType.CODI;
                    }
                } else {
                    if (jsonObject.has("dapp")) {
                        type = DappQRType.DAPP;
                    }
                }
            } catch (JSONException e) {
                return DappQRType.UNKNOWN;
            }
        }
        return type;
    }

    private boolean validateJsonCodi(JSONObject jsonObject) {
        boolean top = jsonObject.has("TYP") &&
                jsonObject.has("v") &&
                jsonObject.has("ic") &&
                jsonObject.has("CRY");
        if (top) {
            return validateJsonCodiV(jsonObject.optJSONObject("v")) &&
                    validateJsonCodiIC(jsonObject.optJSONObject("ic"));
        }
        return false;
    }

    private boolean validateJsonCodiV(JSONObject jsonObject) {
        return jsonObject != null && jsonObject.has("DEV");
    }

    private boolean validateJsonCodiIC(JSONObject jsonObject) {
        return jsonObject != null && jsonObject.has("IDC") &&
                jsonObject.has("SER") &&
                jsonObject.has("ENC");
    }

    public boolean isCodi() {
        try {
            JSONObject jsonObject = new JSONObject(code);
            return validateJsonCodi(jsonObject);
        } catch (JSONException e) {
            return false;
        }
    }

    public void returnPayment(String paymentId, Context context) throws DappPaymentException {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(urlCallback));
        i.putExtra(DAPP_PAYMENT_ID, paymentId);
        PackageManager packageManager = context.getPackageManager();
        if (i.resolveActivity(packageManager) != null) {
            context.startActivity(i);
        } else {
            throw new DappPaymentException(DappPaymentResult.RESULT_NO_ACTIVITY_TO_HANDLE);
        }
    }

    public String getCode() {
        return code;
    }

    public DappUser getDappUser() {
        return dappUser;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.urlCallback);
        dest.writeParcelable(this.dappUser, flags);
        dest.writeString(this.jsonObject.toString());
        dest.writeString(this.dappId);
        dest.writeValue(this.amount);
        dest.writeString(this.description);
        dest.writeString(this.reference);
    }

    protected DappPosCode(Parcel in) throws JSONException {
        this.code = in.readString();
        this.urlCallback = in.readString();
        this.dappUser = in.readParcelable(DappUser.class.getClassLoader());
        this.jsonObject = new JSONObject(in.readString());
        this.dappId = in.readString();
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.description = in.readString();
        this.reference = in.readString();
    }

    public static final Parcelable.Creator<DappPosCode> CREATOR = new Parcelable.Creator<DappPosCode>() {
        @Override
        public DappPosCode createFromParcel(Parcel source) {
            try {
                return new DappPosCode(source);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public DappPosCode[] newArray(int size) {
            return new DappPosCode[size];
        }
    };
}
