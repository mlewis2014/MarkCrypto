package org.markl.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class SecureFileUtility {

    private final String algorithm;
    private final String TripleDES = "DESede";
    private final String keySequence;
    

    public SecureFileUtility() {
        this.algorithm = TripleDES; // FRB/SAFR-approved default.
        this.keySequence = this.generateSequence();
        
    }

    public SecureFileUtility(String keysequence) {
        this.algorithm = TripleDES; // FRB/SAFR-approved default.
        this.keySequence = keysequence; // Initial keysequence

    }

    public static enum Operation {
        READ, WRITE
    };

    public void encrypt(String plaintext, String keyfile, String filename) throws Exception {
        if (plaintext == null) {
            throw new IllegalArgumentException("Input argument cannot be null.");
        }
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(keyfile));
        SealedObject sealedObject = new SealedObject(plaintext, cipher);
        writeToFile(filename, sealedObject);

    }

    public String decrypt(String keyfile, String filename) throws Exception {

        SealedObject sealedObject = (SealedObject) readFromFile(filename);

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, getKey(keyfile));
        String plaintext = (String) sealedObject.getObject(cipher);

        return plaintext;

    }

    private Object manageSecretKey(String filename, Operation operation) throws Exception {
        Object object = null;
        try {
            switch(operation) {
                case READ :
                    object = readFromFile(filename);
                case WRITE :
                    writeToFile(filename, getSecretKey());
            }
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(e.getMessage());
        }
        return object;
    }

    private Object readFromFile(String filename) throws Exception {
        ObjectInputStream ois = null;
        Object object = null;

        try {
            ois = new ObjectInputStream(new FileInputStream(new File(filename)));
            object = ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ois != null)
                ois.close();
        }
        return object;
    }

    private void writeToFile(String filename, Object object) throws Exception {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(new File(filename)));
            oos.writeObject(object);
            oos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage(), e.getCause());
        } catch (IOException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage(), e.getCause());
        } finally {
            if (oos != null) {
                oos.close();
            }

        }
    }

    private SecretKey getSecretKey() throws NoSuchAlgorithmException, InvalidKeyException,
            InvalidKeySpecException {

        System.out.println("My keysequence length is " + keySequence.length());
        byte[] keyValue;
        try {
            keyValue = Hex.decodeHex(keySequence.toCharArray());
        } catch (DecoderException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to decode hex", e);
        }
      
        // DESedeKeySpec keySpec = new
        // DESedeKeySpec(encodeKey(keySequence).getBytes());
        System.out.println("KeyValue=" + String.valueOf(keyValue));
        DESedeKeySpec keySpec = new DESedeKeySpec(keyValue);

        return SecretKeyFactory.getInstance(this.algorithm).generateSecret(keySpec);

        /*
         * try { keyValue = Hex.decodeHex(keySequence.toCharArray()); } catch
         * (DecoderException e) { e.printStackTrace(); throw new
         * RuntimeException("Unable to decode hex",e); }
         * System.out.println("My keyValue length is " + keyValue.length);
         * DESedeKeySpec keySpec = new DESedeKeySpec(keyValue); return
         * SecretKeyFactory.getInstance(this.algorithm).generateSecret(keySpec);
         * // This is an alternative // return
         * KeyGenerator.getInstance(algorithm).generateKey();
         */

    }

    private static final BigInteger OneTwentyEight = new BigInteger("128".getBytes());

    private String generateSequence() {

        SecureRandom random = new SecureRandom(SecureRandom.getSeed(512));
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < 4; i++) {
            sb.append(Math.abs(random.nextLong())).toString();
        }

        System.out.println("Full Secure Random number = " + sb.toString());

        // Force key to be 74
        String randomString = sb.toString();

        return (randomString.length() > 74 ? randomString.substring(0, 74) : randomString);
    }

    private String encodeKey(String keysequence) {
        if (keysequence == null) {
            return null;
        }

        System.out.println("Before EncodeKey: " + keysequence);
        BigInteger bigInt = new BigInteger(keysequence);
        bigInt = OneTwentyEight.xor(bigInt);

        System.out.println("EncodeKey returns " + bigInt.toString());
        return bigInt.toString();
    }

    public void createAndStoreKey(String keyfile) throws Exception {
        manageSecretKey(keyfile, Operation.WRITE);
    }

    private SecretKey getKey(String keyfile) throws EncryptionException {
        SecretKey key = null;
        try {
            key = (SecretKey) manageSecretKey(keyfile, Operation.READ);
            if (key == null) {
                throw new EncryptionException("unable to obtain SecretKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }
}
