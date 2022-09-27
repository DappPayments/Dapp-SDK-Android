package mx.dapp.sdk.vendor.dto;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.core.callbacks.DappPosCodeCallback;
import mx.dapp.sdk.core.dto.AbstractDappPosCode;
import mx.dapp.sdk.core.enums.DappResult;
import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.core.network.http.DappResponseProcess;
import mx.dapp.sdk.vendor.callbacks.DappCodePoSImageCallback;
import mx.dapp.sdk.vendor.callbacks.DappCodePosPushNotificationCallback;
import mx.dapp.sdk.vendor.callbacks.DappCodePushNotificationDestination;
import mx.dapp.sdk.vendor.callbacks.DappCodesWalletsCallback;
import mx.dapp.sdk.vendor.callbacks.DappPaymentCallback;
import mx.dapp.sdk.vendor.handler.PoSCodeHandler;
import mx.dapp.sdk.vendor.network.DappVendorApi;

public class DappPosCode extends AbstractDappPosCode implements DappPosCodeCallback, Parcelable {

    private int height;
    private int width;
    private DappCodePoSImageCallback dappCodePoSImageCallback;
    private DappPosCodeCallback dappCodePoSCallback;
    private PoSCodeHandler poSCodeHandler;
    private DappWallet wallet;

    public DappPosCode(Double amount, String description, @Nullable String reference, @Nullable DappWallet wallet) {
        this(amount, 0.0, description, reference, wallet, -1);
    }

    public DappPosCode(Double amount, Double tip, String description, @Nullable String reference, @Nullable DappWallet wallet) {
        this(amount, tip, description, reference, wallet, -1);
    }

    public DappPosCode(Double amount, String description, @Nullable String reference, @Nullable DappWallet wallet, int expirationMinutes) {
        this(amount, 0.0, description, reference, wallet, expirationMinutes);
    }

    public DappPosCode(Double amount, Double tip, String description, @Nullable String reference, @Nullable DappWallet wallet, int expirationMinutes) {
        super(amount, tip, description, reference, expirationMinutes);
        this.wallet = wallet;
    }

    public void create(DappPosCodeCallback callback){
        create(null, null, callback);
    }

    public void create(String pos, String pin, DappPosCodeCallback callback){
        dappCodePoSCallback = callback;
        create(getQrSource(), pos, pin, this);
    }

    public void createWithImage(int height, int width, final DappCodePoSImageCallback callback) {
        createWithImage(height, width, null, null, callback);
    }

    public void createWithImage(int height, int width, String pos, String pin, final DappCodePoSImageCallback callback) {
        this.height = height;
        this.width = width;
        this.dappCodePoSImageCallback = callback;
        create(getQrSource(), pos, pin, this);
    }

    @Override
    public void onSuccess() {
        if (dappCodePoSCallback != null) {
            dappCodePoSCallback.onSuccess();
        }

        if (dappCodePoSImageCallback != null) {
            try {
                Bitmap bitmap = generateQRBitmap();
                dappCodePoSImageCallback.onSuccess(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
                DappException exception = new DappException(e.getMessage(), e.hashCode());
                dappCodePoSImageCallback.onError(exception);
            }
        }
    }

    @Override
    public void onError(DappException exception) {
        if (dappCodePoSCallback != null) {
            dappCodePoSCallback.onError(exception);
        }

        if (dappCodePoSImageCallback != null) {
            dappCodePoSImageCallback.onError(exception);
        }
    }

    public Bitmap generateQRBitmap(int desiredWidth, int desiredHeight) throws WriterException {
        EnumMap<EncodeHintType, Object> hint = new EnumMap<>(EncodeHintType.class);
        hint.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hint.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = new QRCodeWriter().encode(qrText, com.google.zxing.BarcodeFormat.QR_CODE,
                desiredWidth, desiredHeight, hint);

        int bmWidth = bitMatrix.getWidth();
        int bmHeight = bitMatrix.getHeight();
        int[] pixels = new int[bmWidth * bmHeight];
        for (int y = 0; y < bmHeight; y++) {
            int offset = y * bmWidth;
            for (int x = 0; x < bmWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bmWidth, bmHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, bmWidth, 0, 0, bmWidth, bmHeight);
        return bitmap;
    }

    private Bitmap generateQRBitmap() throws WriterException {
        return generateQRBitmap(width, height);
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

    public void sendPushNotification(String phoneNumber, final DappCodePosPushNotificationCallback callback) {
        if (wallet != null) {
            if (wallet.isPushNotification()) {
                if (wallet != null && wallet.isPushNotification() && dappId != null && isValidPhoneNumber(phoneNumber)) {
                    DappVendorApi api = new DappVendorApi();
                    api.dappCodePush(dappId, phoneNumber, wallet.id, new DappResponseProcess(callback) {
                        @Override
                        public void processSuccess(Object data) {
                            callback.onSuccess();
                        }
                    });
                } else {
                    callback.onError(new DappException(DappResult.RESULT_INVALID_DATA));
                }
            } else {
                callback.onError(new DappException(DappResult.RESULT_INVALID_WALLET_PUSH));
            }
        } else {
            callback.onError(new DappException(DappResult.RESULT_INVALID_WALLET));
        }
    }

    private boolean isValidPhoneNumber(@NonNull String phoneNumber) {
        Pattern regex = Pattern.compile("^[0-9]{10}$");
        Matcher mat = regex.matcher(phoneNumber);
        return mat.matches();
    }

    public static void getWallets(final DappCodesWalletsCallback callback) {
        DappVendorApi api = new DappVendorApi();
        api.dappCodesWallets(new DappResponseProcess(callback) {
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

    private int getQrSource(){
        return wallet != null ? wallet.getQrSource() : -1;
    }

    public DappWallet getWallet() {
        return wallet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.height);
        dest.writeInt(this.width);
        dest.writeParcelable(this.wallet, flags);
        dest.writeString(this.qrText);
        dest.writeValue(this.tip);
        dest.writeString(this.urlImage);
        dest.writeInt(this.expirationMinutes);
        dest.writeString(this.dappId);
        dest.writeValue(this.amount);
        dest.writeString(this.currency);
        dest.writeString(this.description);
        dest.writeString(this.reference);
    }

    public void readFromParcel(Parcel source) {
        this.height = source.readInt();
        this.width = source.readInt();
        this.wallet = source.readParcelable(DappWallet.class.getClassLoader());
        this.qrText = source.readString();
        this.tip = (Double) source.readValue(Double.class.getClassLoader());
        this.urlImage = source.readString();
        this.expirationMinutes = source.readInt();
        this.dappId = source.readString();
        this.amount = (Double) source.readValue(Double.class.getClassLoader());
        this.currency = source.readString();
        this.description = source.readString();
        this.reference = source.readString();
    }

    protected DappPosCode(Parcel in) {
        this.height = in.readInt();
        this.width = in.readInt();
        this.wallet = in.readParcelable(DappWallet.class.getClassLoader());
        this.qrText = in.readString();
        this.tip = (Double) in.readValue(Double.class.getClassLoader());
        this.urlImage = in.readString();
        this.expirationMinutes = in.readInt();
        this.dappId = in.readString();
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.currency = in.readString();
        this.description = in.readString();
        this.reference = in.readString();
    }

    public static final Creator<DappPosCode> CREATOR = new Creator<DappPosCode>() {
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
