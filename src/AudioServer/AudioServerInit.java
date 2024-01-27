package AudioServer;

import java.net.*;
import java.io.*;


//Each time this class is run, it initializes an audio server by creating a thread that runs the AudioServerFunctionality file/class.
//As with the load balancer node, the audio server's setup is performed by the system administrator (the port number that the servers will run on is not inputted by the end user).
//IMPORTANT: Start 3 servers in ports 5001, 5002 and 5003.
public class AudioServerInit
{
    public static void main(String[] args) throws IOException
    {
        int audioServerPortNumber;
        boolean audioServerIsListening = true;
        
        System.out.println("[AudioServerInit]: Type the audio server's port number: ");
        
        BufferedReader adminInput = new BufferedReader(new InputStreamReader(System.in));

        audioServerPortNumber = Integer.parseInt(adminInput.readLine());              
        
        try (ServerSocket serverSocket = new ServerSocket(audioServerPortNumber))
        {
            System.out.println("[AudioServerInit]: Audio server listening on " + serverSocket.toString());
            
            while (audioServerIsListening)
            {            	
            	Thread t = new Thread(new AudioServerFunctionality(serverSocket.accept()));       //AudioServerFunctionality class implements Runnable.
            	
                t.start();              //The thread starts as soon as a client connects [.accept()].
                
                System.out.println("[AudioServerInit]: This client will be served from " + t.getName());
            }
	}
        catch (IOException e)
        {
            System.out.println("[AudioServerInit]: " + e.getMessage());
            System.exit(-1);
        }        
    }
}