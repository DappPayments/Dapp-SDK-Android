package mx.dapp.sdk.core.dto;

public abstract class AbstractDappCode {
    protected String dappId;
    protected Double amount;
    protected String currency;
    protected String description;
    protected String reference;

    public String getDappId() {
        return dappId;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return  currency;
    }

    public String getDescription() {
        return description;
    }

    public String getReference() {
        return reference;
    }
}
