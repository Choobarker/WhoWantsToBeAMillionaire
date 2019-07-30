package pdc_assignment;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// DBManager class handles interaction with the programs database
public class DBManager 
{
    private Connection connection = null;
    private String url = "jdbc:derby:GameDB; create=true";
    private String username = "";
    private String password = "";
    private String questionTable = "question";
    
    public DBManager()
    {
        if(createConnection())
        {
            createQuestionTable();
        }
    }
    
    private boolean createConnection()
    {
        boolean connected = false;
        
        try
        {
            connection = DriverManager.getConnection(url, username, password);     
            connected = true;
        }
        catch(SQLException e)
        {
            System.out.println("SQLException: " + e.getMessage());
        }
        
        return connected;
    }
    
    // createQuestionTable method checks the database to see if the quesiton
    // table is already existing. If it doesn't exist, the table is created
    // then populated with required data.
    private void createQuestionTable()
    {
        try
        {
            String table = "question";
            
//            Statement drop = connection.createStatement();
//            drop.executeUpdate("DROP TABLE question");
            
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getTables(null, null, "QUESTION", null);
                                    
            if(!rs.next())
            {
                // table needs creating
                String sql = "create table " + table + "(id int, question varchar(100)";
                sql += ", correct_answer varchar(30), wrong_answer1 varchar(30), wrong_answer2 varchar(30)";
                sql += ", wrong_answer3 varchar(30), difficulty int)";
                
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
                System.out.println(table + " created");
                
                populateQuestionTable();
                checkTable();
            }
            else
            {
                // Table already exists
            }
            
            connection.close();
        }
        catch(SQLException e)
        {
            System.out.println("SQLException: " + e.getMessage());
        }
    }
    
    // pupulateQuestionTable method is responsible for inserting the required 
    // data into the question table of the game database.
    private void populateQuestionTable() throws SQLException
    {
        int id = 0;
        String table = "question";
        
        Statement statement = connection.createStatement();
        
        String sql = "INSERT INTO " + table + " VALUES(" + id++ + ",'Which is the largest planet in our solar system?'";
        sql += ",'Jupiter', 'Earth', 'The Sun', 'Pluto', 1)";
        statement.addBatch(sql);
        
        sql = "INSERT INTO " + table + " VALUES(" + id++ + ",'What is water made of?'";
        sql += ",'Oxygen and Hydrogen', 'Carbon and Helium', 'Carbon and Oxygen', 'Rain', 1)";
        statement.addBatch(sql);
        
        sql = "INSERT INTO " + table + " VALUES(" + id++ + ",'What makes plants look green?'";
        sql += ",'Chlorophyll', 'Photosynthesis', 'Algae', 'Chloroplasts', 1)";
        statement.addBatch(sql);
        
        sql = "INSERT INTO " + table + " VALUES(" + id++ + ",'What is the second largest country by land mass?'";
        sql += ",'Canada', 'Russia', 'China', 'USA', 2)";
        statement.addBatch(sql);
        
        sql = "INSERT INTO " + table + " VALUES(" + id++ + ",'What is the second most abundant element in the atmosphere?'";
        sql += ",'Oxygen', 'Hydrogen', 'Helium', 'Nitrogen', 2)";
        statement.addBatch(sql);
        
        sql = "INSERT INTO " + table + " VALUES(" + id++ + ",'What was the first animal to orbit the Earth alive?'";
        sql += ",'Dog', 'Ape', 'Human', 'Cockroach', 2)";
        statement.addBatch(sql);
        
        sql = "INSERT INTO " + table + " VALUES(" + id++ + ",'What percent of people live in the Northern Hemisphere?'";
        sql += ",'90%', '80%', '70%', '60%', 3)";
        statement.addBatch(sql);
        
        sql = "INSERT INTO " + table + " VALUES(" + id++ + ",'Samite is a type of what?'";
        sql += ",'Fabric', 'Stone', 'Dog', 'Cake', 3)";
        statement.addBatch(sql);
        
        sql = "INSERT INTO " + table + " VALUES(" + id++ + ",'In anatomy, \"Plantar\" relates to which part of the human body?'";
        sql += ",'Foot', 'Stomach', 'Head', 'Hand', 3)";
        statement.addBatch(sql);
        
        sql = "INSERT INTO " + table + " VALUES(" + id++ + ",'Which of these characters turned 40 years old in 1990?'";
        sql += ",'Charlie Brown', 'Bugs Bunny', 'Mickey Mouse', 'Fred Flintstone', 4)";
        statement.addBatch(sql);
        
        sql = "INSERT INTO " + table + " VALUES(" + id++ + ",'Napoleon suffered defeat at Waterloo in what year?'";
        sql += ",'1815', '1816', '1817', '1818', 4)";
        statement.addBatch(sql);
        
        sql = "INSERT INTO " + table + " VALUES(" + id++ + ",'What does the \"D\" in \"D-Day\" stand for?'";
        sql += ",'Day', 'Dooms', 'Death', 'Dunkirk', 4)";
        statement.addBatch(sql);
        
        statement.executeBatch();
        System.out.println("question populated");
    }
    
    // checkTable method is used to display the contents of the questions table.s
    private void checkTable() throws SQLException
    {
        Statement statement = connection.createStatement();
        String sql = "SELECT * FROM question";
        
        ResultSet rs = statement.executeQuery(sql);
        
        while(rs.next())
        {
            System.out.println(rs.getInt("id"));
            System.out.println(rs.getString("question"));
            System.out.println(rs.getString("correct_answer"));
            System.out.println(rs.getString("wrong_answer1"));
            System.out.println(rs.getString("wrong_answer2"));
            System.out.println(rs.getString("wrong_answer3"));
        }
    }
    
    // getTierQuestions method returns an array of all the questions in the
    // question table that have the tier level of the passed argument
    public ArrayList<Question> getTierQuestions(int tier)
    {
        ArrayList<Question> questions = new ArrayList<>();
        
        if(createConnection())
        {
            try
            {
                Statement statement = connection.createStatement();
                String sql = "SELECT * FROM " + questionTable + " WHERE difficulty = " + tier;

                ResultSet rs = statement.executeQuery(sql);

                while(rs.next())
                {
                    String question = rs.getString("question");
                    ArrayList<String> answers = new ArrayList<>();
                    answers.add(rs.getString("correct_answer"));
                    answers.add(rs.getString("wrong_answer1"));
                    answers.add(rs.getString("wrong_answer2"));
                    answers.add(rs.getString("wrong_answer3"));

                    Question q = new Question(question, answers, String.valueOf(tier));
                    questions.add(q);
                }

                return questions;
            }
            catch(SQLException e)
            {
                System.out.println("getT1Questions() SQLException: " + e.getMessage());
            }
        }
        
        return null;
    }
}
