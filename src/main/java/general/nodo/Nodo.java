package general.nodo;

import general.constantes.Direccion;
import general.conexion.Salida;
import general.mensajes.Mensaje;
import general.utils.RsaUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * La clase Nodo representa los dispositivos finales que generan transaciones, crean bloques y almacenan una copia del
 * blockchain.
 */
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

    /**
     * Obtiene la dirección del nodo.
     * @return dirección del nodo.
     */
    public Direccion getDireccion() {
        return direccion;
    }

    /**
     * Recibe el mensaje y proceso su contenido.
     * @param mensaje mensaje recibido.
     */
    public abstract void recibirMensaje(Mensaje mensaje);

    /**
     * Envía la red de este nodo a otro nodo que envió su dirección para pedir la información actual de la red.
     * @param direccion dirección del nodo que pidió la información de la red.
     */
    public abstract void enviarInfoRed(Direccion direccion);

    /**
     * Obtiene la red.
     * @return red.
     */
    public abstract Red getRed();

    /**
     * Establece una instancia de la red.
     * @param red
     */
    public abstract void setRed(Red red);

}
