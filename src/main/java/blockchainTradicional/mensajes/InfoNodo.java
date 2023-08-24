package blockchainTradicional.mensajes;

import java.io.Serializable;
import java.security.PublicKey;

public class InfoNodo implements Serializable{
    private String direccion;
    private PublicKey clavePublica;
    private double montoDeApuesta;
    private long tiempoDeApuesta;
    
    public InfoNodo(String direccion, PublicKey clavePublica, double montoDeApuesta, long tiempoDeApuesta) {
        this.direccion = direccion;
        this.clavePublica = clavePublica;
        this.montoDeApuesta = montoDeApuesta;
        this.tiempoDeApuesta = tiempoDeApuesta;
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

    public double getMontoDeApuesta() {
        return montoDeApuesta;
    }

    public void setMontoDeApuesta(double stakeAmount1) {
        this.montoDeApuesta = stakeAmount1;
    }

    public long getTiempoDeApuesta() {
        return tiempoDeApuesta;
    }

    public void setTiempoDeApuesta(long stakeTime1) {
        this.tiempoDeApuesta = stakeTime1;
    }

}
