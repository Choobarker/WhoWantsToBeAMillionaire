package pdc_assignment;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

// SoundPlayer class is a thread which plays the sound clip passed in the 
// constructor
public class SoundPlayer extends Thread
{
    private String audioUrl;
    private AudioClip clip;
    
    public SoundPlayer(String audioUrl)
    {
        super();
        this.audioUrl = audioUrl;
        
        try
        {
            clip = Applet.newAudioClip(new URL("file", "localhost", audioUrl));
        }
        catch(MalformedURLException e)
        {
            System.out.println("MalformedURLException e: " + e.getMessage());
        }
    }
    
    @Override
    public void run()
    {
        if(audioUrl != null)
        {
            clip.play();
        }
    }
    
    // finish method is used to force the thread to finish playing it's sound clip
    public void finish()
    {
        if(clip != null)
        {
            clip.stop();
        }
    }
}
