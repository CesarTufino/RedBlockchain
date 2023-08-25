package direcciones;

public enum Direccion {
    DIRECCION1("26.20.111.124"), // 26.20.111.124 192.168.0.103
    DIRECCION2("26.37.38.157"), // 26.37.38.157 192.168.0.100
    DIRECCION3("26.143.218.218"); // 26.143.218.218 192.168.0.101

    private String direccionIP;

    Direccion(String direccionIP) {
        this.direccionIP = direccionIP;
    }

    public String getDireccionIP() {
        return direccionIP;
    }
}
