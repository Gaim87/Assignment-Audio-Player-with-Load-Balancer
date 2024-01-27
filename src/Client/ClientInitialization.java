package Client;

import java.io.*;
import java.net.*;


//This class establishes a connection between a client and the LB. It then authenticates the user and creates and runs a ClientFunctionality object,
//establishing a connection with an audio server.
public class ClientInitialization
{
    public static void main(String[] args) throws IOException
    {
    	String clientResponse = null;
    	String loadBalancerIP = "127.0.0.1";    //Local
    	int loadBalancerPortNumber = 5000;
    	int audioServerPort;
    	String audioServerIP;
        String messageFromLB;
       
        System.out.println("[ClientInitialization]: Connecting to load balancer with IP " + loadBalancerIP + ", on port: " + loadBalancerPortNumber );
        
        try (Socket socketConnWithLB = new Socket(loadBalancerIP, loadBalancerPortNumber);      //Connection with the load balancer.
             PrintWriter outputStreamToLB = new PrintWriter(socketConnWithLB.getOutputStream(), true);      //Output to the load balancer.
             BufferedReader inputStreamFromLB = new BufferedReader(new InputStreamReader(socketConnWithLB.getInputStream()));   //Input from the load balancer.
             BufferedReader inputFromUser = new BufferedReader(new InputStreamReader(System.in));)    //System (user) input.
        {
            System.out.println("[ClientInitialization]: Press \"1\", to log in, \"2\", to sign up or zero (0), to exit the program:");
            
            String userInput = inputFromUser.readLine();            
            
            while (!userInput.equals("1") && !userInput.equals ("2") && !userInput.equals("0"))     //In case the user inputs random characters.
            {
                System.out.println("[ClientInitialization]: Please press \"1\", to log in, \"2\", to sign up or zero (0), to exit the program:");

                userInput = inputFromUser.readLine();
            }
            
            if (userInput.equals("0"))  //"Exit"
                return;
            else if (userInput.equals("1"))     //"Log in"
            {
                while (true)
                {
                    System.out.println("[ClientInitialization]: Type your USERNAME or zero (0), to exit the program:");

                    String clientUsername = inputFromUser.readLine();
                    
                    while (clientUsername.isBlank())            //Prevents user inputting a blank space (enter key or spacebar)
                    {                                           //as a username/password.
                        System.out.println("[ClientInitialization]: Type your USERNAME or zero (0), to exit the program:");
                        
                        clientUsername = inputFromUser.readLine();
                    }
                    
                    if (clientUsername.equals("0"))
                        return;

                    System.out.println("[ClientInitialization]: Type your PASSWORD or zero (0), to exit the program:");
                    
                    String clientPassword = inputFromUser.readLine();
                    
                    while (clientPassword.isBlank())
                    {
                        System.out.println("[ClientInitialization]: Type your PASSWORD or zero (0), to exit the program:");
                        
                        clientPassword = inputFromUser.readLine();
                    }
                    
                    if (clientPassword.equals("0"))
                        return;
                    
                    clientResponse = clientUsername + " " + clientPassword;
                    
                    outputStreamToLB.println(clientResponse + " " + "login");   //"login" message left for possible future use (not utilized by the load balancer).
                                                                                //Sends user's username and password to the load balancer for authentication.
                    messageFromLB =  inputStreamFromLB.readLine();
                    
                    if (messageFromLB.equals("OK"))    //If the user has been authenticated, the LB responds with "OK" and the loop breaks.
                        break;
                    else
                        System.out.println(messageFromLB);
                }
            }
            else        //"Sign up"
            {
                while (true)
                {
                    System.out.println("[ClientInitialization]: Type a USERNAME or zero (0), to exit the program:");
                
                    String clientUsername = inputFromUser.readLine();
                    
                    while (clientUsername.isBlank())
                    {
                        System.out.println("[ClientInitialization]: Type your USERNAME or zero (0), to exit the program:");
                        
                        clientUsername = inputFromUser.readLine();
                    }
                    
                    if (clientUsername.equals("0"))
                        return;

                    System.out.println("[ClientInitialization]: Type a PASSWORD or zero (0), to exit the program:");
                    
                    String clientPassword = inputFromUser.readLine();
                    
                    while (clientPassword.isBlank())
                    {
                        System.out.println("[ClientInitialization]: Type your PASSWORD or zero (0), to exit the program:");
                        
                        clientPassword = inputFromUser.readLine();
                    }
                    
                    if (clientPassword.equals("0"))
                        return;
                    
                    clientResponse = clientUsername + " " + clientPassword;
                    
                    outputStreamToLB.println(clientResponse + " " + "signup");
                    
                    messageFromLB =  inputStreamFromLB.readLine();
                    
                    if (messageFromLB.equals("OK"))
                    {
                        System.out.println("[ClientInitialization]: User successfully registered.");
                        
                        break;
                    }
                    else
                        System.out.println(messageFromLB);

                }
            }

            String[] LBResponse = inputStreamFromLB.readLine().split("\\s+");  //Next-in-line audio server's IP and port received from the LB right after the "OK" message.
            audioServerIP = LBResponse[0];                                          //Splits around spacebar (" ") matches, because the server sends IP + " " + port number.
            audioServerPort = Integer.parseInt(LBResponse[1]);

            System.out.println("[ClientInitialization]: Received server's IP/Port: " + audioServerIP + " " + audioServerPort);
            
            socketConnWithLB.close();       //Connection with LB no longer needed.
            
            System.out.println("[ClientInitialization]: Client disconnected from load balancer");

            ClientFunctionality echoClient = new ClientFunctionality(audioServerIP, audioServerPort);   //A connection with the above audio server is established.

            echoClient.interactWithServer();            //Connects the client with the audio server.
        }
        catch (UnknownHostException e)
        {
            System.out.println("[ClientInitialization]: " + e.getMessage());
            System.exit(1);
        }
        catch (IOException e)
        {
            System.out.println("[ClientInitialization]: " + e.getMessage());
            System.exit(1);
        } 
    }
}