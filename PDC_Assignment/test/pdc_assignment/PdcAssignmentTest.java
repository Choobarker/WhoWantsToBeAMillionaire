package pdc_assignment;

import java.util.ArrayList;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PdcAssignmentTest 
{
    private static GUIGame backend;
    private static GameScreen frontend;
    
    @BeforeClass
    public static void setUpClass()
    {
        backend = new GUIGame();
        frontend = backend.getFrontend();
    }
    
    @AfterClass
    public static void tearDownClass()
    {
        backend.quitGame();
    }
    
    @Before
    public void setUp()
    {
        
    }
    
    @Before
    public void tearDown()
    {
        
    }
    
    @Test
    public void testInitialiseQuestions()
    {
        int expected = 12;
        backend.setQuestions();        
        ArrayList<Question> questions = backend.getQuestions();
        
        assert(expected == questions.size());
    }
    @Test
    public void testInitialiseValues()
    {
        int expected = 12;
        backend.initValues();
        ArrayList<Integer> values = backend.getValues();
        
        assert(expected == values.size());
    }
    
    @Test
    public void testDisplayQuestion()
    {
        String testQuestion = "test question";
        
        ArrayList<String> testAnswers = new ArrayList<>();
        
        testAnswers.add("correct");
        testAnswers.add("wrong1");
        testAnswers.add("wrong2");
        testAnswers.add("wrong3");
        
        Question tQuestion = new Question(testQuestion, testAnswers, "1");
        
        backend.displayQuestion(tQuestion);
        
        if(!testQuestion.equals(frontend.getQuestion()))
        {
            fail();
        }
        
        ArrayList<String> actualAnswers = frontend.getAnswers();
        
        for(String tAnswer : testAnswers)
        {
            int result = actualAnswers.indexOf(tAnswer);
            
            if(result == -1)
            {
                fail();
            }
        }
    }
            
}
