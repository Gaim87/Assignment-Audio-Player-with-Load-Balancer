package AudioServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;


//This class contains the audio server's functionality. It uploads the three available audio files from the server's hard drive and saves them in an byte array. After that, the songs' names
//are sent to the user as a list and he/she can listen to a song by streaming it.
public class AudioServerFunctionality implements Runnable
{
    Socket _audioServerSocket;
    String[] _songPathsList = new String[] {"./LITTLE RICHARD - KEEP A KNOCKIN'.wav", "./IMMORTAL - BEYOND THE NORTH WAVES.wav", "./PINK FLOYD - SET THE CONTROLS FOR THE HEART OF THE SUN.wav"};
    byte[] _song;
    final byte[][] _songList = new byte[3][];
    
    AudioServerFunctionality(Socket audioServerSocket)
    {
	this._audioServerSocket = audioServerSocket;
    }

    void initializeAudioServer()    //Reads a song's bytes/data and saves them inside a byte array, which is then saved in a two-dimensional array.
    {
        Path songFilePath;
        
        try
        {
            for (int i = 0; i <= 2; i +=1)
            {
                songFilePath = Paths.get(_songPathsList[i]);
                _song = Files.readAllBytes(songFilePath); 
                _songList[i] = _song;
            }
            
            System.out.println("[AudioServerFunctionality]: Audio server initialization complete.");
        }
        catch (Exception e)
        {
            System.out.println("[AudioServerFunctionality]: " + e.getMessage());
        }
    }  
    
    @Override
    public void run()
    {
	initializeAudioServer();
        
	System.out.println("[AudioServerFunctionality]: Client successfully connected to audio server " + this._audioServerSocket.toString() + ")");
        
        String clientMessage;
        
            //Connections with ClientFunctionality class (and the client).
        try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(_audioServerSocket.getInputStream()));   //Input from the client.
             PrintWriter outputStream = new PrintWriter(_audioServerSocket.getOutputStream(), true);                //Output to the client.
             DataOutputStream dataOutputStream = new DataOutputStream(_audioServerSocket.getOutputStream());)               //Byte array/song output to the client.
        {
            outputStream.println("\nSong list: \n");        //As soon as a client is connected, the song list is sent to him/her.
            
            //Removes the "./" and ".wav" from the song's name (as saved in the array), before sending it to the client.
            for (int i = 0; i <= 2; i +=1)
                outputStream.println((i+1) + ". " + _songPathsList[i].substring(2, _songPathsList[i].length()-4));  //Sends the song list to the user.
            
            outputStream.println("\nType a song's number, to listen to it, or 0 (zero), to exit the program: \n");
            
            clientMessage = inputStream.readLine();
            
            while (clientMessage != null)
            {
                int clientMessageToInt = Integer.parseInt(clientMessage);
                
                synchronized (_songList)
                {
                    switch (clientMessageToInt)     //The song list the user sees is numbered 1..., 2..., 3... and not 0..., 1..., 2...
                    {
                        case 1:  System.out.println("[AudioServerFunctionality]: Client selected song number " + clientMessage +
                                                    " (" + _songPathsList[clientMessageToInt-1].substring(2, _songPathsList[clientMessageToInt-1].length()-4) + ")");
                                 dataOutputStream.writeInt((_songList[clientMessageToInt-1]).length);  //Sends the array's size, so that the input stream "knows" how many bytes to expect.
                                 dataOutputStream.write(_songList[clientMessageToInt-1]);                //Sends the actual song.
                                 System.out.println("[AudioServerFunctionality]: Song sent to client.");
                                 break;
                        case 2:  System.out.println("[AudioServerFunctionality]: Client selected song number " + clientMessage +
                                                    " (" + _songPathsList[clientMessageToInt-1].substring(2, _songPathsList[clientMessageToInt-1].length()-4) + ")");
                                 dataOutputStream.writeInt((_songList[clientMessageToInt-1]).length);
                                 dataOutputStream.write(_songList[clientMessageToInt-1]);
                                 System.out.println("[AudioServerFunctionality]: Song sent to client.");
                                 break;
                        case 3:  System.out.println("[AudioServerFunctionality]: Client selected song number " + clientMessage +
                                                    " (" + _songPathsList[clientMessageToInt-1].substring(2, _songPathsList[clientMessageToInt-1].length()-4) + ")");
                                 dataOutputStream.writeInt((_songList[clientMessageToInt-1]).length);
                                 dataOutputStream.write(_songList[clientMessageToInt-1]);
                                 System.out.println("[AudioServerFunctionality]: Song sent to client.");
                                 break;
                        case 0:  System.out.println("[AudioServerFunctionality]: Client logged off.");
                                 inputStream.close();
                                 outputStream.close();
                                 dataOutputStream.close();
                                 _audioServerSocket.close();
                                 return;
                        default: System.out.println("[AudioServerFunctionality]: Wrong song number. Please try again.");
                                 break;
                    }
                }
                
                clientMessage = inputStream.readLine();
            }
        }
        catch (IOException e)
        {
            Logger.getLogger(AudioServerFunctionality.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("[AudioServerFunctionality]: " + e.getMessage());
        }                
    }
}