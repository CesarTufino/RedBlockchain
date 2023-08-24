package direcciones;

public enum Direccion {
    DIRECCION1("192.168.0.103"), // 26.20.111.124
    DIRECCION2("192.168.0.100"), // 26.37.38.157
    DIRECCION3("192.168.0.101"); // 26.143.218.218

    private String direccionIP;

    Direccion(String direccionIP) {
        this.direccionIP = direccionIP;
    }

    public String getDireccionIP() {
        return direccionIP;
    }
}
