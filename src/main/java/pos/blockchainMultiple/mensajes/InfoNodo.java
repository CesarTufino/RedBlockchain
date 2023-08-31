package pos.blockchainMultiple.mensajes;

import java.io.Serializable;
import java.security.PublicKey;

public class InfoNodo implements Serializable{
    private String direccion;
    private PublicKey clavePublica;
    private double montoDeApuesta1;
    private double montoDeApuesta2;
    private long tiempoDeApuesta1;
    private long tiempoDeApuesta2;
    
    public InfoNodo(String direccion, PublicKey clavePublica, double montoDeApuesta1, double montoDeApuesta2, long tiempoDeApuesta1, long tiempoDeApuesta2) {
        this.direccion = direccion;
        this.clavePublica = clavePublica;
        this.montoDeApuesta1 = montoDeApuesta1;
        this.montoDeApuesta2 = montoDeApuesta2;
        this.tiempoDeApuesta1 = tiempoDeApuesta1;
        this.tiempoDeApuesta2 = tiempoDeApuesta2;
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
