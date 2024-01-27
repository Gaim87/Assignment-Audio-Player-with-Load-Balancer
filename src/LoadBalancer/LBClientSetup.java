package LoadBalancer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;


//This class waits for incoming clients. After establishing a connection, it receives their username and password and authenticates or registers them as a new user.
//Next, it utilizes a LBFunctionality object, to get the next-in-line audio server's port number and send it to the client, so that he/she is able to establishe a connection with the audio server.
public class LBClientSetup implements Runnable
{	
    Socket _clientSocket;
    
    LBClientSetup(Socket _clientSocket) throws IOException
    {
        this._clientSocket = _clientSocket;
        
        System.out.println("[LBClientSetup]: timestamp:" + Instant.now()  );     
        System.out.println("[LBClientSetup]: Client socket: " + this._clientSocket.toString());
    }

    @Override
    public synchronized void run()
    {
        try (PrintWriter outputStreamToClient = new PrintWriter(_clientSocket.getOutputStream(), true);        //Output to the client.
             BufferedReader inputStreamFromClient = new BufferedReader(new InputStreamReader(_clientSocket.getInputStream()));)     //Input from the client.
        {
	    ClientAuthentication clientAuthentication = new ClientAuthentication();
            String clientResponse = null;
            String username = null;
            String password = null;
            String action = null;
            Boolean userAuthenticationResult;
            
            while ((clientResponse = inputStreamFromClient.readLine()) != null)     //The client's username and password (lines 72-76 of ClientInitialization.java).
            {
                String[] splitClientResponse = clientResponse.split("\\s+");   //Splits around spacebar (" ") matches, because the client sends username + " " +
                username = splitClientResponse[0];                                  //password + " " + "login"/"signup", and saves each part in an array.
                password = splitClientResponse[1];
                action = splitClientResponse[2];
                                
                System.out.println("[LBClientSetup]: Client username: " + username + "\n");
                System.out.println("[LBClientSetup]: Client password: " + password);
            
                if (action.equals("signup"))
                    clientAuthentication.registerNewUser(username, password);
            
                userAuthenticationResult = clientAuthentication.checkCredentialsValidity(username, password);
                
                System.out.println("[LBClientSetup]: User has been authenticated: " + userAuthenticationResult);
                
                if (userAuthenticationResult)
                {
                    LBFunctionality audioServerPicker = new LBFunctionality();
                    String newServerData = audioServerPicker.assignNextServer();                      //Obtain the IP address and port of the next audio server (round robin LB) in line.
                    
                    outputStreamToClient.println("OK");
                    outputStreamToClient.println(newServerData);                                    //Above audio server's port number and IP address are sent to the client.

                    System.out.println("[LBClientSetup]: Sent server's IP and port (" + newServerData + ") to client.");

                    break;
                }
                else
                    outputStreamToClient.println("Username or password wrong. Please try again!");
            }
        	
            _clientSocket.close();      //Connection with client no more needed. The client has connected with the audio server.
            
            System.out.println("[LBClientSetup]: Disconnected from client.");
        }
        catch (IOException e)
        {
            System.out.println("[LBClientSetup]: " + e.getMessage());
        }
    }
}