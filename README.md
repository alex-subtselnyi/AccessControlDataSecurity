# Authentication Lab
This is the implementation of the RMI server with password based authentication.
## Server 
To run the server, run the class "Server.java" located in DSAuthLab_Server/src/view.\
The first time the server is run, the administrator is prompted to insert a key, which is the encryption key of the server.\
After running the server the first time, the dummy users are generated and added to the "users.txt" file.\
Every time the server is run, the key given in the initial run needs to be input in order for the server to start.
## Client 
To run the client, run the class "Client.java" located in DSAuthLab_Client/src/view.\
In order for the client to work, the server needs to be already running.

## Branches
The acl branch implements access control list.\
The roles branch implements the role based access control.
