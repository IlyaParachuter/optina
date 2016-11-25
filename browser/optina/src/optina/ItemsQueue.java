/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import java.io.*;
import java.util.*;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Goncharov.Ilia
 */
public class ItemsQueue {
    private Vector queue;
    private Graphics g;
    
    public ItemsQueue(Graphics vg, HyperLinkManager hyp_mngr, String content, PercentValue parse_percent)
    {
        g = vg;
        parseString(hyp_mngr, content, parse_percent);
    }

    public ItemsQueue(Graphics vg, HyperLinkManager hyp_mngr, InputStream is, PercentValue parse_percent)
    {
        g = vg;
        parseStream(hyp_mngr, is, parse_percent);
    }
    
    public BaseItem peek()
    {return (queue.size() > 0) ? (BaseItem)queue.elementAt(0) : null;}
    public void remove()
    {queue.removeElementAt(0);}

    public int length()
    {
        int res = 0;

        for (int i = 0; i < queue.size(); i++)
            res += ((BaseItem)queue.elementAt(i)).length();

        return res;
    }
    
    public int indexOf(int ch, int i0)
    {
        int res = -1;
        int i = 0;
        for (; i < queue.size(); i++)
        {
            BaseItem bi = (BaseItem)queue.elementAt(i);
            int bi_start = bi.getStart();
            int bi_end = bi.getEnd();
            if (bi_end >= i0)
            {
                res = bi.indexOf(ch, i0 - bi_start);
                if (res == -1)
                {
                    i++;
                    for (; i < queue.size(); i++)
                    {
                        bi = (BaseItem)queue.elementAt(i);
                        bi_start = bi.getStart();
                        res = bi.indexOf(ch, 0);
                        if (res != -1)
                        {
                            res += bi_start;
                            break;
                        }
                    }
                }
                else
                    res += bi_start;
                break;
            }
        }
        return res;
    }

    public int indexOf(int ch, int i0, int i1)
    {
        int res = -1;
        int i = 0;
        int i_end = i1 - 1;
        for (; i < queue.size(); i++)
        {
            BaseItem bi = (BaseItem)queue.elementAt(i);
            int bi_start = bi.getStart();
            if (bi_start > i_end)
                break;

            int bi_end = bi.getEnd();

            if (bi_end >= i0)
            {
                res = bi.indexOf(ch, i0 - bi_start, i1 - bi_start);
                if (res == -1)
                {
                    i++;
                    for (; i < queue.size(); i++)
                    {
                        bi = (BaseItem)queue.elementAt(i);
                        bi_start = bi.getStart();
                        if (bi_start > i_end)
                            break;

                        res = bi.indexOf(ch, 0, i1 - bi_start);
                        if (res != -1)
                        {
                            res += bi_start;
                            break;
                        }
                    }
                }
                else
                {
                    res += bi_start;
                    if (res > i_end)
                        res = -1;
                }
                break;
            }
        }
        return res;
    }
    
    public int stringWidth(int i0, int i1)
    {
        int res = 0;
        int i = 0;
        int i_end = i1 - 1;
        for (; i < queue.size(); i++)
        {
            BaseItem bi = (BaseItem)queue.elementAt(i);
            int bi_start = bi.getStart();
            int bi_end = bi.getEnd();
            if (bi_end >= i0)
            {
                if (bi_end < i_end)
                {
                    res += bi.stringWidth(i0 - bi_start, bi_end + 1 - bi_start);
                    i++;
                    for (; i < queue.size(); i++)
                    {
                        bi = (BaseItem)queue.elementAt(i);
                        bi_start = bi.getStart();
                        bi_end = bi.getEnd();
                        if (bi_end > i_end)
                        {
                            res += bi.stringWidth(0, i1 - bi_start);
                            break;
                        }
                        else
                        {
                            res += bi.stringWidth(0, bi_end + 1 - bi_start);
                        }
                    }
                }
                else
                    res += bi.stringWidth(i0 - bi_start, i1 - bi_start);

                break;
            }
        }
        return res;
    }

    final public void parseString(HyperLinkManager hyp_mngr, String content, PercentValue parse_percent)
    {
        queue = new Vector();
        int src_pos;
        int content_len = content.length();

        InterfaceCharacterProcessing CharProcessing = new FsaWikiParser(g, hyp_mngr, queue);
        CharProcessing = new FsaWikiLineFeedParser(CharProcessing);
        CharProcessing = new FsaUtf8Decoder(CharProcessing);

        parse_percent.setMax(content_len - 1);
        for (src_pos = 0; src_pos < content_len; src_pos++)
        {
            parse_percent.setCurrent(src_pos);
            char c = content.charAt(src_pos);
            CharProcessing.process(c);
        }
        CharProcessing.close();
    }

    final public void parseStream(HyperLinkManager hyp_mngr, InputStream is, PercentValue parse_percent)
    {
        queue = new Vector();
        int src_pos = 0;

        InterfaceCharacterProcessing CharProcessing = new FsaWikiParser(g, hyp_mngr, queue);
        CharProcessing = new FsaWikiLineFeedParser(CharProcessing);
        CharProcessing = new FsaUtf8Decoder(CharProcessing);

        try
        {
            parse_percent.setMax(is.available() - 1);
            int c;
            while((c = is.read())!=-1)
            {
                parse_percent.setCurrent(src_pos++);
                CharProcessing.process((char)c);
            }
            CharProcessing.close();
        }
        catch (IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }
    }
}
