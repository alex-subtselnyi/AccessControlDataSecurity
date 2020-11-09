package controller;

import util.AuthenticationStatusCode;

import java.rmi.RemoteException;
import java.util.Scanner;

public class PrintController {

    private static IPrintServer printServer;
    private static String sessionKey;

    public PrintController(IPrintServer server){
        printServer = server;
        sessionKey = null;
    }

    /**
     * Allows an authenticated user to select an operation from the Print Server to perform.
     * If the user is not authenticated, before any operation can be performed, he needs to authenticate himself.
     * @throws RemoteException
     */
    public void selectOperation() throws RemoteException {

        Scanner scanner = new Scanner(System.in);
        String continue_ = "Y";

        while(continue_.equalsIgnoreCase("Y")) {
            System.out.println("Select an operation:");
            Utils.printMenu();

            int printerOperation = scanner.nextInt();

            while (printerOperation <= 0 || printerOperation > 9) {
                System.out.println("Please enter a valid operation:");
                Utils.printMenu();
                printerOperation = scanner.nextInt();
            }

            if(userAuthenticated()) {
                executeOperation(printerOperation);
            }

            System.out.print("Do you want to continue? Y/N");
            continue_ = scanner.next().trim();
        }
    }

    /**
     * Auth Process 1: If no sessionKey Exists, authenticate user using his/her credentials
     * ---------------------- If Login Successful, sessionKey is updated with the one returned from the server
     * ---------------------- If Login Failed, sessionKey returned by the server is null - the process restarts
     * Auth Process 2: If sessionKey Exists - authenticate user using sessionKey
     * The server returns response codes with the status of authentication
     * ---------------------- Auth.Status 1 - authentication successful
     * ---------------------- Auth.Status 2 - token expired
     * ---------------------- Auth.Status 3 - authentication unsuccessful
     * @return userAuthenticated() if (Process 1 Auth failed || Process 2 Auth.status != 1)
     *         TRUE if (authentication successful)
     *         FALSE otherwise
     */
    private boolean userAuthenticated() throws RemoteException{
        if(sessionKey == null || sessionKey.isEmpty()){
            var credentials = getCredentials();
            var generatedSessionKey = printServer.getUserSessionTokenUUID(credentials[0], credentials[1]);

            if(generatedSessionKey == null) {
                System.out.println("Authentication failed. Please log in again.");
                return userAuthenticated();
            } else {
                sessionKey = generatedSessionKey;
                return true;
            }
        } else {
            AuthenticationStatusCode statusCode = printServer.authenticateUserBySessionKey(sessionKey);
            switch (statusCode) {
                case Successful:
                    return true;
                case Failed:
                    System.out.println("Authentication failed. Please log in again");
                    return userAuthenticated();
                case TokenExpired:
                    System.out.println("Token expired. Please log in again");
                    return userAuthenticated();
            }
            return false;
        }
    }

    private String[] getCredentials() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username:");
        String username = scanner.next().trim();

        while(username.isEmpty()){
            System.out.print("Please enter a valid username:");
            username = scanner.next().trim();
        }

        System.out.print("Enter password:");
        String password = scanner.next().trim();

        while(password.isEmpty()){
            System.out.print("Please enter a valid password:");
            password = scanner.next().trim();
        }
        return new String[]{username, password};
    }

    private static void executeOperation(int printerOperation) throws RemoteException {
        String printerName;
        Scanner scanner = new Scanner(System.in);
        switch (printerOperation) {
            case 1 : {
                System.out.print("Enter filename:");
                String filename = scanner.next().trim();
                printerName = Utils.getPrinterName();
                String message = printServer.request("print", sessionKey, filename, printerName);
                System.out.print(message);
                break;
            }
            case 2 : {
                printerName = Utils.getPrinterName();
                String message = printServer.request("queue", sessionKey, printerName);
                System.out.print(message);
                break;
            }
            case 3 : {
                printerName = Utils.getPrinterName();
                System.out.print("Enter job index:");
                int jobIndex = scanner.nextInt();
                String message = printServer.request("topQueue", sessionKey, printerName, String.valueOf(jobIndex));
                System.out.print(message);
                break;
            }
            case 4 : {
                String message = printServer.request("start", sessionKey);
                System.out.print(message);
                break;
            }
            case 5 : {
                String message = printServer.request("stop", sessionKey);
                System.out.print(message);
                break;
            }
            case 6 : {
                String message = printServer.request("restart", sessionKey);
                System.out.print(message);
                break;
            }
            case 7 : {
                printerName = Utils.getPrinterName();
                String message = printServer.request("status", sessionKey, printerName);
                System.out.print(message);
                break;
            }
            case 8 : {
                System.out.print("Enter parameter:");
                String parameter = scanner.next().trim();
                String message = printServer.request("readConfig", sessionKey, parameter);
                System.out.print(message);
                break;
            }
            case 9 : {
                System.out.print("Enter parameter:");
                String parameter1 = scanner.next().trim();
                System.out.print("Enter parameter value:");
                String value = scanner.next().trim();
                String message = printServer.request("setConfig", sessionKey, parameter1, value);
                System.out.print(message);
                break;
            }
        }
    }

    public static class Utils {
        public static void printMenu(){
            System.out.println("1) Print");
            System.out.println("2) Queue");
            System.out.println("3) Top queue");
            System.out.println("4) Start");
            System.out.println("5) Stop");
            System.out.println("6) Restart");
            System.out.println("7) Status");
            System.out.println("8) Read config");
            System.out.println("9) Set config");
        }

        private static String getPrinterName(){
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter printer name:");
            return scanner.next().trim();
        }
    }
}
