/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import java.util.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author Goncharov.Ilia
 */
public class TimerRepaint extends TimerTask {
    private Canvas canv;

    public TimerRepaint(Canvas canv)
    {this.canv = canv;}

    public void run()
    {canv.repaint();}
}
