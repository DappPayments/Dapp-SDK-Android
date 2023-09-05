package mx.dapp.sdk.vendor.callbacks;

import java.util.List;

import mx.dapp.sdk.core.callbacks.DappCallback;
import mx.dapp.sdk.vendor.dto.DappCashIn;

public interface DappCashInsCallback extends DappCallback {
    void onSuccess(int totalCount, int numPages, int currentPage, boolean hasNextPage, List<DappCashIn> cashIns);
}
