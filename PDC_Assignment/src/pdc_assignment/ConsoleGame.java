package pdc_assignment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;


public class ConsoleGame {
    
    private ArrayList<Question> questions;
    private ArrayList<Integer> value;
    private String correctAnswer;
    private int currentIndex;
    private boolean fifty;  // Flag for "50-50" Life-Line
    private boolean askAudience;    // Flag for "Ask the Audience" Life-Line
    
    public ConsoleGame()
    {
        currentIndex = 0;
        fifty = true;
        askAudience = true;
        correctAnswer = "-1";
        initValue();
    }
    
    public void play()
    {
        boolean playing = true;                
        Scanner scan = new Scanner(System.in);
        
        if(checkSaveFile())
        {
            loadSaveFile();
            System.out.println("Welcome Back!");
        }
        else
        {
            setQuestions();
            startMessages();
            scan.nextLine(); 
        }
                
        while(playing)
        {
            ArrayList<String> answers;
            System.out.println("===================================================");
            
            if(currentIndex == 0)
            {
                System.out.println("Here's your first question");
            }
            else if(currentIndex == 11)
            {
                System.out.println("Final Question!");
            }
            else
            {
                System.out.println("Question " + String.valueOf(currentIndex + 1));
            }
            
            printValue();
            printLifelines();
            answers = printQuestion(questions.get(currentIndex));
            
            playing = manageInput(answers);
            System.out.println("");
        }
        
        System.out.println("Thank you for playing!");
    }
    
    // SartMessages prints out messages to the use when they start a new game
    private void startMessages()
    {
        System.out.println("Welcome to...");
        System.out.println("WHO WANTS TO BE A MILLIONARE!!\n");
        System.out.println("The game is simple, answer 12 questions correctly and win $1,000,000\n");
        System.out.println("Due to budget cuts, you only get 2 Life-Lines: 50-50 and Ask The Audience");
        System.out.println("Every 4 questions you answer correctly, the value of that question will be saved.");
        System.out.println("Guaranteeing you that sum of money");
                
        System.out.println("\nAt any time you may enter 'q' to quit the game, or 's' to save and quit");
        System.out.println("\nPress ENTER to start!");
    }
    
    // checkSaveFile checks if a save file exists, then queries the user if they
    // want to continue the game, or start a new game.
    // Returns: true if user wants to continue. false if they want a new game
    private boolean checkSaveFile()
    {
        Scanner scan = new Scanner(System.in);
        boolean loadSave = false;
        File file = new File("save.dat");
        
        if(file.exists())
        {
            System.out.println("Do you wish to load saved game (1), or start a new game (2)?");
            System.out.println("Warning: starting a new game will erase the save file...");
            
            boolean invalidInput = true;
            String input = "";
            
            while(invalidInput)
            {
                input = scan.nextLine();
                if(input.equals("1") || input.equals("2"))
                {
                    invalidInput = false;
                }
            }
            
            if(input.equals("1"))
            {
                loadSave = true;
            }
            else
            {
                file.delete();
            }
        }
        
        return loadSave;
    }
    
    // manageInput handles input from the user after the question has been asked
    public boolean manageInput(ArrayList<String> answers)
    {
        Scanner scan = new Scanner(System.in);
        boolean validInput;
        boolean playing = true;
        
        // Loops while the input is invalid
        do
        {
            validInput = true;
            String input = scan.nextLine().trim();

            if(input.equalsIgnoreCase("s"))
            {
                saveGame();
                playing = false;
            }
            else if(input.equalsIgnoreCase("q"))
            {
                playing = false;
            }
            else if(input.equalsIgnoreCase("a") || input.equalsIgnoreCase("b") || 
                    input.equalsIgnoreCase("c") || input.equalsIgnoreCase("d"))
            {
                System.out.println("Locking in " + input + "...");
                Timer.wait(2000);
                if(input.equalsIgnoreCase(correctAnswer))
                {
                    if(currentIndex == 11)
                    {
                        System.out.println("CONGRATULATIONS!");
                        System.out.println("You Just Won $1,000,000!");
                        playing = false;
                    }
                    else
                    {
                        System.out.println("That is correct!");
                        playing = queryPlayOrWalk();

                        if(playing)
                        {
                            ++currentIndex;
                        }
                    }
                }
                else
                {
                    System.out.println("Im sorry, that is incorrect. For you, the game is over.");
                    printGuaranteedWinnings();
                    playing = false;
                }
            }
            else if(input.equals("1") || input.equals("2"))
            {
                if(input.equals("1") && fifty)
                {
                    fiftyFifty(answers);
                    playing = manageInput(answers);
                }
                else if(input.equals("2") && askAudience)
                {
                    askAudience(answers);
                    playing = manageInput(answers);
                }
                else
                {
                    validInput = false;
                }
            }
            else
            {
                validInput = false;                
            }
            
            if(!validInput)
            {
                System.out.println("Invalid Input. Please retry...");
            }
        }
        while(!validInput);

        return playing;
    }
    
