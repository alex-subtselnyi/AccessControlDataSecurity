package pwdStorage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import model.ACL;
import model.User;
import util.HashHelper;

public class FileHelper {
	
	public static final String FILENAME = "." + File.separator + "users.txt";
	public static final String ACLFILE = "." + File.separator + "acl.txt";
	private static String _password = "";
	
	public static void setPassword(String password) {
		_password = password;
	}
	
	/**
	 * Checks if the user has read access to the file with the given password. 
	 * If so, a list of users will be returned by the readUsersFromFile method. This is because
	 * at the start of the application, the file will always be populated with users, so 
	 * the file is guaranteed to always have users on it. 
	 * @param password
	 * @return true if the password is valid - meaning that the user has read access to the file.
	 * @throws CryptoException 
	 */
	public static boolean canServerReadFile(String password) {
		setPassword(password); // update password to see if the given password can correctly decrypt the users file
		return readUsersFromFile().size() > 0;
	}
	
	/**
	 * Retrieves all users stored in the users public file. It checks the integrity of the file to ensure it is not corrupted.
	 * If it hasn't, it uses the password uses to encrypt the file to decrypt and thus retrieve the stored users.
	 * @return
	 */
	public static ArrayList<User> readUsersFromFile() {
		var users = new ArrayList<User>();
		
		try(BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
			if(!checkDataIntegrity()) return null;
			
			String line = reader.readLine();
			while(line != null) {
				var decryptedUser = CryptoUtils.decrypt(_password, line);
				var user = User.deserializeUser(decryptedUser);
				users.add(user);
				
				line = reader.readLine();
			}
			
		} catch (Exception ex) {
	        System.err.println("Error encrypting/decrypting file" + ex.getMessage());
	    }
		return users;
	}

	/**
	 * To store user credentials a public file was created. The credentials for each user are stored in a separate line in the format: username password
	 * being the password is a SHA-256 hashed password.
	 * @throws Exception
	 */
	public static void storeUsersInFile() throws Exception {
		
		var users = generateUserData();
		
		for(User user: users) {
			
			var encryptedUser = CryptoUtils.encrypt(_password, user);
			
			var fileWriter = new FileWriter(FileHelper.FILENAME, true);
			var bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(encryptedUser);
			bufferedWriter.newLine(); // separate user credentials
			bufferedWriter.close();
			fileWriter.close();
			
			updateCheckSum();
		}
	}

	/**
	 * Check for file integrity so that any illegal changes on the file are detected at runtime. 
	 * @return
	 * @throws Exception
	 */
	private static boolean checkDataIntegrity() throws Exception {
		var checkSumFromPref = Preferences.userNodeForPackage(String.class).get("CHECKSUM", null);
		var checkSumFromFIle = getMD5Checksum();
		
		if(checkSumFromPref.equals(checkSumFromFIle)) {
			return true;
		} else {
			System.exit(0); // stop the application if any illegal changes are detected.
			return false;
		}
	}
	
	/**
	 * Gets the MD5 checksum of the users file
	 * @returnMD5 checksum
	 * @throws Exception
	 */
	private static byte[] createChecksum() throws Exception {
       InputStream fis =  new FileInputStream(FileHelper.FILENAME);

       byte[] buffer = new byte[1024];
       MessageDigest md = MessageDigest.getInstance("MD5");
       int numRead;

       do {
           numRead = fis.read(buffer);
           if (numRead > 0) {
        	   md.update(buffer, 0, numRead);
           }
       } while (numRead != -1);

       fis.close();
       return md.digest();
   }

	/**
	 * Update the checksum of the file every time the file is encrypted. Stores the checksum in the java Preferences class.
	 * @throws Exception
	 */
	private static void updateCheckSum() throws Exception {
		Preferences.userNodeForPackage(String.class).put("CHECKSUM", getMD5Checksum());
	}
	

   // see this How-to for a faster way to convert
   // a byte array to a HEX string
   private static String getMD5Checksum() throws Exception {
       byte[] b = createChecksum();
       String result = "";

       for (int i=0; i < b.length; i++) {
           result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
       }
       return result;
   }
	   

	private static ArrayList<User> generateUserData() throws Exception {
		var users = new ArrayList<User>();
		
		users.add(new User("Alice", HashHelper.getSaltedHash("password1")));
		users.add(new User("Bob", HashHelper.getSaltedHash("password2")));
		users.add(new User("Cecilia", HashHelper.getSaltedHash("password3")));
		users.add(new User("David", HashHelper.getSaltedHash("password4")));
		users.add(new User("Erica", HashHelper.getSaltedHash("password5")));
		users.add(new User("Fred", HashHelper.getSaltedHash("password6")));
		users.add(new User("George", HashHelper.getSaltedHash("password7")));

		return users;
	}

	/**
	 * Retrieves all users stored in the users public file. It checks the integrity of the file to ensure it is not corrupted.
	 * If it hasn't, it uses the password uses to encrypt the file to decrypt and thus retrieve the stored users.
	 * @return
	 */
	public static ArrayList<ACL> readACLFromFile() {
		var acls = new ArrayList<ACL>();

		try(BufferedReader reader = new BufferedReader(new FileReader(ACLFILE))) {

			String line = reader.readLine();
			while(line != null) {
				var acl = ACL.deserializeACL(line);
				acls.add(acl);

				line = reader.readLine();
			}

		} catch (Exception ex) {
			System.err.println("Error encrypting/decrypting file" + ex.getMessage());
		}
		return acls;
	}

	
}
