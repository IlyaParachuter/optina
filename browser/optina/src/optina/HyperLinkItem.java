package optina;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author home
 */
public class HyperLinkItem extends PlainTextItem {
    private HyperLinkManager mngr;
    private int number;
    private String link;
    private char dbg_link[];
    private static Font fntSelected, fntUnselected;
    private static Font fntInvSelected, fntInvUnselected;
    private static int colorSelected, colorUnselected;
    private static int colorInvSelected, colorInvUnselected;
    private boolean valid; // true, если ссылка указывает на существующий файл.
    
    public HyperLinkItem(Graphics g, long ofs, HyperLinkManager mngr, long start, long end, String text, int number, String link)
    {
        super(g, ofs, start, end, text);
        this.mngr = mngr;
        this.number = number;
        setLink(link);
    }
    
    public HyperLinkItem(PlainTextItem other, HyperLinkManager mngr, int number, String link, boolean valid)
    {
        super(other);
        this.mngr = mngr;
        this.number = number;
        setLink(link, valid);
    }
    
    public HyperLinkItem(HyperLinkItem other)
    {
        super(other);
        mngr = other.mngr;
        number = other.number;
        setLink(other.link, other.valid);
    }
    
    public int getNumber()
    {return number;}
    
    final public HyperLinkManager getMngr()
    {return mngr;}
    
    final public void setLink(String vlink)
    {
        boolean is_valid;

        WikiUrl wu = new WikiUrl(vlink);
        String path = wu.getFilePath();
        try {
            int path_len = path.length();
            char dbg_path[] = new char[path_len];
            path.getChars(0, path_len, dbg_path, 0);
            FileConnection fc=(FileConnection)Connector.open(path, Connector.READ);
            //is_valid = fc.exists();
            is_valid = fc.canRead();
            fc.close();
        }
        catch (IOException ioe)
        {
            is_valid = false;
        }
        setLink(vlink, is_valid);
    }

    final private void setLink(String vlink, boolean valid)
    {
        link = vlink;
        int link_len = link.length();
        dbg_link = new char[link_len];
        link.getChars(0, link_len, dbg_link, 0);
        this.valid = valid;
    }
    
    public String getLink()
    {return valid ? link : null;}
    
    public BaseItem part(long i0, long i1)
    {
        return new HyperLinkItem((PlainTextItem)super.part(i0, i1), mngr, number, link, valid);
    }

    public void init()
    {
        if (fntSelected == null)
        {
            colorSelected = 0xffff80;
            colorUnselected = 0xffffff;
            colorInvSelected = 0xffc080;
            colorInvUnselected = 0xffc0c0;
            //fntSelected = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
            fntUnselected = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
            fntSelected = fntUnselected;
            fntInvUnselected = fntUnselected;
            fntInvSelected = fntUnselected;
        }
    }

    public Font getFont()
    {
        return ((mngr != null) && (mngr.getCurrent() == number)) ?
                (valid ? fntSelected : fntInvSelected) : 
                (valid ? fntUnselected : fntInvUnselected);
    }
    
    public int getColor()
    {
        return ((mngr != null) && (mngr.getCurrent() == number)) ?
            (valid ? colorSelected : colorInvSelected) :
            (valid ? colorUnselected : colorInvUnselected);
    }
    
    public int draw(int x, int y)
    {
        int res = super.draw(x, y);
        mngr.setFirstDrawed(number);
        mngr.setLastDrawed(number);
        return res;
    }

    public void accept(ItemVisitor vis)
    {vis.visit(this);}
}
