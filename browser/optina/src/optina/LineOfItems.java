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
    private int ln;
    public int height = 0; // высота в пикселях
    public long ofs = 0; // смещение в файле до начала токена
    public boolean is_nf_part = false; // это не первая часть
    public long start = 0;
    
    public LineOfItems(int ln)
    {
        line = new Vector();
        this.ln = ln;
    }

    public void addElement(BaseItem bi)
    {
        if(line.size() == 0)
        {
            ofs = bi.getOfs();
            is_nf_part = bi.IsNfPart();
            start = bi.getStart();
        }
        int bi_height = bi.getHeight();
        if (bi_height > height)
            height = bi_height;

        line.addElement(bi);
    }

    public int getLineNumber()
    {return ln;}
    public int getHeight()
    {return height;}
    public long getOfs()
    {return ofs;}
    public boolean IsNfPart()
    {return is_nf_part;}
    public long getStart()
    {return start;}
    
    public void draw(int x, int y)
    {
        int ix = x;
        for (int i = 0; i < line.size(); i++)
            ix += ((BaseItem)line.elementAt(i)).draw(ix, y);            
    }
}
