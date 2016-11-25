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
    private int start, end;
    private Graphics g;
    
    public BaseItem(Graphics vg, int vstart, int vend)
    {
        start = vstart;
        end = vend;
        g = vg;
        init();
    }
    
    public BaseItem(BaseItem other)
    {
        start = other.start;
        end = other.end;
        g = other.g;
    }
    
    public int getStart()
    {return start;}
    public int getEnd()
    {return end;}
    public Graphics getGraphics()
    {return g;}

    abstract public BaseItem part(int i0, int i1);

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
