/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

/**
 * @author Goncharov.Ilia
 */
public class optina
extends MIDlet
{
    private HelloCanvas myCanvas;

    private void initialize() {
        myCanvas = new HelloCanvas(Display.getDisplay(this));
        myCanvas.setFullScreenMode(true);
        Display.getDisplay(this).setCurrent(myCanvas);
        int brightness = 100;
        TLightController.GetInstance(this).SetBrightness(brightness*10*255/100);
        
    }

    public void startApp()
    {initialize();}
    public void pauseApp()
    {}
    public void destroyApp(boolean unconditional)
    {}
}
