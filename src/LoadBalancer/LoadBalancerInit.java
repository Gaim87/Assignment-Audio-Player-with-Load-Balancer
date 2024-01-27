package LoadBalancer;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Instant;


//The first class that has to be run. Initializes the load balancer/bootstrap node/thread, which establishes a client - server connection.
//Run by the system's administrator, when setting the service up.
public class LoadBalancerInit
{
    public static void main(String[] args) throws NumberFormatException, IOException
    {		
        int LBPortNumber = 5000;
        
        System.out.println("[LoadBalancerInit]: timestamp:" + Instant.now());

        boolean LBIsListening = true;
        
        try (ServerSocket serverSocket = new ServerSocket(LBPortNumber))
        { 
            System.out.println("[LoadBalancerInit]: LB listening on: " + serverSocket.toString());
            
            while (LBIsListening)
            {            	
            	Thread t = new Thread(new LBClientSetup(serverSocket.accept()));    //LBClientSetup class implements Runnable.
                
            	System.out.println("Client connected. Thread ID: " + t.getName());
                
            	t.start();          //The thread starts as soon as a client connects [.accept()].
            }   
        }
        catch (IOException e)
        {
            System.out.println("[LoadBalancerInit]: " + e.getMessage());
            System.exit(-1);
        }
    }	
}