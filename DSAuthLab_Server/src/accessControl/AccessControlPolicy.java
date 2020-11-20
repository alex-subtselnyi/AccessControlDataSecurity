package accessControl;

import model.Role;
import pwdStorage.FileHelper;

import java.util.HashMap;

public class AccessControlPolicy {

    private HashMap<String, String> rolesList = new HashMap<>();
    private HashMap<String, String> permissionList = new HashMap<>();

    public AccessControlPolicy(){
        getRoles();
        getPermissions();
    }

    /**
     * Updates the dictionary of roles reading from the roles.txt file.
     */
    public void getRoles() {
        rolesList = FileHelper.readRolesFromFile();
    }

    /**
     * Updates the dictionary of roles reading from the roles.txt file.
     */
    public void getPermissions() {
        permissionList = FileHelper.readPermissionsFromFile();
    }

    /**
     * Checks the permission.
     */
    public boolean checkAccess(String username, String method) {
        var role = rolesList.get(username);
        if (role == null) {
            return false;
        }
        var permissions = permissionList.get(role);
        System.out.println(role);
        var perms = permissions.split(",");
        for(String perm: perms) {
            if (perm.equals(method)) {
                System.out.println("found");
                return true;
            }
        }
        return false;
    }

}
