package mx.dapp.sdk.vendor.dto;

import android.graphics.Bitmap;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.EnumMap;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.core.callbacks.DappPaymentCallback;
import mx.dapp.sdk.core.callbacks.DappPosCodeCallback;
import mx.dapp.sdk.core.dto.AbstractDappPosCode;
import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.vendor.callbacks.DappCodePoSImageCallback;
import mx.dapp.sdk.vendor.handler.PoSCodeHandler;

public class DappPosCode extends AbstractDappPosCode implements DappPosCodeCallback {

    private int heigth;
    private int width;
    private DappCodePoSImageCallback dappCodePoSImageCallback;
    private PoSCodeHandler poSCodeHandler;

    public DappPosCode(Double amount, String description, String reference) {
        super(amount, description, reference);
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
        EnumMap<EncodeHintType, Object> hint = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hint.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hint.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = new QRCodeWriter().encode("https://dapp.mx/c/" + dappId, com.google.zxing.BarcodeFormat.QR_CODE, width, heigth, hint);

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

}
