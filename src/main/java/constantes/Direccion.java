package constantes;

import java.util.ArrayList;
import java.util.List;

public enum Direccion {

    DIRECCION_1("26.20.111.124", 12341),
    DIRECCION_2("26.182.121.49", 12342),
    DIRECCION_3("26.132.202.57", 12343),
    DIRECCION_4("26.37.38.157", 12344),
    DIRECCION_GATEWAY("26.192.95.30", 12345);

/*
    DIRECCION_1("26.20.111.124", 12341),
    DIRECCION_2("26.182.121.49", 12342),
    DIRECCION_3("26.132.202.57", 12343),
    DIRECCION_4("192.168.100.73", 12344),
    DIRECCION_GATEWAY("26.20.111.124", 12345);
*/

    private String direccionIP;
    private int puerto;

    Direccion(String direccionIP, int puerto) {
        this.direccionIP = direccionIP;
        this.puerto = puerto;
    }

    public String getDireccionIP() {
        return direccionIP;
    }

    public int getPuerto() {
        return puerto;
    }

    public static List<Direccion> getNodos() {
        List<Direccion> listaDirecciones = new ArrayList<>();
        for (Direccion direccion : Direccion.values()) {
            if (!direccion.equals(DIRECCION_GATEWAY)) {
                listaDirecciones.add(direccion);
            }
        }
        return listaDirecciones;
    }

    public static int getPuertoPorDireccion(String direccionIP) {
        for (Direccion direccion : Direccion.values()) {
            if (direccion.getDireccionIP().equals(direccionIP)) {
                return direccion.getPuerto();
            }
        }
        return -1;
    }

}
