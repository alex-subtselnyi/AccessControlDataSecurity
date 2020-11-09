package controller;

import util.AuthenticationStatusCode;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPrintServer extends Remote {
   String request(String methodName, String sessionKey, String... params) throws RemoteException;

   String getUserSessionTokenUUID(String username, String password) throws RemoteException;
   AuthenticationStatusCode authenticateUserBySessionKey(String sessionKey) throws RemoteException;
}