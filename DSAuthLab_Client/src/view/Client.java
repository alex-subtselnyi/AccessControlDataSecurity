package view;


import controller.IPrintServer;
import controller.PrintController;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        System.err.println("Connecting to server..");
        IPrintServer printServer;

        Registry registry = LocateRegistry.getRegistry(null, 1099);
        System.err.println("This might take a while...");
        printServer = (IPrintServer)
                  registry.lookup("print_server");

        var controller = new PrintController(printServer);
        controller.selectOperation();
    }
}

