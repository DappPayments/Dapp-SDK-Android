package mx.dapp.sdk.customer.dto;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import mx.dapp.sdk.R;
import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.core.callbacks.DappPosCodeCallback;
import mx.dapp.sdk.core.dto.AbstractDappPosCode;
import mx.dapp.sdk.core.enums.DappPaymentResult;
import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.core.exceptions.DappPaymentException;

import static mx.dapp.sdk.core.Dapp.DAPP_CALLBACK_URL_REQUEST;

public class DappPosCode extends AbstractDappPosCode implements DappPosCodeCallback, Parcelable {

    public static final String DAPP_PAYMENT_ID = "DAPP_PAYMENT_ID";

    private Context context;
    private DappCallback userCallback;

    public DappPosCode(String dappId) {
        super(dappId);
    }

    public DappPosCode(Double amount, String description, String reference) {
        super(amount, description, reference);
    }

    public void pay(Context context, DappCallback userCallback) {
        super.create(this);
        this.context = context;
        this.userCallback = userCallback;
    }

    @Override
    public void onSuccess() {
        try {
            String url = "mxdapp://dapp.mx/c/" + getDappId();
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            String host = context.getString(R.string.dapp_callback_host);
            String pathPrefix = context.getString(R.string.dapp_callback_path_prefix);
            String scheme = context.getString(R.string.dapp_callback_scheme);
            String wallet = context.getString(R.string.dapp_wallet_scheme);
            String urlCallback = wallet.isEmpty() ? scheme + "://" + host + pathPrefix : wallet;
            i.putExtra(DAPP_CALLBACK_URL_REQUEST, urlCallback);
            PackageManager packageManager = context.getPackageManager();
            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i);
            } else {
                DappPaymentException ex = new DappPaymentException(DappPaymentResult.RESULT_NO_ACTIVITY_TO_HANDLE);
                userCallback.onError(ex);
            }
        } catch (Exception e) {
            userCallback.onError(new DappException(e.getMessage(), e.hashCode()));
        }
    }

    @Override
    public void onError(DappException exception) {
        userCallback.onError(exception);
    }

    public static String getDappPaymentId(Intent intent, Context context) throws Exception {
        String host = context.getString(R.string.dapp_callback_host);
        String pathPrefix = context.getString(R.string.dapp_callback_path_prefix);
        String scheme = context.getString(R.string.dapp_callback_scheme);
        String urlCallback = scheme + "://" + host + pathPrefix;
        if (intent.getData() != null) {
            Uri appLinkData = intent.getData();
            String linkReferencia = appLinkData.toString();
            if (linkReferencia.contains(urlCallback)) {
                return intent.getExtras().getString(DAPP_PAYMENT_ID, null);
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.qrText);
        dest.writeString(this.urlImage);
        dest.writeInt(this.expirationMinutes);
        dest.writeString(this.dappId);
        dest.writeValue(this.amount);
        dest.writeString(this.currency);
        dest.writeString(this.description);
        dest.writeString(this.reference);
    }

    public void readFromParcel(Parcel source) {
        this.qrText = source.readString();
        this.urlImage = source.readString();
        this.expirationMinutes = source.readInt();
        this.dappId = source.readString();
        this.amount = (Double) source.readValue(Double.class.getClassLoader());
        this.currency = source.readString();
        this.description = source.readString();
        this.reference = source.readString();
    }

    protected DappPosCode(Parcel in) {
        this.qrText = in.readString();
        this.urlImage = in.readString();
        this.expirationMinutes = in.readInt();
        this.dappId = in.readString();
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.currency = in.readString();
        this.description = in.readString();
        this.reference = in.readString();
    }

    public static final Parcelable.Creator<DappPosCode> CREATOR = new Parcelable.Creator<DappPosCode>() {
        @Override
        public DappPosCode createFromParcel(Parcel source) {
            return new DappPosCode(source);
        }

        @Override
        public DappPosCode[] newArray(int size) {
            return new DappPosCode[size];
        }
    };
}
