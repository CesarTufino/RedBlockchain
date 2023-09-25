package general.constantes;

/**
 * La enumeración MinimoDeNodos contiene la cantidad de nodos mínimos que se utilizan en los test para el inicio
 * automático.
 */
public enum MinimoDeNodos {
    /**
     * Cantidad de nodos mínimos para las versiones: V1, V2, V3 y V4.
     */
    MIN_POS(3),
    /**
     * Cantidad de nodos mínimos para las versiones: V5.
     */
    MIN_GATEWAY_TRADICIONAL(3),
    /**
     * Cantidad de nodos mínimos para las versiones: V6, V7 y V8.
     */
    MIN_GATEWAY_MULTIPLE(4);

    private int cantidad;

    MinimoDeNodos(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Retorna la cantidad de este mínimo de nodos.
     * @return la cantidad de este mínimo de nodos.
     */
    public int getCantidad() {
        return cantidad;
    }

}
