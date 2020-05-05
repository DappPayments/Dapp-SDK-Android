package mx.dapp.sdk.core.enums;

/**
 * Created by carlos on 28/06/18.
 */

public enum DappPaymentType {
    BALANCE(0),
    CREDIT(1),
    DEBIT(2),
    CODI(5);

    int id;

    DappPaymentType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static DappPaymentType fromRawValue(int id) {
        switch (id) {
            case 0:
                return BALANCE;
            case 1:
                return CREDIT;
            case 2:
                return DEBIT;
            case 5:
                return CODI;
            default:
                return null;
        }
    }
}
