package mx.dapp.sdk;

/**
 * Created by carlos on 7/06/17.
 */

public class DappException extends Exception {

    private int codeError;

    public DappException(String message, int codeError){
        super(message);
        this.codeError = codeError;
    }

    public int getCodeError() {
        return codeError;
    }

    public void setCodeError(int codeError) {
        this.codeError = codeError;
    }
}
