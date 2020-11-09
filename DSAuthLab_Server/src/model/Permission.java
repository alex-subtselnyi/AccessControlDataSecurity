package model;

public class Permission {

    private final String role;
    private final String permissions;

    public Permission(String role, String permissions) {
        this.role = role;
        this.permissions = permissions;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.role, this.permissions);
    }


    public static Permission deserializePermission(String perm) {
        var credentials = perm.split(" ");

        return (Permission) new Permission(credentials[0].trim(), credentials[1].trim());
    }
}
