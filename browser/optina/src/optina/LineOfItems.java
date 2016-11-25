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
public class LineOfItems {
    public Vector line;
    public int height;
    
    public LineOfItems()
    {
        height = 0;
        line = new Vector();
    }

    public void addElement(BaseItem bi)
    {
        int bi_height = bi.getHeight();
        if (bi_height > height)
            height = bi_height;

        line.addElement(bi);
    }
    
    public int getHeight()
    {return height;}
    
    public void draw(int x, int y)
    {
        int ix = x;
        for (int i = 0; i < line.size(); i++)
            ix += ((BaseItem)line.elementAt(i)).draw(ix, y);            
    }
}
