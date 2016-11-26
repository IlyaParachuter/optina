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
    
    public ItemsQueue()
    {
        queue = new Vector();
    }
    
    public void addElement(BaseItem bi)
    {queue.addElement(bi);}
    
    public BaseItem peek()
    {return (queue.size() > 0) ? (BaseItem)queue.elementAt(0) : null;}
    public void remove()
    {queue.removeElementAt(0);}
    /// del
    public long length()
    {
        long res = 0;

        for (int i = 0; i < queue.size(); i++)
            res += ((BaseItem)queue.elementAt(i)).length();

        return res;
    }
    
    public long indexOf(int ch, long i0)
    {
        long res = -1;
        int i = 0;
        for (; i < queue.size(); i++)
        {
            BaseItem bi = (BaseItem)queue.elementAt(i);
            long bi_start = bi.getStart();
            long bi_end = bi.getEnd();
            if (bi_end >= i0)
            {
                res = bi.indexOf(ch, (int)(i0 - bi_start));
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

    /// del
    public int indexOf(int ch, long i0, long i1)
    {
        int res = -1;
        int i = 0;
        long i_end = i1 - 1;
        for (; i < queue.size(); i++)
        {
            BaseItem bi = (BaseItem)queue.elementAt(i);
            long bi_start = bi.getStart();
            if (bi_start > i_end)
                break;

            long bi_end = bi.getEnd();

            if (bi_end >= i0)
            {
                res = bi.indexOf(ch, (int)(i0 - bi_start), (int)(i1 - bi_start));
                if (res == -1)
                {
                    i++;
                    for (; i < queue.size(); i++)
                    {
                        bi = (BaseItem)queue.elementAt(i);
                        bi_start = bi.getStart();
                        if (bi_start > i_end)
                            break;

                        res = bi.indexOf(ch, 0, (int)(i1 - bi_start));
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
    /// del
    public int stringWidth(long i0, long i1)
    {
        int res = 0;
        int i = 0;
        long i_end = i1 - 1;
        for (; i < queue.size(); i++)
        {
            BaseItem bi = (BaseItem)queue.elementAt(i);
            long bi_start = bi.getStart();
            long bi_end = bi.getEnd();
            if (bi_end >= i0)
            {
                if (bi_end < i_end)
                {
                    res += bi.stringWidth((int)(i0 - bi_start), (int)(bi_end + 1 - bi_start));
                    i++;
                    for (; i < queue.size(); i++)
                    {
                        bi = (BaseItem)queue.elementAt(i);
                        bi_start = bi.getStart();
                        bi_end = bi.getEnd();
                        if (bi_end > i_end)
                        {
                            res += bi.stringWidth(0, (int)(i1 - bi_start));
                            break;
                        }
                        else
                        {
                            res += bi.stringWidth(0, (int)(bi_end + 1 - bi_start));
                        }
                    }
                }
                else
                    res += bi.stringWidth((int)(i0 - bi_start), (int)(i1 - bi_start));

                break;
            }
        }
        return res;
    }
}
