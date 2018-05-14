package edu.khai.csn.abondar.passwordmanager;

import java.security.MessageDigest;
import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by Alexey Bondar on 12-May-18.
 */
class Cryptography {

    /**
     * Encryption and decryption algorithm
     */
    private static final String ALG = "AES";
    /**
     * Salt for the encryption
     */
    private final String SALT;

    public Cryptography(String key){
        SALT = key;
    }

    public String encrypt(String data) throws Exception{
        SecretKeySpec key  = generateKey();
        Cipher c = Cipher.getInstance(ALG);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, android.util.Base64.DEFAULT);

        return  encryptedValue;
    }

    public String decrypt(String encryptedData) throws Exception{
        SecretKeySpec key  = generateKey();
        Cipher c = Cipher.getInstance(ALG);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);

        return decryptedValue;
    }

    public SecretKeySpec generateKey() throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = SALT.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec  = new SecretKeySpec(key, ALG);

        return secretKeySpec;
    }
}
