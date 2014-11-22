package org.markl.crypto;


public class EncryptionTestRunner {

    private static final String KEYFILE = "c://temp//myfile.key";

    private static final String PASSWORD_FILE = "c://temp//encrypted_password.dat";

    // private static final String SECRET_KEY =
    // "161240328984847399324738060808398936334569379601032361724636453956494886226";

    private static final String SECRET_KEY =
        "12345678901234569324738060808398936334569379601032361724636453956494886226";

    // 171241228884847288224728

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // SecureFileUtility fileUtil = new SecureFileUtility(SECRET_KEY);
        SecureFileUtility fileUtil = new SecureFileUtility();

        fileUtil.createAndStoreKey(KEYFILE);

        System.out.println("Encrypting... ");

        // use the key to encrypt sometext
        fileUtil.encrypt("myP@55W0rdIsL@me", KEYFILE, PASSWORD_FILE);

        System.out.println("decrypting... ");
        String decrypted = fileUtil.decrypt(KEYFILE, PASSWORD_FILE);

        System.out.println("decrypted password: " + decrypted);

        // Test Crypo utility.
//        CryptoUtility crypto = CryptoUtility.getInstance();

       // String encrypted = crypto.encrypt("Hello World blah blah", SECRET_KEY);
        // System.out.println("Encrypted " + encrypted);

         //decrypted = crypto.decrypt("Hello World", SECRET_KEY);
         //System.out.println("decrypted= " + decrypted);

    }

}
