package general.mensajes;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * Clase InfoNodo.
 */
public class InfoNodo implements Serializable{
    private String direccion;
    private PublicKey clavePublica;
    private int puerto;
    private double montoDeApuesta1;
    private double montoDeApuesta2;
    private long tiempoDeApuesta1;
    private long tiempoDeApuesta2;
    
    public InfoNodo(String direccion, PublicKey clavePublica, int puerto) {
        this.direccion = direccion;
        this.clavePublica = clavePublica;
        this.puerto = puerto;
    }

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

    public InfoNodo(String direccion, PublicKey clavePublica, int puerto,
                    double montoDeApuesta1, long tiempoDeApuesta1) {
        this.direccion = direccion;
        this.clavePublica = clavePublica;
        this.puerto = puerto;
        this.montoDeApuesta1 = montoDeApuesta1;
        this.tiempoDeApuesta1 = tiempoDeApuesta1;
    }

    public PublicKey getClavePublica() {
        return clavePublica;
    }

    public void setClavePublica(PublicKey publicKey) {
        this.clavePublica = publicKey;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }


    public double getMontoDeApuesta1() {
        return montoDeApuesta1;
    }

    public void setMontoDeApuesta1(double stakeAmount1) {
        this.montoDeApuesta1 = stakeAmount1;
    }

    public double getMontoDeApuesta2() {
        return montoDeApuesta2;
    }

    public void setMontoDeApuesta2(double stakeAmount2) {
        this.montoDeApuesta2 = stakeAmount2;
    }

    public long getTiempoDeApuesta1() {
        return tiempoDeApuesta1;
    }

    public void setTiempoDeApuesta1(long stakeTime1) {
        this.tiempoDeApuesta1 = stakeTime1;
    }

    public long getTiempoDeApuesta2() {
        return tiempoDeApuesta2;
    }

    public void setTiempoDeApuesta2(long stakeTime2) {
        this.tiempoDeApuesta2 = stakeTime2;
    }

}
