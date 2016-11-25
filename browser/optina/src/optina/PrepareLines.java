/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import java.lang.Runnable;
import javax.microedition.lcdui.*;

/**
 *
 * @author Goncharov.Ilia
 */
abstract public class PrepareLines implements Runnable {
    protected ItemsQueue iq;
    private Canvas canv;
    protected Graphics g;
    protected HyperLinkManager hyp_mngr;
    private MultiLineText MLT;
    protected PercentValue parse_percent;
    private int old_hyp;
    
    abstract protected void InitQueue();

    public PrepareLines(Canvas canv, Graphics g, HyperLinkManager hyp_mngr, MultiLineText MLT, PercentValue parse_percent, int old_hyp)
    {
        this.canv = canv;
        this.g = g;
        this.hyp_mngr = hyp_mngr;
        this.MLT = MLT;
        this.parse_percent = parse_percent;
        this.old_hyp = old_hyp;
    }

    public void run()
    {
        InitQueue();
        int GrH=canv.getHeight();
        int GrW=canv.getWidth();
        int t = 10;

        MLT.SetTextPar(
          t,
          t,
          GrW - t*2,
          GrH - t*2,
          5,
          g,
          iq
        );
        if (old_hyp > -1)
            hyp_mngr.setCurrent(old_hyp);
    }
}
