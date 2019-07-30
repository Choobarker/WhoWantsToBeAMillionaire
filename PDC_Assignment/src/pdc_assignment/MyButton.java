package pdc_assignment;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

// MyButton class is a JComponent which represents a custom button
public class MyButton extends JComponent
{    
    private ImageIcon buttonSkin;
    
    public MyButton()
    {
        super();
    }
    
    public void updateSkin(ImageIcon buttonSkin)
    {
        this.buttonSkin = buttonSkin;
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        if(buttonSkin != null)
        {
            buttonSkin.paintIcon(this, g, 0, 0);
        }
    }
    
}
