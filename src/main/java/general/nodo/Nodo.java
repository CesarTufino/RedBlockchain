package general.nodo;

import general.constantes.Direccion;
import general.conexion.Salida;
import general.mensajes.Mensaje;
import general.utils.RsaUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class Nodo {

    protected Salida salida;
    protected PublicKey clavePublica;
    protected PrivateKey clavePrivada;
    protected Direccion direccion;
    protected int id;
    protected final double TARIFA_TRANSACCION = 0.1;
    protected final int DINERO_INICIAL = 100000000;
    protected double billetera1;

    public Nodo(int id, Direccion direccion) {
        KeyPair keys;
        try {
            keys = RsaUtil.generateKeyPair();
            this.clavePublica = keys.getPublic();
            this.clavePrivada = keys.getPrivate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.id = id;
        this.direccion = direccion;
        this.billetera1 = DINERO_INICIAL;
        this.salida = new Salida();
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public abstract void recibirMensaje(Mensaje mensaje);

    public abstract void enviarInfoRed(Direccion direccion);

    public abstract Red getRed();

    public abstract void setRed(Red red);

}
