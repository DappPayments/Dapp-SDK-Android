package mx.dapp.sdk.vendor.dto;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.core.callbacks.DappPaymentCallback;
import mx.dapp.sdk.core.callbacks.DappPosCodeCallback;
import mx.dapp.sdk.core.dto.AbstractDappPosCode;
import mx.dapp.sdk.core.enums.DappResult;
import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.vendor.callbacks.DappCodePoSImageCallback;
import mx.dapp.sdk.vendor.callbacks.DappCodePosPushNotificationCallback;
import mx.dapp.sdk.vendor.callbacks.DappCodePushNotificationDestination;
import mx.dapp.sdk.vendor.handler.PoSCodeHandler;
import mx.dapp.sdk.vendor.network.DappVendorApi;

public class DappPosCode extends AbstractDappPosCode implements DappPosCodeCallback {

    private int heigth;
    private int width;
    private DappCodePoSImageCallback dappCodePoSImageCallback;
    private PoSCodeHandler poSCodeHandler;

    public DappPosCode(Double amount, String description, String reference) {
        super(amount, description, reference);
    }

    public DappPosCode(Double amount, String description, String reference, int expirationMinutes) {
        super(amount, description, reference, expirationMinutes);
    }

    public void createWithImage(int height, int width, final DappCodePoSImageCallback callback) {
        this.heigth = height;
        this.width = width;
        this.dappCodePoSImageCallback = callback;
        create(this);
    }

    @Override
    public void onSuccess() {
        try {
            Bitmap bitmap = generateQRBitmap();
            dappCodePoSImageCallback.onSuccess(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            DappException exception = new DappException(e.getMessage(), e.hashCode());
            ((DappCallback) dappCodePoSImageCallback).onError(exception);
        }
    }

    @Override
    public void onError(DappException exception) {
        ((DappCallback) dappCodePoSImageCallback).onError(exception);
    }

    private Bitmap generateQRBitmap() throws WriterException {
        EnumMap<EncodeHintType, Object> hint = new EnumMap<>(EncodeHintType.class);
        hint.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hint.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = new QRCodeWriter().encode(qrText, com.google.zxing.BarcodeFormat.QR_CODE, width, heigth, hint);

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public void listen(DappPaymentCallback callback) {
        poSCodeHandler = new PoSCodeHandler(dappId, callback);
        poSCodeHandler.startRequest();
    }

    public void stopListening() {
        poSCodeHandler.stopRequest();
    }

    public void sendCodiPushNotification(String phoneNumber, final DappCodePosPushNotificationCallback callback) {
        if (dappId != null && isValidPhoneNumber(phoneNumber)) {
            DappVendorApi api = new DappVendorApi();
            api.sendPushNotification(dappId, phoneNumber, new DappResponseProcess(callback) {
                @Override
                public void processSuccess(Object data) {
                    callback.onSuccess();
                }
            });
        } else {
            callback.onError(new DappException(DappResult.RESULT_INVALID_DATA));
        }
    }

    public void sendPushNotification(String phoneNumber, DappWallet destination, final DappCodePosPushNotificationCallback callback) {
        if (dappId != null && isValidPhoneNumber(phoneNumber) && destination != null) {
            DappVendorApi api = new DappVendorApi();
            api.dappCodePush(dappId, phoneNumber, destination.id, new DappResponseProcess(callback) {
                @Override
                public void processSuccess(Object data) {
                    callback.onSuccess();
                }
            });
        } else {
            callback.onError(new DappException(DappResult.RESULT_INVALID_DATA));
        }
    }

    public static void getPushNotificationDestinations(final DappCodePushNotificationDestination callback) {
        DappVendorApi api = new DappVendorApi();
        api.dappCodePushDestinations(new DappResponseProcess(callback) {
            @Override
            public void processSuccess(Object data) {
                JSONArray destinations = (JSONArray) data;
                List<DappWallet> result = new ArrayList<>();
                for (int i = 0; i < destinations.length(); i++) {
                    result.add(new DappWallet(destinations.optJSONObject(i)));
                }
                callback.onSuccess(result);
            }
        });
    }

    private boolean isValidPhoneNumber(@NonNull String phoneNumber) {
        Pattern regex = Pattern.compile("^[0-9]{10}$");
        Matcher mat = regex.matcher(phoneNumber);
        return mat.matches();
    }

}
