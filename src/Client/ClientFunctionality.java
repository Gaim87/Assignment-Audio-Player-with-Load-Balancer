package Client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


//This class enables the client to interact with the server (choose a song from the list that the server sends and play it back).
public class ClientFunctionality
{
    String _audioServerIP;
    int _audioServerPort;
    AudioClip _songToBePlayed;
    
    public ClientFunctionality(String audioServerIP, int audioServerPort)
    {
	_audioServerIP = audioServerIP;
        _audioServerPort = audioServerPort;
    }

    //Connects the client with an audio server.
    public void interactWithServer ()
    {
        try
            (Socket audioServerSocket = new Socket(_audioServerIP, _audioServerPort);            
            PrintWriter outputStream = new PrintWriter(audioServerSocket.getOutputStream(), true);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(audioServerSocket.getInputStream()));
            BufferedReader userInputStream = new BufferedReader(new InputStreamReader(System.in));
            DataInputStream songDataInputStream = new DataInputStream(audioServerSocket.getInputStream());)     //Transfers the songs.
        {
            System.out.println("[ClientFunctionality]: Client connected to server " + audioServerSocket.toString());
            String helperString;
            List<String> songList = new ArrayList<>();

            while ((helperString = inputStream.readLine()) != null)        //Prints the song list received from the server.
            {
                System.out.println (helperString);
                
                songList.add(helperString);

                if (helperString.contains("Type"))   //The full song list has arrived and the server has instructed the user to type the number of the song he/she wants to listen to.
                    break;
            }

            while (((helperString = userInputStream.readLine()) != null))   //The helper string is now assigned to the user's input.
            {
                if (helperString.equals("0"))             //Zero (0) disconnects the client from the server.
                {
                    outputStream.println(helperString);        //Informs the server and disconnects.
                    System.out.println("[ClientFunctionality]: Disconnected from server.");
                    return;
                }
                else
                {
                    outputStream.println(helperString);     //The selected song's number is sent to the server.                   

                    int receivedSongLength = songDataInputStream.readInt(); //The size of the song that is going to be received.
                    byte[] receivedSongData = new byte[1];

                    if (receivedSongLength >0)
                    {
                        receivedSongData = new byte[receivedSongLength];
                        songDataInputStream.readFully(receivedSongData, 0, receivedSongData.length);    //"b" is the buffer/container for the song's/input stream's data, "off" the starting
                    }                                                                                            //point/index the data is going to be written at and "len" the length of bytes to be
                                                                                                                 //read/written. To sum up, the song is saved in "receivedSongData".
                                                                                                                 
                    //When the user ultimately presses 0, the two below streams must close. Since their initialization happens inside an if-else block, though,  I can't check
                    //if they have been initialized and close them. Thus, I used a try-catch with resources.
                    try (InputStream helperInputStream = new ByteArrayInputStream(receivedSongData);
                         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(helperInputStream);)
                    {
                        _songToBePlayed = new AudioClip(audioInputStream);         //To play a song, you have to initialize a Clip object by providing it with an AudioInputStream object.
                        _songToBePlayed.play();                                    //I created an AIS object by converting the song/byte array into an InputStream object and feeding it to the AIS.

                        System.out.println ("Press:\n\n" + "\"1\" (pause)\n" +
                                                           "\"2\" (resume)\n" +
                                                           "\"3\" (stop)\n" +
                                                           "\"4\" (play)\n" +
                                                           "\"5\" (select another song)\n" + 
                                                           "\"0\" (exit program)");

                        String audioControlInput;

                        //Not using a switch statement, because we don't want the loop to break, unless the user opts to listen to another song.
                        while ((audioControlInput = userInputStream.readLine()) != null)
                        {
                            if (audioControlInput.equals("5"))
                            {
                                _songToBePlayed.stop();
                                
                                for (String song : songList)
                                    System.out.println(song);     //Assists the user by re-displaying the initial message (songs and instructions) sent from the server.

                                break;
                            }
                            else if (audioControlInput.equals("4"))
                            {
                                _songToBePlayed.play();
                                
                                System.out.println ("Press:\n\n" + "\"1\" (pause)\n" +
                                                                   "\"2\" (resume)\n" +
                                                                   "\"3\" (stop)\n" +
                                                                   "\"4\" (play)\n" +
                                                                   "\"5\" (select another song)\n" + 
                                                                   "\"0\" (exit program)");
                            }
                            else if (audioControlInput.equals("3"))
                            {
                                _songToBePlayed.stop();
                                
                                System.out.println ("Press:\n\n" + "\"1\" (pause)\n" +
                                                                   "\"2\" (resume)\n" +
                                                                   "\"3\" (stop)\n" +
                                                                   "\"4\" (play)\n" +
                                                                   "\"5\" (select another song)\n" + 
                                                                   "\"0\" (exit program)");                            
                            }
                            else if (audioControlInput.equals("2"))
                            {
                                _songToBePlayed.resumeAudio();
                                
                                System.out.println ("Press:\n\n" + "\"1\" (pause)\n" +
                                                                   "\"2\" (resume)\n" +
                                                                   "\"3\" (stop)\n" +
                                                                   "\"4\" (play)\n" +
                                                                   "\"5\" (select another song)\n" +
                                                                   "\"0\" (exit program)");                            
                            }
                            else if (audioControlInput.equals("1"))
                            {
                                _songToBePlayed.pause();
                                
                                System.out.println ("Press:\n\n" + "\"1\" (pause)\n" +
                                                                   "\"2\" (resume)\n" +
                                                                   "\"3\" (stop)\n" +
                                                                   "\"4\" (play)\n" +
                                                                   "\"5\" (select another song)\n" + 
                                                                   "\"0\" (exit program)");
                            }
                            else if (audioControlInput.equals("0"))
                            {
                                outputStream.println(helperString);        //Informs the server and disconnects.
                                System.out.println("[ClientFunctionality]: Disconnected from server.");
                                return;
                            }
                        }
                    }
                }
            }
         }
         catch (Exception e)
         {
             System.out.println("[ClientFunctionality]: " + (e.toString()) );
         }
    }
}