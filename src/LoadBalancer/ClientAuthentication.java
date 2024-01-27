package LoadBalancer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


//Methods for registering a user and authenticating his username and password.
public class ClientAuthentication
{    
    List<User> validUsers = new ArrayList<>();
    //static List<User> loggedInUsers = new ArrayList<> ();
      
    public ClientAuthentication()
    {
        loadUsersFromFile();
    }
    
    public Boolean checkCredentialsValidity(String username, String password)
    {        
        Boolean userIsValid = false;
        
        //Commented out because the method to delete a user from the list of active users does not work (for the same reason reducing an active connection from the
        //respective list also doesn't) and this prevents a user that has logged off to log in again, because his name still exists in said list.
        
//        for (User user : loggedInUsers)
//        {
//            if ((user.getUsername().equals (usrnm) && user.getPassword().equals (pwd)))
//            {
//                System.out.println("[EchoServer_RandomLoadBalancer] User is already logged in!");
//                return isUsrValid;
//            }
//
//        }
     
        for (User user : validUsers)
        {            
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) //Checks if the given parameters/username and password match any pair
            {                                                                                             //of the valid usersnames and passwords loaded from the external file.
                userIsValid = true;
                
                //loggedInUsers.add (user);
                
                System.out.println("[ClientAuthentication]: User has been authenticated.");
                
                break;
            }
            else
                userIsValid = false;
        }
        
        return userIsValid;
    }
    
    public void registerNewUser(String newUserUsername, String newUserPassword)
    {        
        saveUserToDisk(newUserUsername, newUserPassword);
        validUsers.add(new User(newUserUsername, newUserPassword));
        
        System.out.println("[ClientAuthentication]: User successfully registered.");

    }
    
    void loadUsersFromFile()                                                             //Loads the .txt file from the hard drive, splits its contents and creates User objects.
    {
        try {                                                                            //Try block, in case the file does not exist.
            FileReader fileReader = new FileReader("./UserList.txt");
            Scanner scanner = new Scanner(fileReader);
            String fileContents = "";

            while (scanner.hasNextLine())
            {
                fileContents = fileContents + scanner.nextLine();
            }

            String[] userCredentials = fileContents.split("%");                     //Splits the file's contents into username-password pairs. The percent symbol is not the most
                                                                                         //appropriate way to split an entry, since it can be part of the password. The best practice would
            for (int x = 0; x < userCredentials.length; x += 1)                          //be to utilize a DB, but I could not link one to NetBeans. TODO
            {                                                                            //The ampersand symbol (&) separates users and the spacebar (" ") each user's username from his/her
                String[] splitCredentials = userCredentials[x].split(" ");          //password.
                validUsers.add (new User(splitCredentials[0], splitCredentials[1]));
            }
        }
        catch (IOException e)
        {
            System.out.println("[ClientAuthentication]: Error while loading user credentials." + e.getMessage());
        }
    }

    synchronized void saveUserToDisk(String username, String password)                  //Saves a user's credentials in an external file.
    {
        File userCredentialsFile = new File("./UserList.txt");

        String dataForFile = username + " " + password + "%";

        try {
            FileWriter fileWriter = new FileWriter(userCredentialsFile, true);
            fileWriter.write(dataForFile);
            fileWriter.flush();
            System.out.println("[ClientAuthentication]: User credentials successfully saved to " + userCredentialsFile.getPath());
        }
        catch (IOException except)
        {
            System.out.println("[ClientAuthentication]: Error while saving user credentials to file." + except.getMessage());
        }
    }
}