package gatewayVersion.blockchainTradicional.mensajes;

import java.io.Serializable;
import java.security.PublicKey;

public class InfoNodo implements Serializable{
    private String direccion;
    private PublicKey clavePublica;
    private int puerto;
    
    public InfoNodo(String direccion, PublicKey clavePublica, int puerto) {
        this.direccion = direccion;
        this.clavePublica = clavePublica;
        this.puerto = puerto;
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
}
