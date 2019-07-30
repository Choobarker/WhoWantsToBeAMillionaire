package pdc_assignment;

import java.io.Serializable;
import java.util.ArrayList;


public class Question implements Serializable{
    
    private String question;
    private ArrayList<String> answers;
    private String correctAnswer;
    private String difficulty;
    
    public Question(String question, ArrayList<String> answers, String difficulty)
    {
        this.question = question;
        this.answers = answers;
        this.difficulty = difficulty;
        correctAnswer = answers.get(0);
    }
    
    public String getQuestion()
    {
        return question;
    }
    
    public ArrayList<String> getAnswers()
    {
        return answers;
    }
    
    public String getDifficulty()
    {
        return difficulty;
    }
    
    public boolean checkAnswer(String answer)
    {
        if(answer.equals(correctAnswer))
        {
            return true;
        }
        return false;
    }

}
