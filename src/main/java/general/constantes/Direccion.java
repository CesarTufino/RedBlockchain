package general.constantes;

import java.util.ArrayList;
import java.util.List;

/**
 * La enumeración Direccion contiene las direcciones IP y los puertos que pueden emplear los nodos y el gateway.
 */
public enum Direccion {
    DIRECCION_1("26.20.111.124", 12341),
    DIRECCION_2("26.182.121.49", 12342),
    DIRECCION_3("26.132.202.57", 12343),
    DIRECCION_4("26.192.95.30", 12344),
    DIRECCION_GATEWAY("26.37.38.157", 12345);

    private String direccionIP;
    private int puerto;

    Direccion(String direccionIP, int puerto) {
        this.direccionIP = direccionIP;
        this.puerto = puerto;
    }

    /**
     * Retorna la dirección IP de esta dirección.
     * @return la dirección IP de esta dirección.
     */
    public String getDireccionIP() {
        return direccionIP;
    }

    /**
     * Retorna el puerto de esta dirección.
     * @return el puerto de esta dirección.
     */
    public int getPuerto() {
        return puerto;
    }

    /**
     * Obtiene una lista de las direcciones que se utilizan para los nodos, por lo cual no se incluye la dirección del
     * gateway.
     * @return lista de direcciones de los nodos.
     */
    public static List<Direccion> getNodos() {
        List<Direccion> listaDirecciones = new ArrayList<>();
        for (Direccion direccion : Direccion.values()) {
            if (!direccion.equals(DIRECCION_GATEWAY)) {
                listaDirecciones.add(direccion);
            }
        }
        return listaDirecciones;
    }

}
