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
public class PlainTextItem extends BaseItem {
    public int width;
    private String text;
    private char dbg_text[];
    private static Font font;
    private static int color;
    
    public PlainTextItem(Graphics g, int vstart, int vend, String vtext)
    {
        super(g, vstart, vend);
        setText(vtext);
    }
    
    public PlainTextItem(PlainTextItem other)
    {
        super(other);
        setText(other.text);
    }
    
    final public void setText(String vtext)
    {
        text = vtext;
        int text_len = text.length();
        dbg_text = new char[text_len];
        text.getChars(0, text_len, dbg_text, 0);
        width = stringWidth(0, text_len);
    }

    public BaseItem part(int i0, int i1)
    {
        int start = getStart();
        return new PlainTextItem(getGraphics(), i0, i1, text.substring(i0-start, i1+1-start));
    }

    public void init()
    {
        if (font == null)
        {
            color = 0xd0d0d0;
            font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        }
    }

    public Font getFont()
    {return font;}
    
    public int getColor()
    {return color;}
    
    public int getHeight()
    {return getFont().getHeight();}
    public int length()
    {return text.length();}
    public int indexOf(int ch, int i)
    {return text.indexOf(ch, i);}

    public int indexOf(int ch, int i0, int i1)
    {
        int ilen = text.length();
        int iend = i1 > ilen ? ilen : i1;
        int res;
        
        res = text.substring(i0, iend).indexOf(ch);

        return (res == -1) ? res : res + i0;
    }

    public int stringWidth(int i0, int i1)
    {return getFont().stringWidth(text.substring(i0, i1));}
    public int stringWidth()
    {return width;}

    public int draw(int x, int y)
    {
        Graphics g = getGraphics();
        g.setFont(getFont());
        g.setColor(getColor());
        g.drawString(text, x, y, g.LEFT | g.TOP);
        return stringWidth();
    }
    
    public void accept(ItemVisitor vis)
    {vis.visit(this);}
}
