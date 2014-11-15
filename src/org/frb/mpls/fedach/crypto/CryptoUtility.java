package org.frb.mpls.fedach.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import com.ibm.misc.BASE64Decoder;
import com.ibm.misc.BASE64Encoder;

/**
 * Service to handle TripleDES (similar to TripleDESEncryptionServiceImpl)
 * 
 * @author i1mal00
 */
public class CryptoUtility {

    public final static String TRIPLE_DES = "DESede";

    private String transformation = "DESede/CBC/PKCS5Padding";
    // 24
    // bit
    private SecretKey key;

    public static enum ENCRYPTION_MODE {
        ENCRYPT, DECRYPT
    };

    private static CryptoUtility utility;

    private CryptoUtility() {

    }

    public static CryptoUtility getInstance() {
        if (utility == null) {
            utility = new CryptoUtility();
        }
        return utility;
    }

    private Cipher getCipher(ENCRYPTION_MODE mode, SecretKey key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(transformation);
//        Cipher cipher = Cipher.getInstance("DESede");
        IvParameterSpec iv = new IvParameterSpec(new byte[8]);

        switch(mode) {
            case ENCRYPT :
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            case DECRYPT :
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
        }
        return cipher;
    }

    private SecretKey getSecretKey(String keyPhrase) throws InvalidKeyException, InvalidKeySpecException,
            NoSuchAlgorithmException {
        DESedeKeySpec keySpec = new DESedeKeySpec(keyPhrase.getBytes());
        key = SecretKeyFactory.getInstance(TRIPLE_DES).generateSecret(keySpec);
        return key;
    }

    /*
     * StringBuffer saltifiedText = new StringBuffer(getSalt());
     * saltifiedText.append(SALT_DELIMITER).append(plainText); 
     * byte[] plain = saltifiedText.toString().getBytes("ASCII"); 
     * byte[] encrypted = cipher.doFinal(plain);
     */
    public String encrypt(String plainText, String keyPhrase) {
        Cipher cipher;
        try {
            String SALT_DELIMITER = ":";
            StringBuffer saltifiedText = new StringBuffer(keyPhrase);
            saltifiedText.append(SALT_DELIMITER).append(plainText);
            
            cipher = getCipher(ENCRYPTION_MODE.ENCRYPT, getSecretKey(keyPhrase));
            
            byte[] plain = saltifiedText.toString().getBytes("ASCII");
            
//            String padded = appendChars(8, plainText);
            byte[] encrypted = cipher.doFinal(plain);

            // Encode the string into bytes using utf-8
//            byte[] utf8 = padded.getBytes("UTF-8");

//            System.out.println("\nPadded Bytes length: " + utf8.length);

            // Encrypt
//            byte[] enc = cipher.doFinal(utf8);

            return new BASE64Encoder().encode(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new EncryptionException("Unable to encrypt " + plainText);
        }

    }

    private String appendChars(int multiple, String unpadded) throws Exception {
        StringBuffer sb = new StringBuffer(unpadded);

        int charsToAdd = multiple - (unpadded.length() % multiple);
        for (int i = 0; i < charsToAdd; i++) {
            sb.append("_");
        }

        if (sb.length() % multiple != 0) {
            throw new Exception("Problem padding this string");
        }
        return sb.toString();
    }

    public String decrypt(String stringToDecrypt, String keyPhrase) throws Exception {
        Cipher cipher;
        try {
            key = getSecretKey(keyPhrase);
            cipher = getCipher(ENCRYPTION_MODE.DECRYPT, key);
            byte[] decoded = new BASE64Decoder().decodeBuffer(stringToDecrypt);

            // Encrypt
            byte[] enc = cipher.doFinal(decoded);

            return new String(enc, "UTF8");

        } catch (Exception e) {
            e.printStackTrace();
            throw new EncryptionException("Unable to decrypt " + stringToDecrypt);
        }

    }

}
