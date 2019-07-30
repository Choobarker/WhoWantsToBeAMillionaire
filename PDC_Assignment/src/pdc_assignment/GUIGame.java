package pdc_assignment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

// GUIGame class is the backend of the game
public class GUIGame 
{
    private DBManager dbm;
    private GameScreen frontend;
    private JFrame frame;
    
    SoundPlayer soundPlayer;
    
    private ArrayList<Question> questions;
    private ArrayList<Integer> values;
    
    private boolean fifty = true;
    private boolean askAudience = true;
    private boolean playing = true;
    
    private char userAnswer = 0;
    private int currentIndex = 0;
    private String correctAnswer = "";
    
    public GUIGame()
    {        
        frontend = new GameScreen(this);    
        
        questions = new ArrayList<>();
        dbm = new DBManager();
        setQuestions();
        initValues();
        frontend.setListModel(values);
        
        frame = new JFrame();
        frame.setContentPane(frontend);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);       
        
        String message = "For optimal gaming experience, ensure computer sound is on!";
        JOptionPane.showMessageDialog(null, message, "", JOptionPane.INFORMATION_MESSAGE);
        
        play();
    }
    
    // play method is responsible for playing the correct game sound depending
    // on the current question, and updating the frontend with the corrent
    // question and corresponding answers.
    private void play()
    {
        if(soundPlayer != null)
        {
            soundPlayer.finish();
        }
        
        String audio = "";
        int currentValue = values.get(currentIndex);
        
        if(currentValue == 1000000)
        {
            audio = "sounds" + File.separator + "1,000,000_question.wav";
        }
        else if(currentValue == 500000)
        {
            audio = "sounds" + File.separator + "500,000_question.wav";
        }
        else if(currentValue >= 64000)
        {
            audio = "sounds" + File.separator + "64,000_question.wav";
        }
        else if(currentValue >= 8000)
        {
            audio = "sounds" + File.separator + "8,000_question.wav";
        }
        else
        {
            audio = "sounds" + File.separator + "500_question.wav";
        }
        
        soundPlayer = new SoundPlayer(audio);
        soundPlayer.start();
        
        displayQuestion(questions.get(currentIndex));
    }
    
    // setQuestions method gets the 4 tier questions from the database,
    // suffles them, then adds the first 3 quesitons from each tier array
    // to the master question list that will be used for the game.
    public void setQuestions()
    {
        questions = new ArrayList<>();
        
        // initialising the 4 difficulty tier arrays
        ArrayList<Question> t1 = dbm.getTierQuestions(1);
        ArrayList<Question> t2 = dbm.getTierQuestions(2);
        ArrayList<Question> t3 = dbm.getTierQuestions(3);
        ArrayList<Question> t4 = dbm.getTierQuestions(4);
            
        Collections.shuffle(t1);
        Collections.shuffle(t2);
        Collections.shuffle(t3);
        Collections.shuffle(t4);

        questions.add(t1.get(0));
        questions.add(t1.get(1));
        questions.add(t1.get(2));
        questions.add(t2.get(0));
        questions.add(t2.get(1));
        questions.add(t2.get(2));
        questions.add(t3.get(0));
        questions.add(t3.get(1));
        questions.add(t3.get(2));
        questions.add(t4.get(0));
        questions.add(t4.get(1));
        questions.add(t4.get(2));
    }
    
    // initValues method adds the value of each question to the values
    // array, corresponding to its position in the array.
    public void initValues()
    {
        values = new ArrayList<>();
        
        values.add(500);
        values.add(1000);
        values.add(2000);
        values.add(4000);
        values.add(8000);
        values.add(16000);
        values.add(32000);
        values.add(64000);
        values.add(125000);
        values.add(250000);
        values.add(500000);
        values.add(1000000);        
    }
    
    // displayQuestion method shuffles the answers from the current question
    // and then passes the current question and the shuffled answers to the
    // frontend
    public void displayQuestion(Question question)
    {
        ArrayList<String> answers = question.getAnswers();
        
        // New ArrayList is made to hold values of the origional array but
        // in a different memory space. Maintaining the integrity of the origional
        // data
        ArrayList<String> shuffledAnswers = new ArrayList<>();
        
        for(String answer : answers)
        {
           shuffledAnswers.add(answer);
        }
        
        String correctAnswerString = answers.get(0);
        
        Collections.shuffle(shuffledAnswers);
        this.correctAnswer = String.valueOf((char)(shuffledAnswers.indexOf(correctAnswerString) + 'a'));
        
        frontend.displayQuestion(question.getQuestion(), shuffledAnswers);
    }
    
    // lockIn method plays the lock in sound and starts a new LockedInTimer thread
    public void lockIn(char userAnswer)
    {
        soundPlayer.finish();
        soundPlayer = new SoundPlayer("sounds" + File.separator + "final_answer.wav");
        soundPlayer.start();
        this.userAnswer = userAnswer;
        LockedInTimer lit = new LockedInTimer(this);
        lit.start();
    }
    
    // lockedInTimerDone method is called by the LockedInTimer when it's timer
    // is finished. Then the frontend is updated with the correct answer being
    // highlighted.
    // A new AnswerRevealTimer thread is started, and then the user answer is
    // compared to the correct answer.
    // Appropiate messages and sounds are played if the user was right or not
    public void lockedInTimerDone()
    {
        char answer = correctAnswer.charAt(0);
        soundPlayer.finish();
        
        frontend.answerOverlay(answer, true);
        
        AnswerRevealTimer art = new AnswerRevealTimer(this);
        art.start();
        
        if(userAnswer != answer)
        {
            playing = false;
            soundPlayer = new SoundPlayer("sounds" + File.separator + "lose.wav");
            soundPlayer.start();
            
            String winnings = getGuaranteedWinnings();
            String message = "I'm sorry, that is incorrect!";
            
            if(winnings.equals(""))
            {
                message += " You walk away enpty handed!";
                JOptionPane.showMessageDialog(frontend, message, "Incorrect Answer", JOptionPane.ERROR_MESSAGE);
            }
            else
            {                
                message += " But you still walk away with $" + winnings;
                JOptionPane.showMessageDialog(frontend, message, "Incorrect Answer", JOptionPane.WARNING_MESSAGE);
            }
            
        }
        else if(currentIndex != 11)
        {
            updateIndexes();
            soundPlayer = new SoundPlayer("sounds" + File.separator + "correct_answer.wav");
            soundPlayer.start();
        }
        else
        {
            updateIndexes();
            playing = false;
            soundPlayer = new SoundPlayer("sounds" + File.separator + "1,000,000_win.wav");
            soundPlayer.start();
            
            String message = "CONGRATULATIONS! You have won $1,000,000!";
            JOptionPane.showMessageDialog(frontend, message, "Congratulations", JOptionPane.INFORMATION_MESSAGE);
        }       
    }
    
    // AnswerRevealTimerDone method is called by the AnswerRevealTimer thread 
    // once it's timer is finished.
    public void answerRevealTimerDone()
    {        
        if(playing)
        {
            soundPlayer.finish();
            ++currentIndex;
            play();
        }
    }
    
    // getGuaranteedWinnings method returns the string value of the 
    // guaranteed winning the user gets
    public String getGuaranteedWinnings()
    {
        String winnings = "";
        
        if(currentIndex > 3)
        {            
            if(currentIndex > 7)
            {
                winnings = String.valueOf(values.get(7));
            }
            else 
            {
                winnings = String.valueOf(values.get(3));
            }
        }
        
        return winnings;
    }
    
    // updateIndexes updates the front end with the indexes of the quesitons
    // they have got correct.
    private void updateIndexes()
    {
        if(playing)
        {
            int[] indexes = new int[currentIndex + 1];
            
            for(int i = 0; i < indexes.length; ++i)
            {
                indexes[i] = 11 - i;
            }
            
            frontend.updateValueListIndexes(indexes);
        }
    }
    
    // walkAwayClicked method shows a message to the user with the money they
    // have won, and plays appropriate sounds
    public void walkAwayClicked()
    {
        playing = false;        
        soundPlayer.finish();       
        
        String message = "";
        
        if(currentIndex != 0)
        {
            message = "You walk away with $" + values.get(currentIndex - 1);
            soundPlayer = new SoundPlayer("sounds" + File.separator + "win.wav");
        }
        else
        {
            message = "Not even the first question huh..?";
            soundPlayer = new SoundPlayer("sounds" + File.separator + "lose.wav");
        }
        
        soundPlayer.start();
        JOptionPane.showMessageDialog(frontend, message, "Walking Away", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
    
    // fiftyFiftyClicked handles removing two wrong answers from the passed
    // answer set and updates the frontend
    public void fiftyFiftyClicked(ArrayList<String> answers)
    {           
        Random rand = new Random();
        int correctIndex = (int)(correctAnswer.toCharArray()[0] - 'a');
        int randomIndex;

        for(int i = 0; i < 2; ++i)
        {
            do
            {
                randomIndex = rand.nextInt(4);
            }
            while(answers.get(randomIndex).equals("") || randomIndex == correctIndex);

            answers.set(randomIndex, "");
        }

        fifty = false;
        frontend.updateAnswers(answers);
    }
    
    // askAudienceClicked method handles generating audience interation with 
    // the current question and updates the frontend with the percents of 
    // selection for each answer
    public void askAudienceClicked(ArrayList<String> answers)
    {
        Random rand = new Random();
        int level = Integer.parseInt(questions.get(currentIndex).getDifficulty());
        int modifier = 5 - level;
        int lowest = modifier * 10;

        int rightPercent = rand.nextInt(lowest) + lowest;
        int[] wrongPercent = new int[3];
        wrongPercent[0] = rand.nextInt(100 - rightPercent);
        wrongPercent[1] = rand.nextInt(100 - (rightPercent + wrongPercent[0]));
        wrongPercent[2] = 100 - (rightPercent + wrongPercent[0] + wrongPercent[2]);
        
        int[] percents = new int[4];

        int correctIndex = (int)(correctAnswer.toCharArray()[0] - 'a');
        int wrongCounter = 0;
        for(int i = 0; i < 4; ++i)
        {
            if(i == correctIndex)
            {
                percents[i] = rightPercent;
            }
            else if(!answers.get(i).equals(""))
            {
                percents[i] = wrongPercent[wrongCounter];
                ++wrongCounter;
            }
        }

        frontend.audienceAsked(percents);
    }
    
    public ArrayList<Question> getQuestions()
    {
        return questions;
    }
    
    public ArrayList<Integer> getValues()
    {
        return values;
    }
    
    public void quitGame()
    {
        frame.dispose();
        soundPlayer.finish();        
    }
    
    public GameScreen getFrontend()
    {
        return frontend;
    }
}
