package multiple.mensajes;

import general.constantes.Tipo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Paquete implements Serializable {

    private final Tipo tipo;

    private List<TransaccionMultiple> transacciones = new ArrayList<>();

    public Paquete(Tipo tipo, List<TransaccionMultiple> transacciones) {
        this.tipo = tipo;
        this.transacciones = transacciones;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public List<TransaccionMultiple> getTransacciones() {
        return transacciones;
    }
}
