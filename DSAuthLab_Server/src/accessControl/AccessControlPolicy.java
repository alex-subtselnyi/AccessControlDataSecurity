package accessControl;

import model.ACL;
import model.User;
import pwdStorage.FileHelper;

import java.util.HashMap;

public class AccessControlPolicy {

    private HashMap<String, String> accessList = new HashMap<>();

    public AccessControlPolicy(){
        getAcceses();
    }

    /**
     * Updates the dictionary of access controls  by reading from the acl.txt file.
     */
    public void getAcceses() {
        var acls = FileHelper.readACLFromFile();
        for(ACL acl: acls) {
            accessList.put(acl.getUsername(), acl.getPermissions());
        }
    }

    /**
     * Checks the permission.
     */
    public boolean checkAccess(String username, String method) {
        var acl = accessList.get(username);
        if (acl == null) {
            return false;
        }
        System.out.println(acl);
        var permissions = acl.split(",");
        for(String perm: permissions) {
            if (perm.equals(method)) {
                System.out.println("found");
                return true;
            }
        }
        return false;
    }

}
