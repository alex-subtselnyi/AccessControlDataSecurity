package authentication;

import model.*;
import pwdStorage.FileHelper;
import util.*;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AuthenticationManager implements IAuthenticate {

   private final HashMap<SessionToken, User> sessionAuthenticatedUsers;
   private HashMap<String, User> registeredUsers = new HashMap<>();

   public AuthenticationManager(){
      sessionAuthenticatedUsers = new HashMap<>();
      setRegisteredUsers(); 
   }
   
   /**
    * Updates the dictionary of registered users by reading from the users.txt file.
    */
   public void setRegisteredUsers() {
	   var users = FileHelper.readUsersFromFile();
	   for(User user: users) {
		   registeredUsers.put(user.getUsername(), user);
	   }
   }
   
   /**
    * Authenticates a given user by username and password by first checking if user is already registered.
    * If so, the given plaintext password is compared to the password already registered salted hash password.
    * Then, if there exists a session token for the authenticated user, it is updated with a newly generated token.
    * @param username
    * @param password
    * @return new session key generated for the given user.
    * @throws Exception
    */
   @Override
   public String getUserSessionTokenUUID(String username, String password) throws Exception {
      if(!registeredUsers.containsKey(username)) return null;

      var user = registeredUsers.get(username);
      boolean passwordValid = HashHelper.check(password, registeredUsers.get(username).getPassword());
      if(passwordValid) {
         for(SessionToken sessionToken : sessionAuthenticatedUsers.keySet()){
            if(sessionAuthenticatedUsers.get(sessionToken).getUsername().equals(username)){
               sessionAuthenticatedUsers.remove(sessionToken);
            }
         }

         var sessionToken = new SessionToken();
         sessionAuthenticatedUsers.put(sessionToken, user);

         return sessionToken.getUUID();
      }

      return null;
   }

   /**
    * Allows us to authenticate a user by using his session token UUID so that the user does not have
    * to enter his credentials every time an operation in the Printer Server needs to be performed.
    * @param sessionTokenUUID
    * @return
    */
   @Override
   public AuthenticationStatusCode authenticateUserBySessionToken(String sessionTokenUUID) {
      Predicate<SessionToken> byUUID = token -> token.getUUID().equals(sessionTokenUUID);

      var sessionTokens = sessionAuthenticatedUsers.keySet()
              .stream().filter(byUUID)
              .collect(Collectors.toList());

      for(SessionToken sessionToken : sessionTokens) {
         if(sessionToken.getExpirationTime().compareTo(new Date()) < 0) {
            System.out.printf("%s Token expired", sessionAuthenticatedUsers.get(sessionToken).getUsername());
            return AuthenticationStatusCode.TokenExpired;
         } else {
            System.out.printf("%s successfully identified with token", sessionAuthenticatedUsers.get(sessionToken).getUsername());

            return AuthenticationStatusCode.Successful;
         }
      }
      return AuthenticationStatusCode.Failed;
   }

   /**
    * Get username from sessionToken.
    * @param sessionTokenUUID
    * @return
    */
   @Override
   public String getUserBySessionToken(String sessionTokenUUID) {
      Predicate<SessionToken> byUUID = token -> token.getUUID().equals(sessionTokenUUID);

      var sessionTokens = sessionAuthenticatedUsers.keySet()
              .stream().filter(byUUID)
              .collect(Collectors.toList());

      for(SessionToken sessionToken : sessionTokens) {
         if(sessionToken.getExpirationTime().compareTo(new Date()) < 0) {
            System.out.printf("%s Token expired", sessionAuthenticatedUsers.get(sessionToken).getUsername());
            return "";
         } else {
            String user = sessionAuthenticatedUsers.get(sessionToken).getUsername();
            System.out.printf("%s successfully identified with token", user);

            return user;
         }
      }
      return "";
   }
}