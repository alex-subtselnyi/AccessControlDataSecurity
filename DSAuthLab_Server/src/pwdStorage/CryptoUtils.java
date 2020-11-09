package pwdStorage;

import java.io.IOException;
import java.util.Base64;

import javax.crypto.Cipher;
import java.util.prefs.Preferences;

import model.User;
import util.HashHelper;

/**
 * A utility class that encrypts or decrypts a file.
 * To ensure the data stored in the public file is not comprehensive for all users, the file has to be properly encrypted. 
 * The AES algorithm for symmetric encryption of the file. 
 * When the server runs, there is a promt to select a key that will be used for encryption and decryption.
 * The salt used for storing the password is stored in the user preferences using the java Preferences class.  *
 */
public class CryptoUtils {
    private static final String ALGORITHM = "AES";
    
  
    public static String encrypt(String key, User user) throws IOException {
    	var stringToEncrypt = user.toString();
    	
        try {
		byte[] salt;
    		String salt_temp = Preferences.userNodeForPackage(String.class).get("ENCRYPTION_KEY_SALT", null);
    		
    	    if(salt_temp == null) {
    	    	salt = HashHelper.generateSalt();
    	    	Preferences.userNodeForPackage(String.class).put("ENCRYPTION_KEY_SALT", salt.toString());
    	    }
		else
		{
                salt=salt_temp.getBytes();
            }
    		
        	Cipher cipher = Cipher.getInstance(ALGORITHM); 
            cipher.init(Cipher.ENCRYPT_MODE, HashHelper.hash(key, salt));
          
            byte[] outputBytes = cipher.doFinal(stringToEncrypt.getBytes("UTF-8"));
             
            return Base64.getEncoder().encodeToString(outputBytes);
             
        } catch (Exception  ex) {
            System.err.println("Error encrypting/decrypting file" + ex.getMessage());
        }
		return null;
    }
 
    public static String decrypt(String key, String encrypted) {
    	try {
    		
        	var salt = Preferences.userNodeForPackage(String.class).get("ENCRYPTION_KEY_SALT", null).getBytes();
   	
            Cipher cipher = Cipher.getInstance(ALGORITHM); 
            cipher.init(Cipher.DECRYPT_MODE, HashHelper.hash(key, salt));
                		
            byte[] outputBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
             
           return new String(outputBytes);
             
        } catch (Exception  ex) {
            System.err.println("Error encrypting/decrypting file" + ex.getMessage());
        }
        return null;
    
    }
 
}
