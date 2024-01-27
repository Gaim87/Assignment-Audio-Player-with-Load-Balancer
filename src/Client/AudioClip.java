package Client;

import java.io.IOException;  
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
  

//This class plays back audio files. It is based on Java's Clip interface.
public class AudioClip 
{
    Long _currentFrame;     //Stores song's current position in time.    
    Clip _clip;      
    static AudioInputStream _audioInputStream;
    String _songStatus = "Ready";
  
    public AudioClip (AudioInputStream audioInputStream) throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {
        _audioInputStream = audioInputStream;          
        _clip = AudioSystem.getClip();              //Clip reference.
        
        _clip.open(_audioInputStream);
    }
    
    //Tried !isRunning(), clip.getMicrosecondPosition() == 0 and clip.getMicrosecondPosition() == _clip.getMicrosecondLength(), to make it re-play the song by pressing "4" ("Play"),
    //when it has reached its end, but could not make it work.
    public void play()       //Always plays the song from the beginning.
    {
        if (_songStatus.equals("Paused") || _songStatus.equals("Stopped") || _songStatus.equals("Ready"))
        {
            _clip.start();
            _songStatus = "Playing";
        
            System.out.println("[AudioClip]: The song is playing.");
        }
        else
        {
            System.out.println("[AudioClip]: The song is already playing.");
        }
    }
      
    public void pause()    //The song actually stops. The difference from the stop() method is that here, we save the point in time where we paused it, so that we can resume it, if we want.
    {
        if (_songStatus.equals("Playing"))
        {
            this._currentFrame = this._clip.getMicrosecondPosition();               
            _clip.stop();

            _songStatus = "Paused";

            System.out.println("[AudioClip]: The song has been paused.");
        }
        else if (_songStatus.equals("Paused"))
            System.out.println("[AudioClip]: The song is already paused.");

        else
            System.out.println("[AudioClip]: Cannot pause because song palyback has stopped. Press \"4\", to play it again.");
    }
      
    public void resumeAudio() throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {
        if (_songStatus.equals("Paused"))
        {
            _clip.setMicrosecondPosition(_currentFrame);    //We set the song's timer to the point where we paused it.            
            _songStatus = "Playing";

            _clip.start();

            System.out.println("[AudioClip]: The song has resumed playback.");
        }
        else if (_songStatus.equals("Playing"))
            System.out.println("[AudioClip]: The song is already playing.");
        else
            System.out.println("[AudioClip]: Cannot resume because song playback has stopped. Press \"4\", to play it again.");
    }      
      
    public void stop() throws UnsupportedAudioFileException, IOException, LineUnavailableException  //We stop the song and reset all timers.
    {
        if (!_songStatus.equals("Stopped"))
        {
            _currentFrame = 0L;
            _clip.stop();
            _currentFrame = 0L;
            _clip.setMicrosecondPosition(0);
            _songStatus = "Stopped";

            System.out.println("[AudioClip]: Song playback has stopped.");
        }
        else
            System.out.println("[AudioClip]: Song playback has already stopped.");
    }
}