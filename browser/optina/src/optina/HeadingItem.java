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
public class HeadingItem extends PlainTextItem {
    private int level;
    private static Font font;
    private static int color;
    
    public HeadingItem(Graphics g, long ofs, long start, long end, String text, int level)
    {
        super(g, ofs, start, end, text);
        this.level = level;
    }
    
    public HeadingItem(PlainTextItem other, int level)
    {
        super(other);
        this.level = level;
    }
    
    public BaseItem part(long i0, long i1)
    {return new HeadingItem((PlainTextItem)super.part(i0, i1), level);}
    
    public void init()
    {
        if (font == null)
        {
            color = 0xa0a0ff;// - (0xff / level);
            font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
        }
    }

    public Font getFont()
    {return font;}
    
    public int getColor()
    {return color;}

    public void accept(ItemVisitor vis)
    {vis.visit(this);}
}
