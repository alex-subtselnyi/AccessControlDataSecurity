package authentication;

import util.AuthenticationStatusCode;

interface IAuthenticate {

   String getUserSessionTokenUUID(String username, String password) throws Exception;

   AuthenticationStatusCode authenticateUserBySessionToken(String sessionTokenUUID);

   String getUserBySessionToken(String sessionTokenUUID);
}