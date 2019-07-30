package pdc_assignment;

// AnswerRevealTimer class is a thread that waits for 2 seconds then responds
// to the calling class notifying the timer is up.
// This class is used to time how long the correct answer is shown before
// moving on to the next question.
public class AnswerRevealTimer extends Thread
{
    GUIGame backend;
    
    public AnswerRevealTimer(GUIGame backend)
    {
        super();
        this.backend = backend;
    }
    
    @Override
    public void run()
    {
        Timer.wait(2000);
        backend.answerRevealTimerDone();
    }
    
}
