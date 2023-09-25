package multiple.mensajes;

import general.constantes.Tipo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * La clase Paquete contiene un grupo de transacciones y el tipo de blockchain lógico al que pertenecen.
 */
public class Paquete implements Serializable {
    private final Tipo tipo;
    private List<TransaccionMultiple> transacciones;

    public Paquete(Tipo tipo, List<TransaccionMultiple> transacciones) {
        this.tipo = tipo;
        this.transacciones = transacciones;
    }

    /**
     * Obtiene el tipo de blockchain al que pertenecen las transacciones del paquete.
     * @return tipo del paquete
     */
    public Tipo getTipo() {
        return tipo;
    }

    /**
     * Obtiene la lista de transacciones que contiene el paquete.
     * @return lista de transacciones.
     */
    public List<TransaccionMultiple> getTransacciones() {
        return transacciones;
    }

}
