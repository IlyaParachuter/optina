/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import java.io.*;
import javax.microedition.lcdui.Graphics;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 *
 * @author Goncharov.Ilia
 */
public class FsaDrawLineOfItems implements InterfaceLineOfItemsProcessing {
    final int fsa_start = 0;
    final int fsa_add = 1;
    //final int fsa_skip_before = 1;
    //final int fsa_draw = 2;

    private int ofs = 0;
    private int state = fsa_start;
    private Graphics g;
    FileConnection fc;
    private InputStream is;
    private FileConnection fcIdx;
    private InterfaceCharacterProcessing CharProcessing;
    private HyperLinkManager hyp_mngr;
    private int x, y, w, h;
    private int y_page;
    private int y_line;
    private int i, i_first;
    private LinkedList lines;
    private LinkedListEnumeration lines_enumeration;
    private int textheight; //Общая высота текста
    public final int nm_txt = 0;
    public final int nm_hyp = 1;
    private int nav_mode;
    private int y0 = 0, dy = 5;
    private LineOfItems loi = null;
    private String path, pathIdx;
    private IndexFile idx;

    public FsaDrawLineOfItems(Graphics g, String path, int x, int y, int width, int height, HyperLinkManager hyp_mngr)
    {
        try
        {
            this.path = path;
            fc = (FileConnection)Connector.open(path, Connector.READ);
            is = fc.openInputStream();
            pathIdx = path + ".idx";
            try
            {
                idx = new IndexFile(pathIdx);
                this.g = g;
                this.x = x;
                this.y = y;
                this.w = width;
                this.h = height;
                CharProcessing = null;
                this.hyp_mngr = hyp_mngr;
                y_page = y_line = 0;
                i = i_first = 0;
                lines = new LinkedList();
                setNavMode(nm_txt);
            }
            catch (IOException ioe)
            {
                System.out.println(ioe.getMessage());
            }
        }
        catch (IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }
    }
    
    public void setCharProcessing(InterfaceCharacterProcessing CharProcessing)
    {this.CharProcessing = CharProcessing;}
    
    public void process(LineOfItems loi)
    {
        lines.insertLast(loi);
        state = fsa_start;
    }

    public void flush()
    {
        //
    }
    
    public void close()
    {
        //
    }

    public LineOfItems hasMoreElements(int y) throws IOException
    {
        LineOfItems res = null;
        boolean has = lines_enumeration.hasMoreElements();
        IndexElement ie = null;
        if (!has)
        {
            state = fsa_add;
            try
            {
                ie = SkipStream2LineY(y);
            }
            catch (IOException ioe)
            {
                System.out.println(ioe);
            }
            int c = -1;
            while((state != fsa_start) && ((c = is.read())!=-1))
            {
                CharProcessing.process((char)c, ofs++);
            }
            CharProcessing.flush();

            if (state == fsa_start)
                has = true;
            else
                state = fsa_start; // Значит нет данных!
        }
        
        if (has)
        {
            LLNode node;
            if (ie != null)
                do
                {
                    node = (LLNode)nextElement();
                    res = (LineOfItems)node.getData();
                } while (res.getStart() < ie.getStart());
            else
            {
                node = (LLNode)nextElement();
                res = (LineOfItems)node.getData();
            }

            while (node.prev != null)
                lines.removeFirst();
        }
        return res;
    }

    public Object nextElement()
    {return lines_enumeration.nextElement();}
    
    private IndexElement SkipStream2LineY(int y) throws IOException
    {
        IndexElement ie;
        ie = idx.read(y);
        is.close();
        is = fc.openInputStream();
        is.skip(ie.getOfs());
        CharProcessing.reset();
        return ie;
    }
    
    public void draw()
    {
        int i = 0;
        int y = 0;
        int y1 = y0;
        LLNode node = null;

        loi = null;
        hyp_mngr.ResetDrawed();
        try
        {
            lines_enumeration = (LinkedListEnumeration)lines.elements();
            loi = hasMoreElements(-y1);

            if (loi != null)
            {
                //while (node.prev != null)
                    //lines.removeFirst();
                
                long t = loi.getStart();
                
                for (;;)
                {
                    idx.write(loi, y);
                    int h_str = loi.getHeight();
                    //loi.draw(x + 1, y1);
                    loi.draw(x + 1, (int)((long)y1 + t));
                    if (y1 > h)
                    {
                        break;
                    }
                    
                    loi = hasMoreElements(-y1);
                    if (loi == null)
                    {
                        break;
                    }

                    y1 += h_str;
                    y += h_str;
                    i++;
                }
                idx.close();
            }
        }
        catch (IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }
    }

    final public void setNavMode(int nm)
    {nav_mode = nm;}

    public void PrevHyp()
    {
        setNavMode(nm_hyp);
        hyp_mngr.Prev();
    }

    public void NextHyp()
    {
        setNavMode(nm_hyp);
        hyp_mngr.next();
    }

    public void FollowHyp()
    {
        //MLT.setNavMode(MLT.nm_hyp);
    }

    public void MoveUp()
    {
        setNavMode(nm_txt);
        //if (textheight>h)
        {
            y0 += dy;
            if (y0>0){y0=0;}
        }
        
    }

    public void  MoveDown()
    {
        setNavMode(nm_txt);
        //if (textheight>h)
        //if (loi != null)
        {            
            y0 -= dy;
            //if (h-y0>textheight) {y0=h-textheight;}
        }
    }

    public void PageUp()
    {
        setNavMode(nm_txt);
        if (textheight>h)
        {
           y0 += h;
           if (y0>0){y0=0;} 
        }
        
    }

    public void PageDown()
    {
        setNavMode(nm_txt);
        if (textheight>h)
        {
            y0 -= h;
            if (h-y0>textheight) {y0=h-textheight;}
        }         
    }
}
