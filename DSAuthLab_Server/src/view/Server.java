package view;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import controller.IPrintServer;
import controller.PrintController;
import pwdStorage.FileHelper;

public class Server {
	
	public static final int RETRY_COUNT = 5;
	
	/**
	 * Once the server starts, we check if the users file already exists, if not, we populate it by first setting a password 
	 * which will be hashed and used as a symmetric key to encrypt the contents of the public file. 
	 * 
	 * If the file already exists, the symmetric key to decrypt the password has to be entered. This allows the server to decrypt the contents of the file.
	 * If the user enters the wrong password, he will have 5 chances to input it correctly, afterwards, the program ends.
	 * @param args
	 */
    public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

        try {
        	File usersFile = new File(FileHelper.FILENAME);
        	if(usersFile.exists()) {
        		boolean canServerReadFile = false; // password valid
        		int count = 0;
        		while(!canServerReadFile) {
        			if(count < RETRY_COUNT) { 
	            		System.out.print("Enter password to access the users file:");
	            		var password = scanner.next();
	            		
	            		canServerReadFile = FileHelper.canServerReadFile(password);
        			} 
        			count++;
        		}
        		if(count == RETRY_COUNT)
        			System.exit(0);
        		
        	} else {
        		System.out.print("Set password to access user file:");
        		String password = scanner.next();
         		FileHelper.setPassword(password);

    			FileHelper.storeUsersInFile();
        	}
			scanner.close();

            IPrintServer stub = new PrintController();
            Registry registry = LocateRegistry.createRegistry(1099);

            registry.bind("print_server", stub);
            System.out.println("PrintServer bound");
            
            stub.getUserSessionTokenUUID("Alice", "password1");
            
        } catch (Exception e) {
            System.err.println("PrintServer exception:");
            e.printStackTrace();
        }
    }

}
