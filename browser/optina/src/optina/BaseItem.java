/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Goncharov.Ilia
 */
abstract public class BaseItem {
    private long ofs, start, end;
    private boolean is_nf_part;
    private Graphics g;
    
    public BaseItem(Graphics vg, long ofs, long start, long end, boolean is_nf_part)
    {
        this.ofs = ofs;
        this.start = start;
        this.end = end;
        this.is_nf_part = is_nf_part;
        g = vg;
        init();
        String s;
    }
    
    public BaseItem(BaseItem other)
    {
        ofs = other.ofs;
        start = other.start;
        end = other.end;
        is_nf_part = other.is_nf_part;
        g = other.g;
    }

    public long getOfs()
    {return ofs;}
    public long getStart()
    {return start;}
    public long getEnd()
    {return end;}
    public boolean IsNfPart()
    {return is_nf_part;}
    public Graphics getGraphics()
    {return g;}

    abstract public BaseItem part(long i0, long i1);

    abstract public void init();
    abstract public Font getFont();

    abstract public int getColor();

    abstract public int getHeight();
    abstract public int length();
    abstract public int indexOf(int ch, int i);
    abstract public int indexOf(int ch, int i0, int i1);
    abstract public int stringWidth(int i0, int i1);
    abstract public int stringWidth();
    abstract public int draw(int x, int y);
    abstract public void accept(ItemVisitor vis);
}
