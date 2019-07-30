package pdc_assignment;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

// GameScreen class is the frontend of the game
public class GameScreen extends JPanel implements MouseListener, MouseMotionListener
{
    private GUIGame backend;
    
    private char selectedAnswer = 0;    
    
    private int mouseX = 0;
    private int mouseY = 0;
    private int overlayX = 0;
    private int overlayY = 0;
    private int realOverlayX = 0;
    private int realOverlayY = 0;
    private int[] percents;
    
    private boolean lockedIn = false;
    private boolean inAnswerBox = false;
    private boolean answerOverlay = false;
    private boolean realAnswerOverlay = false;
    private boolean audienceAsked = false;
    
    private JLabel lblA, lblB, lblC, lblD;
    private JLabel answerA, answerB, answerC, answerD;
    private JLabel question;
    private JList valueList;
    private DefaultListModel listModel;
    private MyButton walkAwayButton;
    private MyButton fiftyFifty;
    private MyButton askAudience;
    
    private Color labelColour;
    
    private NumberFormat numberFormat;
    
    public GameScreen(GUIGame backend)
    {
        super(null);
        this.backend = backend;
        
        numberFormat = new DecimalFormat("#,###,###");
        initComponents();
    }
    
    private void initComponents()
    {
        Dimension size = new Dimension(1000, 550);
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
        
        addMouseListener(this);
        addMouseMotionListener(this);

        Font font;
        labelColour = new Color(255, 210, 0);

        lblA = new JLabel("A:");
        font = lblA.getFont().deriveFont(20f);
        lblA.setFont(font);
        lblA.setForeground(labelColour);
        lblA.setLocation(168, 433);
        lblA.setSize(21, 18);
        add(lblA);
        
        answerA = new JLabel("", SwingConstants.CENTER);
        answerA.setFont(font);
        answerA.setForeground(Color.white);        
        answerA.setLocation(178, 433);
        answerA.setSize(278, 25);
        answerA.setBackground(Color.red);
        add(answerA);
        
        lblB = new JLabel("B:");
        lblB.setFont(font);
        lblB.setForeground(labelColour);
        lblB.setLocation(532, 433);
        lblB.setSize(21, 21);
        add(lblB);
        
        answerB = new JLabel("", SwingConstants.CENTER);
        answerB.setFont(font);
        answerB.setForeground(Color.white);
        answerB.setLocation(542, 433);
        answerB.setSize(278, 25);
        add(answerB);
        
        lblC = new JLabel("C:");
        lblC.setFont(font);
        lblC.setForeground(labelColour);
        lblC.setLocation(168, 503);
        lblC.setSize(21, 19);
        add(lblC);
        
        answerC = new JLabel("", SwingConstants.CENTER);
        answerC.setFont(font);
        answerC.setForeground(Color.white);
        answerC.setLocation(178, 503);
        answerC.setSize(287 , 25);
        add(answerC);
        
        lblD = new JLabel("D:");
        lblD.setFont(font);
        lblD.setForeground(labelColour);
        lblD.setLocation(532, 503);
        lblD.setSize(21, 18);
        add(lblD);
        
        answerD = new JLabel("", SwingConstants.CENTER);
        answerD.setFont(font);
        answerD.setForeground(Color.white);
        answerD.setLocation(542, 503);
        answerD.setSize(287, 25);
        add(answerD);
        
        question = new JLabel("", SwingConstants.CENTER);
        question.setFont(font.deriveFont(19f));
        question.setForeground(Color.white);
        question.setLocation(188, 330);
        question.setSize(618, 30);
        add(question);
        
        valueList = new JList();
        valueList.setLocation(863, 291);
        valueList.setSize(115, 220);
        valueList.setBackground(new Color(28, 39, 51));
        valueList.setForeground(Color.white);
        valueList.setBorder(new LineBorder(labelColour, 2));
        valueList.addMouseListener(null);
        valueList.addListSelectionListener(null);
        valueList.setSelectionBackground(new Color(116, 183, 44));
        valueList.setSelectionForeground(labelColour);
        valueList.setEnabled(false);
        valueList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        
        valueList.setCellRenderer(new DefaultListCellRenderer() 
        {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                this.setEnabled(true);
                return this;
            }
    });        
        add(valueList);
        
        walkAwayButton = new MyButton();
        walkAwayButton.setSize(137, 24);
        walkAwayButton.setLocation(849, 246);
        walkAwayButton.addMouseListener(this);
        walkAwayButton.updateSkin(new ImageIcon("walkAway.png"));
        add(walkAwayButton);
        
