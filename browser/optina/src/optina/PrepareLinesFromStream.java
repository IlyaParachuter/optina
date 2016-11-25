/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package optina;

import java.io.*;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Goncharov.Ilia
 */
public class PrepareLinesFromStream extends PrepareLines {
    private InputStream is;

    protected void InitQueue()
    {
        iq = new ItemsQueue(g, hyp_mngr, is, parse_percent);
    }

    public PrepareLinesFromStream(Canvas canv, Graphics g, HyperLinkManager hyp_mngr, MultiLineText MLT, InputStream is, PercentValue parse_percent, int old_hyp)
    {
        super(canv, g, hyp_mngr, MLT, parse_percent, old_hyp);
        this.is = is;
    }
}
