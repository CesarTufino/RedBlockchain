package direcciones;

import java.util.ArrayList;
import java.util.List;

public enum Direccion {
    DIRECCION_1("26.20.111.124", 12341), // 26.20.111.124; 192.168.0.103; 192.168.100.73
    DIRECCION_2("26.182.121.49", 12342), // 26.37.38.157; 192.168.0.100; 26.194.104.185; 192.168.100.9
    DIRECCION_3("26.132.202.57", 12343), // 26.143.218.218; 192.168.0.101; 192.168.100.200
    DIRECCION_4("26.37.38.157", 12344),
    DIRECCION_GATEWAY("26.143.218.218", 12345); // 192.168.100.73

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

    public static List<Direccion> getNodos(){
        List<Direccion> listaDirecciones = new ArrayList<>();
        for (Direccion direccion : Direccion.values()){
            if (!direccion.equals(DIRECCION_GATEWAY)){
                listaDirecciones.add(direccion);
            }
        }
        return listaDirecciones;
    }

    public static int getPuertoPorDireccion(String direccionIP) {
        for (Direccion direccion : Direccion.values()){
            if (direccion.getDireccionIP().equals(direccionIP)){
                return direccion.getPuerto();
            }
        }
        return -1;
    }

}
