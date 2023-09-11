package gatewayVersion.blockchainMultipleAletorio.mensajes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Paquete implements Serializable {

    private final String tipo;

    private List<Transaccion> transacciones = new ArrayList<>();

    public Paquete(String tipo, List<Transaccion> transacciones) {
        this.tipo = tipo;
        this.transacciones = transacciones;
    }

    public String getTipo() {
        return tipo;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }
}
