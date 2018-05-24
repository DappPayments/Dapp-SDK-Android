package mx.dapp.sdk.dto;

/**
 * Created by carlos on 28/06/18.
 */

public enum EDappPaymentType {
    SALDO(0, "Saldo DAPP"),
    TARJETA_CREDITO(1, "CRÉDITO"),
    TARJETA_DEBITO(3, "DÉBITO");

    int id;
    String texto;

    EDappPaymentType(int id, String texto){
        this.id = id;
        this.texto = texto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public static EDappPaymentType fromRawValue(int id) {
        switch (id) {
            case 0:
                return SALDO;
            case 1:
                return TARJETA_CREDITO;
            case 2:
                return TARJETA_DEBITO;
            default:
                return SALDO;
        }
    }
}