    // delayedPrint pauses before printing the output, for dramatic effect
    private void delayedPrint(String output)
    {        
        Timer.wait(800);
        System.out.print(output);
    }
    
    // setQuestions reads the questions from the questions.txt file and adds 
    // them to the corresponding tier array depending on their difficulty tier.
    // Then each array is shuffled and 3 questions from each difficulty tier
    // array are added to the questions array, which is the final question list
    // the user will be asked.
    private void setQuestions()
    {
        questions = new ArrayList<>();
        
        // initialising the 4 difficulty tier arrays
        ArrayList<Question> t1 = new ArrayList<>();
        ArrayList<Question> t2 = new ArrayList<>();
        ArrayList<Question> t3 = new ArrayList<>();
        ArrayList<Question> t4 = new ArrayList<>();
        
        try
        {
            File file = new File("questions.txt");
            Scanner scan = new Scanner(new FileInputStream(file));
            
            while(scan.hasNext())
            {
                String line = scan.nextLine();
                String[] splitLine = line.split("_");
                
                if(splitLine.length != 6)
                {
                    continue;                    
                }
                
                ArrayList<String> answers = new ArrayList<>();
                for(int i = 1; i <= 4; ++i)
                {
                    answers.add(splitLine[i].trim());
                }
                
                Question question = new Question(splitLine[0], answers, splitLine[5]);
                
                switch(splitLine[5].trim())
                {
                    case "1":
                        t1.add(question);
                        break;
                    case "2":
                        t2.add(question);
                        break;
                    case "3":
                        t3.add(question);
                        break;
                    case "4":
                        t4.add(question);
                        break;
                    default:
                        System.out.println("invalid difficulty level " + splitLine[5] + "for questions: " + splitLine[0]);
                }
            }
            
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
        catch(IOException ioe)
        {
            System.out.println("Something went wrong with reading file...");
            System.out.println(ioe);
        }
    }
    
    // initValue method holds the value of each question corresponding to 
    // the position in the array
    private void initValue()
    {
        value = new ArrayList<>();
        
        value.add(500);
        value.add(1000);
        value.add(2000);
        value.add(4000);
        value.add(8000);
        value.add(16000);
        value.add(32000);
        value.add(64000);
        value.add(125000);
        value.add(250000);
        value.add(500000);
        value.add(1000000);        
    }
    
    // printQuestion handles printing the question, and keeping track of
    // the correct answer
    private ArrayList<String> printQuestion(Question question)
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
//        System.out.println("Correct answer: " + correctAnswerString);
        
        Collections.shuffle(shuffledAnswers);
        this.correctAnswer = String.valueOf((char)(shuffledAnswers.indexOf(correctAnswerString) + 'a'));
        
        System.out.println(question.getQuestion());
        printAnswers(shuffledAnswers);
        
        return shuffledAnswers;
    }
    
    // Prints the answers to the console with appropriate formatting
    private void printAnswers(ArrayList<String> answers)
    {
        System.out.print("a) ");
        delayedPrint(answers.get(0));
        delayedPrint("\nb) ");
        delayedPrint(answers.get(1) + "\n");
        delayedPrint("c) ");
        delayedPrint(answers.get(2));
        delayedPrint("\nd) ");
        delayedPrint(answers.get(3) + "\n");
    }
    
