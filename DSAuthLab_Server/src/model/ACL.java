package model;

public class ACL {

    private final String username;
    private final String permissions;

    public ACL(String username, String permissions) {
        this.username = username;
        this.permissions = permissions;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.username, this.permissions);
    }


    public static ACL deserializeACL(String acl) {
        var credentials = acl.split(" ");

        return (ACL) new ACL(credentials[0].trim(), credentials[1].trim());
    }
}
