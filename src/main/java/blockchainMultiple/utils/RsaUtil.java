package blockchainMultiple.utils;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * Clase para generar pares de claves (publicas y privadas) con métodos para generar y verificar firmas.
 */
public class RsaUtil {

    /**
     * Método para generar un par de claves.
     *
     * @return Par de claves de 512 bits.
     * @throws Exception Excepción.
     */
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(512);
        return generator.generateKeyPair();
    }

    /**
     * Método de generación de firma por clave privada.
     *
     * @param plainText Texto plano que necesita ser encriptado.
     * @param privateKey La clave privada del emisor.
     * @return Texto cifrado.
     * @throws Exception Excepción.
     */
    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(StandardCharsets.UTF_8));
        byte[] signature = privateSignature.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    /**
     * Método de verificación de firma por clave pública.
     *
     * @param plainText Texto plano que necesita ser validado.
     * @param signature El texto cifrado perteneciente al texto plano.
     * @param publicKey La clave pública del emisor.
     * @return Si fue enviado por el propio remitente.
     * @throws Exception Excepción.
     */
    public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return publicSignature.verify(signatureBytes);
    }


}