    // printValue prints the current questions value
    private void printValue()
    {
        System.out.println("This question is worth $" + value.get(currentIndex));
    }
    
    // queryPlayOrWalk queries the user if they want to keep playing or if
    // they want to take the money
    private boolean queryPlayOrWalk()
    {
        boolean keepPlaying = true;
        boolean validInput;
        Scanner scan = new Scanner(System.in);
        
        do
        {
            validInput = true;
            System.out.println("Do you want to walk away with they money (1), or face the next quesiton? (2)");
            String input = scan.nextLine().trim();
            
            if(input.equals("1"))
            {
                System.out.println("Heres your cheque for $" + value.get(currentIndex));
                keepPlaying = false;
            }
            else if(!input.equals("2"))
            {
                validInput = false;
            }
        }
        while(!validInput);
        
        return keepPlaying;
    }
    
    public void printGuaranteedWinnings()
    {
        if(currentIndex > 3)
        {
            System.out.print("You still get to walk away with $");
            
            if(currentIndex > 7)
            {
                System.out.println(value.get(7));
            }
            else 
            {
                System.out.println(value.get(3));
            }
        }
        else
        {
            System.out.println("Im sorry to say you have to walk away empty handed.");
        }
    }
    
    private void printLifelines()
    {
        System.out.println("---------------------------------------------------");
        System.out.print("Life-Lines:\t");
        
        if(fifty || askAudience)
        {          
            if(fifty)
            {
                System.out.print("50-50 (1)\t");
            }
            
            if(askAudience)
            {
                System.out.print("Ask the Audence (2)");
            }
        }
        else
        {
            System.out.print("None");
        }
        System.out.println("\n---------------------------------------------------");
    }
    
    // fiftyFifty removes 2 wrong answers from the possible 4 answers
    private void fiftyFifty(ArrayList<String> answers)
    {
        if(fifty)
        {
            System.out.println("We will eliminate 2 wrong answers...");
            System.out.println("Leaving 1 wrong answer, and the right answer...");
            
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
            printAnswers(answers);
        }
    }
    
    // askAudience roughly simulates asking the audience to answer the question
    // Probability of the audience being right drops as the difficulty tier
    // increases
    private void askAudience(ArrayList<String> answers)
    {
        if(askAudience)
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

            int correctIndex = (int)(correctAnswer.toCharArray()[0] - 'a');
            int wrongCounter = 0;
            for(int i = 0; i < 4; ++i)
            {
                if(i == correctIndex)
                {
                    answers.set(i, String.valueOf(rightPercent + "%"));
                }
                else if(!answers.get(i).equals(""))
                {
                    answers.set(i, String.valueOf(wrongPercent[wrongCounter] + "%"));
                    ++wrongCounter;
                }
            }

            System.out.println("Okay audience, please enter your answer into your keypads now...");
            Timer.wait(3000);
            System.out.println("Lets hope they're as smart as they look.");
            
            printAnswers(answers);
            askAudience = false;
        }
    }
    
    private void saveGame()
    {
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("save.dat")));
            oos.writeObject(questions);
            oos.writeInt(currentIndex);
            oos.writeBoolean(fifty);
            oos.writeBoolean(askAudience);
            
            oos.flush();
            oos.close();
            System.out.println("\nGame Sucessfully Saved..");
        }
        catch(IOException ioe)
        {
            System.out.println("Error: Problem Saving File");
            System.out.println(ioe);
        }
    }
    
    private void loadSaveFile()
    {
        try
        {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("save.dat")));
            questions = (ArrayList<Question>)ois.readObject();
            currentIndex = ois.readInt();
            fifty = ois.readBoolean();
            askAudience = ois.readBoolean();
            
            ois.close();
            System.out.println("Save Loaded..\n");
        }
        catch(IOException | ClassNotFoundException e)
        {
            System.out.println("Error: Problem Loading File");
            System.out.println(e);
        }
    }
}
