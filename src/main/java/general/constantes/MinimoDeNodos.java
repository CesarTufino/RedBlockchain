package general.constantes;

public enum MinimoDeNodos {
    MIN_POS(3),
    MIN_GATEWAY_TRADICIONAL(3),
    MIN_GATEWAY_MULTIPLE(4);

    private int cantidad;

    MinimoDeNodos(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }
}
