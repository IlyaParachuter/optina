/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import java.util.Vector;
import java.util.Hashtable;

/**
 *
 * @author Goncharov.Ilia
 */
public class HyperLinkManager {
    private int current;
    private int first_drawed, last_drawed;
    private int total;
    private boolean loop_mode;
    private Hashtable HypNumber2Ln;
    
    public HyperLinkManager()
    {
        current = -1;
        total = 0;
        loop_mode = true;
        reset();
        HypNumber2Ln = new Hashtable();
    }
    
    public void Prev()
    {
        if (current > 0)
            current--;
        else
        if (loop_mode)
            current = total - 1;
    }
    
    public void next()
    {
        if (current < total - 1)
            current++;
        else
        if (loop_mode && (current > 0))
            current = 0;
    }
    
    public void incTotal()
    {total++;}
    public int getTotal()
    {return total;}

    public void register(HyperLinkItemLn hil)
    {
        Integer key = new Integer(hil.getNumber());
        Vector val;

        if (HypNumber2Ln.containsKey(key))
            val = (Vector)HypNumber2Ln.get(key);
        else
            val = new Vector();

        //val.addElement(new Integer(hil.getLineNumber()));
        val.addElement(hil);
        HypNumber2Ln.put(key, val);
    }
    
    public Vector getLnFromHypNumber(int n)
    {return (Vector)HypNumber2Ln.get(new Integer(n));}

    public void setCurrent(int n)
    {current = n;}
    public int getCurrent()
    {return current;}

    public void setFirstDrawed(int n)
    {
        if (first_drawed == -1)
            first_drawed = n;
    }

    public int getFirstDrawed()
    {return first_drawed;}
    public void setLastDrawed(int n)
    {last_drawed = n;}
    public int getLastDrawed()
    {return last_drawed;}
    
    public void reset()
    {
        first_drawed = -1;
        last_drawed = -1;
    }
}
