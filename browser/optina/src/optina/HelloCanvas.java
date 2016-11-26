/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package optina;
import javax.microedition.lcdui.*;

/**
 *
 * @author Goncharov.Ilia
 */
public class HelloCanvas extends Canvas implements CommandListener {
    private WikiBrowser WB;
    Display myDisplay;


    protected void keyPressed (int keyCode)
    {
        if ((keyCode == KEY_NUM7) || (keyCode == KEY_NUM9))
            WB.MoveDown();
        else if ((keyCode == KEY_NUM1) || (keyCode == KEY_NUM3))
            WB.MoveUp();
        else if ((keyCode == getKeyCode(FIRE)) || (keyCode == KEY_NUM5))
            WB.FollowHyp();
        else if ((keyCode == getKeyCode(UP)) || (keyCode == KEY_NUM2))
            WB.PageUp();
        else if ((keyCode == getKeyCode(DOWN)) || (keyCode == KEY_NUM8))
            WB.PageDown();
        else if ((keyCode == getKeyCode(LEFT)) || (keyCode == KEY_NUM4) || (keyCode == -6))
            WB.PrevHyp();
        else if ((keyCode == getKeyCode(RIGHT)) || (keyCode == KEY_NUM6) || (keyCode == -7)) // джойстик барахлит просто :)
            WB.NextHyp();
        else if (keyCode == KEY_STAR)
            WB.PrevHist();
        else if (keyCode == KEY_POUND)
            WB.NextHist();

        repaint();
    }
    
    public HelloCanvas(Display d)
    {
        myDisplay = d;
        WB = null;
    }

    public void commandAction(Command command, Displayable displayable)
    {
    }

    public void paint(Graphics g) 
    {
        //System.out.println("Hit!");
        if (WB == null)
            //WB = new WikiBrowser(this, myDisplay, g, "index");
            WB = new WikiBrowser(this, myDisplay, g, "old:2par:start");
            //WB = new WikiBrowser(this, myDisplay, g, "old:gen:01:00");

        WB.draw();
    }
}
