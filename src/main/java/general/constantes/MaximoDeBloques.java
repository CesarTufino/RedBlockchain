package general.constantes;

/**
 * La enumeración MaximoDeBloques contiene el número máximo de bloques que se crean en la ejecución de los tests.
 */
public enum MaximoDeBloques {
    /**
     * Número máximo de bloques que se crean en la ejecución de los tests.
     */
    MAX(200);

    private int cantidad;

    MaximoDeBloques(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Retorna la cantidad de bloques máxima.
     * @return cantidad de bloques máxima.
     */
    public int getCantidad() {
        return cantidad;
    }

}
