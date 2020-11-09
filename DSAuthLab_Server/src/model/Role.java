package model;

public class Role {

    private final String username;
    private final String role;

    public Role(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.username, this.role);
    }


    public static Role deserializeRole(String role) {
        var credentials = role.split(" ");

        return (Role) new Role(credentials[0].trim(), credentials[1].trim());
    }
}