        fiftyFifty = new MyButton();
        fiftyFifty.setSize(68, 43);
        fiftyFifty.setLocation(187, 237);
        fiftyFifty.addMouseListener(this);
        fiftyFifty.updateSkin(new ImageIcon("fiftyFifty.png"));
        add(fiftyFifty);
        
        askAudience = new MyButton();
        askAudience.setSize(68, 43);
        askAudience.setLocation(265, 237);
        askAudience.addMouseListener(this);
        askAudience.updateSkin(new ImageIcon("askAudience.png"));
        add(askAudience);
    }
    
    // setListModel handles setting up the list of values for each question
    // for the user to see
    public void setListModel(ArrayList<Integer> values)
    {
        listModel = new DefaultListModel();
        int number = 1;
        
        for(int value : values)
        {
            if(value == 4000 || value == 64000)
            {
                listModel.add(0, number++ + ".      ** $" + numberFormat.format(value) + " **");
            }
            else
            {
                listModel.add(0, number++ + ".      $" + numberFormat.format(value));
            }            
        }
        
        valueList.setModel(listModel);       
    }
    
    // updateValueListIndexes method updates the selected values in the list
    // representing the questions they have answered correctly
    public void updateValueListIndexes(int[] indexes)
    {        
        valueList.setSelectedIndices(indexes);
        
    }
    
    //updateAnswers method updates the answer lables with the passed array of
    // answers
    public void updateAnswers(ArrayList<String> answers)
    {
        answerA.setText(answers.get(0));
        answerB.setText(answers.get(1));
        answerC.setText(answers.get(2));
        answerD.setText(answers.get(3));
    }
    
    // audienceAsked is responsible for setting the audienceAsked flag and 
    // setting the value of the percents array with the passed array
    public void audienceAsked(int[] percents)
    {
        this.percents = percents;
        audienceAsked = true;
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        ImageIcon studio = new ImageIcon("studio2.png");
        studio.paintIcon(this, g, 0, 0);
        
        if(lockedIn)
        {
//            backend.lockIn();
        }
        
        if(answerOverlay) // if user has selected an answer
        {
            ImageIcon overlay = new ImageIcon("locked_in.png");
            overlay.paintIcon(this, g, overlayX, overlayY);
        }
        
        if(realAnswerOverlay) // if real answer needs to be revealed
        {
            ImageIcon overlay = new ImageIcon("correct.png");
            overlay.paintIcon(this, g, realOverlayX, realOverlayY);
        }
        
        // code generates a graph on screen representing the audience guessing
        // the correct answer of the current question
        if(audienceAsked)
        {
            int startX = 445;
            int startY = 250;
            int width = 39;
            int height = 0;
            int gap = 12;
            
            g.setColor(new Color(28, 39, 51));
            g.fillRect(startX - 10, startY - 200, 212, 220);
            g.setColor(labelColour);
            g.drawRect(startX - 10, startY - 200, 212, 220);
            
            
            for(int i = 0; i < 4; ++i)
            {
                height = (percents[i] + 1) * 2;
                
                g.setColor(new Color(222, 106, 106));
                g.fillRect(startX, startY - height, width, height);
                g.setColor(new Color(157, 0, 0));
                g.drawRect(startX, startY - height, width, height);
                
                startX += width + gap;
            }
            
            g.setColor(Color.white);
            g.drawString("A", 462, 264);
            g.drawString("B", 513, 264);
            g.drawString("C", 564, 264);
            g.drawString("D", 615, 264);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        Object source = e.getSource();
        
        if(!lockedIn)
        {
            if(selectedAnswer != 0) // if the user clicked on an answer
            {
                inAnswerBox = false;
                lockedIn = true;
                lockIn();
            }
            else if(source == walkAwayButton)
            {
                walkAwayButton.updateSkin(new ImageIcon("walkAwayClicked.png"));
                setDefaultCursor();
                backend.walkAwayClicked();
            }
            else if(source == fiftyFifty || source == askAudience) // if lifeline clicked
            {
                setDefaultCursor();
                
                ArrayList<String> answers = new ArrayList<>();
                answers.add(answerA.getText());
                answers.add(answerB.getText());
                answers.add(answerC.getText());
                answers.add(answerD.getText());
                
                if(source == fiftyFifty)
                {
                    backend.fiftyFiftyClicked(answers);
                    fiftyFifty.removeMouseListener(this);
                    fiftyFifty.updateSkin(new ImageIcon("fiftyFiftyClicked.png"));
                }
                else
                {
                    backend.askAudienceClicked(answers);
                    askAudience.removeMouseListener(this);
                    askAudience.updateSkin(new ImageIcon("askAudienceClicked.png"));
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) 
    {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) 
    {
        Object source = e.getSource();
        
        if(source == walkAwayButton || source == fiftyFifty || source == askAudience)
        {
            if(!lockedIn)
            {
                setHandCursor();
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) 
    {
        Object source = e.getSource();
        
        if(source == walkAwayButton || source == fiftyFifty || source == askAudience)
        {
            setDefaultCursor();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) 
    {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) 
    {     
        Point p = e.getPoint();
        mouseX = p.x;
        mouseY = p.y;
        
        if(!lockedIn)
        {
            inAnswerBox = true;
            
            if(inA())
            {
                selectedAnswer = 'a';
            }
            else if(inB())
            {
                selectedAnswer = 'b';
            }
            else if(inC())
            {
                selectedAnswer = 'c';
            }
            else if(inD())
            {
                selectedAnswer = 'd';
            }
            else
            {
                selectedAnswer = 0;
                inAnswerBox = false;
            }
        }
        
        if(inAnswerBox)
        {
            setHandCursor();
        }
        else
        {
            setDefaultCursor();
        }
        
        repaint();
    }
    
    // checks if mouse is over answer A
    private boolean inA()
    {
        boolean inA = false;
        
        if((mouseX >= 178 && mouseX <= 456) && (mouseY >= 416 && mouseY <= 473))
        {
            inA = true;
        }
        
        return inA;
    }
    
    // checks if mouse is over answer B
    private boolean inB()
    {
        boolean inB = false;
        
        if((mouseX >= 535 && mouseX <= 813) && (mouseY >= 416 && mouseY <= 473))
        {
            inB = true;
        }
        
        return inB;
    }
    
    // checks if mouse is over answer C
    private boolean inC()
    {
        boolean inC = false;
        
        if((mouseX >= 178 && mouseX <= 456) && (mouseY >= 484 && mouseY <= 541))
        {
            inC = true;
        }
        
        return inC;
    }
    
    // checks if mouse if over answer D
    private boolean inD()
    {
        boolean inD = false;
        
        if((mouseX >= 535 && mouseX <= 813) && (mouseY >= 484 && mouseY <= 541))
        {
            inD = true;
        }
        
        return inD;
    }
    
    // anserOverlay method sets the location the overlay needs to be drawn
    // depending on the passed answer.
    // Also sets if the overlay is for the user answer or the real answer
    public void answerOverlay(char answer, boolean realAnswer)
    {
        int x = 0;
        int y = 0;
        JLabel lblToChange = null;
        
        switch(answer)
        {
            case 'a':
                x = 150;
                y = 416;
                lblToChange = lblA;
                break;
            case 'b':
                x = 506;
                y = 416;
                lblToChange = lblB;
                break;
            case 'c':
                x = 150;
                y = 484;
                lblToChange = lblC;
                break;
            case 'd':
                x = 506;
                y = 484;
                lblToChange = lblD;
                break;
        }
        
        if(lblToChange != null)
        {
            lblToChange.setForeground(Color.black);
        }
        
        if(!realAnswer)
        {
            answerOverlay = true;
            overlayX = x;
            overlayY = y;
            repaint();
        }
        else
        {
            realAnswerOverlay = true;
            realOverlayX = x;
            realOverlayY = y;
            repaint();
        }       
    }
    
    private void setHandCursor()
    {
        if(getCursor().getType() != Cursor.HAND_CURSOR)
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }
    
    private void setDefaultCursor()
    {
        if(getCursor().getType() != Cursor.DEFAULT_CURSOR)
        {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    // displayQuestion method updates the question label and answer labels
    // with the passed arguments
    public void displayQuestion(String question, ArrayList<String> answers)
    {
        resetFlags();
        resetLabelColour();
        
        this.question.setText(question);
        
        answerA.setText(answers.get(0));
        answerB.setText(answers.get(1));
        answerC.setText(answers.get(2));
        answerD.setText(answers.get(3));
    }
    
    private void lockIn()
    {
        answerOverlay(selectedAnswer, false);
        backend.lockIn(selectedAnswer);
    }
    
    private void resetFlags()
    {
        lockedIn = false;
        inAnswerBox = false;
        answerOverlay = false;
        realAnswerOverlay = false;
        audienceAsked = false;
        repaint();
    }
    
    private void resetLabelColour()
    {
        lblA.setForeground(labelColour);
        lblB.setForeground(labelColour);
        lblC.setForeground(labelColour);
        lblD.setForeground(labelColour);
    }
    
    public String getQuestion()
    {
        return question.getText();
    }
    
    public ArrayList<String> getAnswers()
    {
        ArrayList<String> answers = new ArrayList<>();
        
        answers.add(answerA.getText());
        answers.add(answerB.getText());
        answers.add(answerC.getText());
        answers.add(answerD.getText());
        
        return answers;
    }

}
