/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import java.util.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import java.io.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author Goncharov.Ilia
 */
public class WikiBrowser {
    private Canvas canv;
    private Display d;
    Graphics g;
    HyperLinkManager hyp_mngr;
    private MultiLineText MLT;
    private boolean ok;
    //private PercentValue parse_percent;
    private Vector hyp_history;
    private int pos_history;
    //private Timer timerRepaint;

    private void setSrc(String path, int old_hyp)
    {
        int GrH=canv.getHeight();
        int GrW=canv.getWidth();
        int t = 10;

        hyp_mngr = new HyperLinkManager();
        MLT = new MultiLineText(
                g,
                canv,
                hyp_mngr,
                t,
                t,
                GrW - t*2,
                GrH - t*2,
                path
        );
        //parse_percent = new PercentValue();

        if (old_hyp > -1)
            hyp_mngr.setCurrent(old_hyp);
    }

    private void load(String url)
    {load(url, -1, false);}
    private void load(HistoryElement his, boolean from_hist)
    {load(his.getUrl(), his.getCurrentHyp(), from_hist);}

    private void load(String url, int old_hyp, boolean from_hist)
    {
        if (!from_hist) // нас вызвали не из PrevHist / NextHist
        {
            if (pos_history > -1)
            {
                ((HistoryElement)hyp_history.elementAt(pos_history)).setCurrentHyp(hyp_mngr.getCurrent());
            }

            pos_history++;
            if (pos_history < hyp_history.size())
            {
                // сравниваем ссылки, и это работает:
                if (((HistoryElement)hyp_history.elementAt(pos_history)).getUrl() != url)
                {
                    hyp_history.setSize(pos_history + 1);
                    hyp_history.setElementAt(new HistoryElement(url, -1), pos_history);
                }
            }
            else
                hyp_history.addElement(new HistoryElement(url, -1));
        }

        WikiUrl wu = new WikiUrl(url);
        String path = wu.getFilePath();

        //try
        //{
        setSrc(path, old_hyp);
        /*}
        catch (IOException ioe)
        {
            // выведем список корней ФС для отладки:
            g.setColor(0x000000);   //set color for the background
            g.fillRect(0, 0, canv.getWidth(), canv.getHeight());
            Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

            g.setFont(font);
            g.setColor(0xffffff);
            Enumeration e = FileSystemRegistry.listRoots();
            int y = 1;
            while(e.hasMoreElements())
            {
                g.drawString((String)e.nextElement(), 1, y , g.LEFT | g.TOP);
                y += font.getHeight();
            }
            canv.repaint();
            ok = false;
        }*/
    }

    public WikiBrowser(Canvas vcanv, Display vd, Graphics vg, String url)
    {
        ok = true;
        canv = vcanv;
        d = vd;
        g = vg;
        hyp_history = new Vector();
        pos_history = -1;
        //timerRepaint = new Timer();
        load(url);
    }

    public void draw()
    {
        if (ok)
        {
            // данные готовы?
            //if ((prep_thread != null) && !prep_thread.isAlive())
            //{
            g.setColor(0x000000);
            g.fillRect(0, 0, canv.getWidth(), canv.getHeight());
            MLT.DrawMultStr();
            /*}
            else // рисуем progress bar:
            {
                g.setColor(0x202520);
                int mid_x = canv.getWidth() / 2;
                int mid_y = canv.getHeight() / 2;
                int bar_width = 200;
                int bar_height = 80;
                int border_size = 10;
                g.fillRect(mid_x - bar_width / 2, mid_y - bar_height / 2, bar_width, bar_height);
                g.setColor(0x808020);
                int prc = (parse_percent == null) ? 0 : parse_percent.get();
                g.fillRect(
                    mid_x - (bar_width - border_size) / 2,
                    mid_y - (bar_height - border_size) / 2,
                    (prc * (bar_width - border_size)) / 100,
                    bar_height - border_size
                );
                Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE);
                g.setFont(font);
                g.setColor(0xffffff);
                String s = "" + prc + "%";
                g.drawString(s, mid_x - font.stringWidth(s) / 2, mid_y - font.getHeight() / 2 , g.LEFT | g.TOP);

                //canv.repaint();
                TimerRepaint repaintTask = new TimerRepaint(canv);
                timerRepaint.schedule(repaintTask, 100);
            }*/
        }
    }
    public void PrevHyp()
    {MLT.PrevHyp();}
    public void NextHyp()
    {MLT.NextHyp();}
    
    public void NextHist()
    {
        if (pos_history < hyp_history.size() - 1)
        {
            pos_history++;
            HistoryElement his = (HistoryElement)hyp_history.elementAt(pos_history);
            load(his, true);
        }
    }

    public void PrevHist()
    {
        if (pos_history > 0)
        {
            pos_history--;
            HistoryElement his = (HistoryElement)hyp_history.elementAt(pos_history);
            load(his, true);
        }
    }

    public void FollowHyp()
    {
        String url = MLT.getCurrentLink();
        if (url == null)
        {
            d.vibrate(125);
        }
        else
            load(url);

        //MLT.FollowHyp();
        canv.repaint();
    }

    public void MoveUp()
    {MLT.MoveUp();}
    public void MoveDown()
    {MLT.MoveDown();}
    public void PageUp()
    {MLT.PageUp();}
    public void PageDown()
    {MLT.PageDown();}
}
