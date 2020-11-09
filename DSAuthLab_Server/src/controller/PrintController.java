package controller;

import accessControl.AccessControlPolicy;
import authentication.AuthenticationManager;
import util.AuthenticationStatusCode;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Extending the UnicastRemoteObject class allows us to use the SSLRMIServerSocketFactory class
 * in order to make remote invocations over SSL connections.
 */
public class PrintController extends UnicastRemoteObject implements IPrintServer {

    public AuthenticationManager authenticationManager;
    public AccessControlPolicy accessControl;
    private static HashMap<String, ArrayList<String>> jobQueueByPrinter;
    private static HashMap<String, String> config;
    private static boolean serverStarted;

    /**
     * The controller sets the parameters of the UnicastRemoteObject parent class
     *
     * More precisely, defines the SslRMIServerSocketFactory with the needClientAuth argument set to TRUE
     * This is done in order to require client authentication on SSL connections
     * accepted by server sockets created by this factory
     *
     * Inspiration from: https://codeoftheday.blogspot.com/2013/07/java-remote-method-invocation-rmi-with.html
     * @throws RemoteException
     */
    public PrintController() throws RemoteException {
        authenticationManager = new AuthenticationManager();
        accessControl = new AccessControlPolicy();
        jobQueueByPrinter = new HashMap<>();
        config = new HashMap<>();
        serverStarted = false;
    }

    @Override
    public String getUserSessionTokenUUID(String username, String password) throws Exception {
        return authenticationManager.getUserSessionTokenUUID(username, password);
    }

    @Override
    public AuthenticationStatusCode authenticateUserBySessionKey(String sessionKey) throws RemoteException {
        return authenticationManager.authenticateUserBySessionToken(sessionKey);
    }

    @Override
    public String request(String methodName, String sessionKey, String[] params) {
        String message = "Permission denied";
        String user = authenticationManager.getUserBySessionToken(sessionKey);
        var access = accessControl.checkAccess(user, methodName);
        if (!access) {
            System.out.println("PERMISSION DENIED");
            return message;
        }

        System.out.println("PERMISSION GRANTED");
        message = "Permission granted. Job Done";

        switch (methodName) {
            case "print": {
                print(params[0], params[1]);
                break;
            }
            case "queue": {
                queue(params[0]);
                break;
            }
            case "topQueue": {
                topQueue(params[0], Integer.parseInt(params[1]));
                break;
            }
            case "start": {
                start();
                break;
            }
            case "stop": {
                stop();
                break;
            }
            case "restart": {
                restart();
                break;
            }
            case "status": {
                status(params[0]);
                break;
            }
            case "readConfig": {
                readConfig(params[0]);
                break;
            }
            case "setConfig": {
                setConfig(params[0], params[1]);
                break;
            }
        }
        return message;
    }

    @Override
    public void print(String filename, String printer) {
        if(serverStarted == false) System.out.println("The printer server has not yet been started.");

        if(jobQueueByPrinter.get(printer) == null || jobQueueByPrinter.get(printer).isEmpty()){
            jobQueueByPrinter.put(printer, new ArrayList<>());
        }

        jobQueueByPrinter.get(printer).add(filename);
        System.out.printf("%s added to %s%n", filename, printer);
    }

    @Override
    public void queue(String printer) {
        if(serverStarted == false) System.out.println("The printer server has not yet been started.");
        
        var jobsInPrinter = jobQueueByPrinter.get(printer);

        if(jobsInPrinter == null) {
            System.out.printf("The job can not be moved, the printer %s does not exist", printer);
            return;
        }
        
        StringBuilder sb = new StringBuilder("Queue:\n");
        int i = 1;
        sb.append(String.format("Printer Name: %s \n", printer));
        for(String job : jobsInPrinter) {
            if(jobsInPrinter.size() == i) {
                sb.append(String.format("\t- Job number %d, Filename: %s", i, job));
            } else {
                sb.append(String.format("\t- Job number %d, Filename: %s \n", i, job));
                i++;
            }
            i = 1;

        }
        System.out.println(sb.toString());
    }

    @Override
    public void topQueue(String printer, int job) {
        if(serverStarted == false) System.out.println("The printer server has not yet been started.");

        System.out.print("Top queue called");
    }

    @Override
    public void start() {
        serverStarted = true;
        System.out.println("Print server started");
    }

    @Override
    public void stop() {
        if(serverStarted == false) System.out.println("The printer server has not yet been started.");
        serverStarted = false;
        System.out.println("Print server stopped");
    }

    @Override
    public void restart() {
        System.out.println("Print server restarting...");
        stop();
        jobQueueByPrinter = new HashMap<>(); // reset jobs in all printers
        start();
    }

    @Override
    public void status(String printer) {
        if(serverStarted == false) System.out.println("The printer server has not yet been started.");

        var jobsInPrinter = jobQueueByPrinter.get(printer);

        if(jobsInPrinter == null) {
            System.out.printf("The printer %s does not have any jobs assigned at the moment.", printer);
            return;
        }
        System.out.printf("Jobs in printer %s: ", printer);
        for(String filename : jobsInPrinter){
            System.out.println(filename);
        }
    }

    @Override
    public void readConfig(String parameter) {
        if(serverStarted == false) System.out.println("The printer server has not yet been started.");

        var configValue = config.get(parameter);

        System.out.printf("%s: %s", parameter, configValue);
    }

    @Override
    public void setConfig(String parameter, String value) {
        if(serverStarted == false) System.out.println("The printer server has not yet been started.");

        config.put(parameter, value);

        System.out.printf("%s set to %s", parameter, value);
    }
}
