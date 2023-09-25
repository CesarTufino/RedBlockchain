package general.utils;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * La clase RsaUtil contiene métodos para generar pares de claves (publicas y privadas) y generar y verificar firmas.
 */
public class RsaUtil {

    /**
     * Genera un par de claves de 512 bits.
     * @return par de claves de 512 bits.
     * @throws Exception si no se puede obtener una instancia de KeyPair.
     */
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(512);
        return generator.generateKeyPair();
    }

    /**
     * Genera una firma por clave privada.
     * @param plainText texto plano que necesita ser encriptado.
     * @param privateKey clave privada del emisor.
     * @return texto cifrado.
     * @throws Exception si no se puede obtener una instancia de Signature.
     */
    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(StandardCharsets.UTF_8));
        byte[] signature = privateSignature.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    /**
     * Verifica una firma por clave pública.
     * @param plainText texto plano que necesita ser validado.
     * @param signature texto cifrado perteneciente al texto plano.
     * @param publicKey vlave pública del emisor.
     * @return true si fue enviado por el propio remitente.
     * @throws Exception si no se puede obtener una instancia de Signature.
     */
    public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return publicSignature.verify(signatureBytes);
    }

}
