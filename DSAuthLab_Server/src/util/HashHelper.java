package util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Inspired from: https://stackoverflow.com/questions/2860943/how-can-i-hash-a-password-in-java
 *
 * Hashes the password using salt and returns it as: salt$hash(password, salt).
 */
public class HashHelper {
    // The higher the number of iterations the more
    // expensive computing the hash is for us and
    // also for an attacker.
    private static final int iterations = 20 * 1000;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;

    public HashHelper(){}

    /**
     * Computes a salted PBKDF2 hash of given plaintext password
     * suitable for storing in a database.
     * Empty passwords are not supported.
     */
    public static String getSaltedHash(String password) throws Exception {
        byte[] salt = generateSalt();
        // store the salt with the password

        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash(password, salt).getEncoded());
    }
    
    public static byte[] generateSalt() throws NoSuchAlgorithmException {
    	return SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
    }
    /**
     * Checks whether given plaintext password corresponds
     * to a stored salted hash of the password.
     */
    public static boolean check(String password, String stored) throws Exception {
        String[] saltAndHash = stored.split("\\$");
        if (saltAndHash.length != 2) {
            throw new IllegalStateException(
                    "The stored password must have the form 'salt$hash'");
        }
        String hashOfInput = Base64.getEncoder().encodeToString(hash(password, Base64.getDecoder().decode(saltAndHash[0])).getEncoded());
        return hashOfInput.equals(saltAndHash[1]);
    }

    // using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
    // cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
    public static SecretKey hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, desiredKeyLen);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
		
        return key;
    }
}