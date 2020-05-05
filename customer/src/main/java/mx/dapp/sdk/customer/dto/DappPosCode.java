package mx.dapp.sdk.customer.dto;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import mx.dapp.sdk.R;
import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.core.callbacks.DappPosCodeCallback;
import mx.dapp.sdk.core.dto.AbstractDappPosCode;
import mx.dapp.sdk.core.enums.DappPaymentResult;
import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.core.exceptions.DappPaymentException;

import static mx.dapp.sdk.core.Dapp.DAPP_CALLBACK_URL_REQUEST;

public class DappPosCode extends AbstractDappPosCode implements DappPosCodeCallback {

    public static final String DAPP_PAYMENT_ID = "DAPP_PAYMENT_ID";

    private Context context;
    private DappCallback userCallback;

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
            String urlCallback = scheme + "://" + host + pathPrefix;
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
}
