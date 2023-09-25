package general.mensajes;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * La clase InfoNodo se encarga de almacenar la información de un nodo en un solo objeto para su envío.
 */
public class InfoNodo implements Serializable{
    private String direccion;
    private PublicKey clavePublica;
    private int puerto;
    private double montoDeApuesta1;
    private double montoDeApuesta2;
    private long tiempoDeApuesta1;
    private long tiempoDeApuesta2;

    /**
     * Constructor de InfoNodo con la información básica.
     * @param direccion dirección del nodo.
     * @param clavePublica clave del nodo.
     * @param puerto puerto del nodo.
     */
    public InfoNodo(String direccion, PublicKey clavePublica, int puerto) {
        this.direccion = direccion;
        this.clavePublica = clavePublica;
        this.puerto = puerto;
    }

    /**
     * Constructor de InfoNodo con toda la información
     * @param direccion dirección del nodo.
     * @param clavePublica clave publica del nodo.
     * @param puerto puerto del nodo.
     * @param montoDeApuesta1 monto de apuesta del nodo para blockchain tipo 1.
     * @param montoDeApuesta2 monto de apuesta del nodo para blockchain tipo 2.
     * @param tiempoDeApuesta1 tiempo inicial de apuesta del nodo para blockchain tipo 1.
     * @param tiempoDeApuesta2 tiempo inicial de apuesta del nodo para blockchain tipo 2.
     */
    public InfoNodo(String direccion, PublicKey clavePublica, int puerto,
                    double montoDeApuesta1, double montoDeApuesta2,
                    long tiempoDeApuesta1, long tiempoDeApuesta2) {
        this.direccion = direccion;
        this.clavePublica = clavePublica;
        this.puerto = puerto;
        this.montoDeApuesta1 = montoDeApuesta1;
        this.montoDeApuesta2 = montoDeApuesta2;
        this.tiempoDeApuesta1 = tiempoDeApuesta1;
        this.tiempoDeApuesta2 = tiempoDeApuesta2;
    }

    /**
     * Constructor de InfoNodo con la información para blockchain tradicional con POS.
     * @param direccion dirección del nodo.
     * @param clavePublica clave publica del nodo.
     * @param puerto puerto del nodo.
     * @param montoDeApuesta1 monto de apuesta del nodo.
     * @param tiempoDeApuesta1 tiempo inicial de apuesta del nodo.
     */
    public InfoNodo(String direccion, PublicKey clavePublica, int puerto,
                    double montoDeApuesta1, long tiempoDeApuesta1) {
        this.direccion = direccion;
        this.clavePublica = clavePublica;
        this.puerto = puerto;
        this.montoDeApuesta1 = montoDeApuesta1;
        this.tiempoDeApuesta1 = tiempoDeApuesta1;
    }

    /**
     * Obtiene la clave publica del nodo.
     * @return clave publica del nodo.
     */
    public PublicKey getClavePublica() {
        return clavePublica;
    }

    /**
     * Obtiene la dirección IP del nodo.
     * @return dirección IP del nodo.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Obtiene el puerto por el cual el nodo esta recibiendo información.
     * @return puerto del nodo.
     */
    public int getPuerto() {
        return puerto;
    }

    /**
     * Obtiene el monto de apuesta del nodo para blockchain tipo 1.
     * @return monto de apuesta 1 del nodo.
     */
    public double getMontoDeApuesta1() {
        return montoDeApuesta1;
    }

    /**
     * Obtiene el monto de apuesta del nodo para blockchain tipo 2.
     * @return monto de apuesta 2 del nodo.
     */
    public double getMontoDeApuesta2() {
        return montoDeApuesta2;
    }

    /**
     * Obtiene el tiempo inicial de apuesta del nodo para blockchain tipo 1.
     * @return tiempo de apuesta 1 del nodo.
     */
    public long getTiempoDeApuesta1() {
        return tiempoDeApuesta1;
    }

    /**
     * Obtiene el tiempo inicial de apuesta del nodo para blockchain tipo 2.
     * @return tiempo de apuesta 2 del nodo.
     */
    public long getTiempoDeApuesta2() {
        return tiempoDeApuesta2;
    }

}
