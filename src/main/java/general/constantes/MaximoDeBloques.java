package general.constantes;

public enum MaximoDeBloques {
    MAX(200);

    private int cantidad;

    MaximoDeBloques(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }
}
