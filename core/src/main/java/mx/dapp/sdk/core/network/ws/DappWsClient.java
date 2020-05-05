package mx.dapp.sdk.core.network.ws;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import mx.dapp.sdk.core.callbacks.DappSocketStatusCallback;
import mx.dapp.sdk.core.enums.DappResult;
import mx.dapp.sdk.core.exceptions.DappException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class DappWsClient extends WebSocketListener{

    private String url;
    private String header;
    private DappSocketStatusCallback callback;

    public DappWsClient(String url, String header, DappSocketStatusCallback callback) {
        this.url = url;
        this.header = header;
        this.callback = callback;
    }

    public WebSocket createSocket() {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        OkHttpClient client = clientBuilder.build();


        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Basic " + header)
                .build();
        return client.newWebSocket(request, this);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        processTextMessage(text);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        callback.onError(new DappException(t.getMessage(), DappResult.RESULT_RESPONSE_ERROR.getCode()));
    }

    private void processTextMessage(String text) {
        JSONObject response = null;
        try {
            response = new JSONObject(text);
            callback.onMessage(response);
        } catch (JSONException e) {
            callback.onError(new DappException(e.getMessage(), DappResult.RESULT_RESPONSE_ERROR.getCode()));
        }
    }
}
