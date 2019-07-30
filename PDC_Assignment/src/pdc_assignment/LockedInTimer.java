package pdc_assignment;

// LockedInTimer class is a thread that waits for 2 seconds then responds
// to the calling class notifying the timer is up.
// This class is used to time how long the program should pause between 
// the user selecting an answer, and when the correct answer should be revealed
public class LockedInTimer extends Thread 
{
    private GUIGame backend;
    
    public LockedInTimer(GUIGame backend)
    {
        super();
        this.backend = backend;
    }
    
    @Override
    public void run()
    {
        Timer.wait(2000);
        backend.lockedInTimerDone();
    }
}
