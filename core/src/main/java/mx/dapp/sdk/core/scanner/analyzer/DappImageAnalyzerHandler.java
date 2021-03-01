package mx.dapp.sdk.core.scanner.analyzer;

public interface DappImageAnalyzerHandler {
    void onSuccess(String value);

    void onError(Exception exception);
}
