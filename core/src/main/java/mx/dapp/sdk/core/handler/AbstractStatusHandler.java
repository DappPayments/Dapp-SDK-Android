package mx.dapp.sdk.core.handler;

import android.os.Handler;

import okhttp3.WebSocket;

public abstract class AbstractStatusHandler {
    protected String code;

    protected WebSocket ws = null;
    protected boolean isListening = false;
    protected static final int NORMAL_CLOSURE_STATUS = 1000;

    protected final int AUTOMATIC_SECONDS = 5;
    protected Handler timerHandler;
    protected Runnable timerRunnable;
    protected int scheduleErrorCount = 0;

    protected AbstractStatusHandler(String code) {
        this.code = code;
    }

    protected abstract void startRequest();

    public void stopRequest() {
        if (ws != null && isListening) {
            ws.close(NORMAL_CLOSURE_STATUS, null);
        } else if (timerHandler != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
        isListening = false;
    }
}
