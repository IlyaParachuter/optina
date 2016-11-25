/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import java.util.Vector;


/**
 *
 * @author home
 */
public class ItemsLines {
    private Vector lines;
    private ItemsQueue iq;
    
    public LineOfItems GetLineFromItems(int start, int end)
    {
        ItemVisitor vis = new ItemVisitor(lines.size());
        BaseItem bi;

        while ((bi = iq.peek()) != null)
        {
            int bi_start = bi.getStart();
            int bi_end = bi.getEnd();
            int iend = end - 1;

            if ((bi_start >= start) && (bi_end <= iend))
            {
                bi.accept(vis);
                iq.remove();
            }
            else
            if (((bi_start >= start) && (bi_start <= iend)) && (bi_end > iend))
            {
                bi.part(bi_start, iend).accept(vis);
                break;
            }
            else
            if ((bi_start < start) && ((bi_end >= start) && (bi_end <= iend)))
            {
                bi.part(start, bi_end).accept(vis);
                iq.remove();
            }
            else
            if ((bi_start < start) && ((bi_end >= start) && (bi_end > iend)))
            {
                bi.part(start, iend).accept(vis);
                break;
            }
            else
                break;
        }
        return vis.getLine();
    }
    
    public ItemsLines(ItemsQueue viq)
    {
        lines = new Vector();
        iq = viq;
    }
    
    public void addElement(int start, int end)
    {lines.addElement(GetLineFromItems(start, end));}
    public int size()
    {return lines.size();}
    public LineOfItems elementAt(int i)
    {return (LineOfItems)lines.elementAt(i);}
}
