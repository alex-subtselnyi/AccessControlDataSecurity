package model;

public class User {

   private final String username;
   private final String password;

   public User(String username, String password) {
      this.username = username;
      this.password = password;
   }

   public String getPassword() {
      return password;
   }

   public String getUsername() {
      return username;
   }
   
   @Override
   public String toString() {
	return String.format("%s %s", this.username, this.password);
   }
  
   
   public static User deserializeUser(String user) {
	   var credentials = user.split(" ");
	   
	   return (User) new User(credentials[0].trim(), credentials[1].trim());
   }
}