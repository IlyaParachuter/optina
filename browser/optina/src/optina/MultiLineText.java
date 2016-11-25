/*
 * To change this license header, choose License Headers start_of_line Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template start_of_line the editor.
 */

package optina;

/**
 *
 * @author Goncharov.Ilia
 */
//public class MultiLineText {

//}
/*
 * Класс MultiLineText предназначен для отображения многострочного текста.
 * Автор А.С. Ледков
 * www.mobilab.ru
 * ledkov@inbox.ru
 */

import java.util.Vector;
import javax.microedition.lcdui.*;

public class MultiLineText {
    private int x,y,w,h,fsz,fst,fty;    //Размер ограничивающего прямоугольника;
    private int y0;         //Положение верхнего края текста
    private int dy;         //Шаг при прокрутке текста
    private int textheight; //Общая высота текста
    private ItemsLines StringLines;    
    private Graphics g;
    private int gx,gy,gw,gh; //Исходная область
    private HyperLinkManager hyp_mngr;
    private Canvas canv;
    private int nav_mode;
    public final int nm_txt = 0;
    public final int nm_hyp = 1;
    
    public MultiLineText(Canvas vcanv, HyperLinkManager vhyp_mngr)
    {
        canv = vcanv;
        hyp_mngr = vhyp_mngr;
        setNavMode(nm_txt);
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
    
    public String getCurrentLink()
    {
        int cur = hyp_mngr.getCurrent();
        Vector LineNumbers = hyp_mngr.getLnFromHypNumber(cur);
        return ((HyperLinkItemLn)LineNumbers.elementAt(0)).getLink();
    }

    public void  MoveDown()
    {
        setNavMode(nm_txt);
        if (textheight>h)
        {            
            y0 -= dy;
            if (h-y0>textheight) {y0=h-textheight;}
        }
    }
    
    public void MoveUp()
    {
        setNavMode(nm_txt);
        if (textheight>h)
        {
            y0 += dy;
            if (y0>0){y0=0;}
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

    public void SetTextPar(
            int x, 
            int y,
            int width,
            int height,
            int dy,
            Graphics graph,
            ItemsQueue iq
    )
    {
        String s;
        int nl;
        
        this.x=x;
        this.y=y;
        this.w=width;
        this.h=height;
        this.dy=dy;
        this.g=graph;
        gx=g.getClipX();
        gy=g.getClipY();
        gw=g.getClipWidth();
        gh=g.getClipHeight();
        g.setFont(Font.getFont(fty, fst, fsz));
        //Разбиваем строку на массив строк
        StringLines=null;
        StringLines =new ItemsLines(iq);
        int end_of_line=0,next_space=0,start_of_line=0,next_width,line_width=0;   //Сещение от начала строки
        int imax=iq.length();   //Длина строки
        boolean isexit=true;
        y0=0;
        while (isexit)
        {
            next_space=iq.indexOf(' ', end_of_line+1);
            if (next_space == -1)
            {
                next_space=imax;
                isexit=false;
            }
               
            next_width=iq.stringWidth(end_of_line, next_space);
            if (line_width + next_width < w)
            {//Слово умещается
                line_width += next_width;
                end_of_line = next_space;
            } else 
            {//Слово не умещается
                nl = iq.indexOf('\n', start_of_line, end_of_line);
                if (nl > -1)
                {
                    end_of_line = nl;
                    next_space = iq.indexOf(' ', end_of_line+1);
                    if (next_space == -1)
                    {
                        next_space = imax;
                        isexit = false;
                    }
                    next_width=iq.stringWidth(end_of_line, next_space);
                }

                StringLines.addElement(start_of_line, end_of_line);
                if (nl > -1)
                {
                    // т.к. в даннной ситуации элемент с переводом строки в конце
                    // отображаемого текста остается в очереди, принудительно извлекаем его:
                    StringLines.GetLineFromItems(nl, nl + 1);
                    end_of_line++;
                }
                start_of_line = end_of_line;
                line_width = next_width;
                if (next_width > w)
                {//Слово полностью не умещается в строке
                    
                    next_space = end_of_line;
                    while (line_width>w)
                    {
                      next_width = 0;  
                      while (next_width<w)
                      {
                          next_space++;
                          next_width=iq.stringWidth(start_of_line, next_space);

                      }
                      next_space--;
                      next_width = iq.stringWidth(start_of_line, next_space);
                      nl = iq.indexOf('\n', start_of_line, next_space);
                      if (nl > -1)
                      {
                          next_space = nl;
                          next_width = iq.stringWidth(start_of_line, next_space);
                          next_space++;
                      }
                      StringLines.addElement(start_of_line, next_space);

                      line_width -= next_width;
                      end_of_line = next_space;                
                      start_of_line = next_space;
                    }
                    line_width=0;                    
                }else{end_of_line = next_space;}                
            }            
        }

        int i = start_of_line;
        while ((nl = iq.indexOf('\n', i, imax)) > -1)
        {
            StringLines.addElement(i, nl);
            StringLines.GetLineFromItems(nl, nl + 1);
            i = nl + 1;
        }
        StringLines.addElement(i, imax);

        textheight = 0;
        for (i = 0; i < StringLines.size(); i++)
            textheight += StringLines.elementAt(i).getHeight();
    }
    
    public void DrawMultStr()
    {
        int i = 0, i_first = 0;
        int i_cnt = StringLines.size();
        int y1;
        y1 = y0;
        LineOfItems loi = null;

        hyp_mngr.reset();
        for (; i < i_cnt; i++)
        {
            loi = (LineOfItems)StringLines.elementAt(i);
            int hStr = loi.getHeight();
            if ((y1 + hStr) > 0)
            {
                i_first = i;
                break;
            }
            y1 += hStr;
        }
        for (; i < i_cnt; i++)
        {
            loi = (LineOfItems)StringLines.elementAt(i);
            int hStr = loi.getHeight();
            loi.draw(x + 1, y + y1);
            y1 += hStr;
            if (y1 > h){break;}
        }

        if (loi != null)
        {
            int cur = hyp_mngr.getCurrent();
            if (cur > -1)
            {
                int ln;
                Vector LineNumbers = hyp_mngr.getLnFromHypNumber(cur);
                int LineNumbersSize = LineNumbers.size() - 1;
                if (nav_mode == nm_hyp)
                {
                    int t = 0;
                    // двигаемся вниз
                    if(cur >= hyp_mngr.getLastDrawed())
                    {
                        ln = ((HyperLinkItemLn)LineNumbers.elementAt(LineNumbersSize)).getLineNumber();

                        i++;
                        for (; i <= ln; i++)
                        {
                            loi = (LineOfItems)StringLines.elementAt(i);
                            int hStr = loi.getHeight();
                            t -= hStr;
                        }
                    }
                    else // двигаемся вверх
                        if(cur <= hyp_mngr.getFirstDrawed())
                        {
                            //ln = ((Integer)LineNumbers.elementAt(0)).intValue();
                            ln = ((HyperLinkItemLn)LineNumbers.elementAt(0)).getLineNumber();

                            i = i_first;
                            for (; i > ln; i--)
                            {
                                loi = (LineOfItems)StringLines.elementAt(i);
                                int hStr = loi.getHeight();
                                t += hStr;
                            }
                        }

                    if (t != 0)
                        y0 += t;
                }
                else
                {
                    ln = ((HyperLinkItemLn)LineNumbers.elementAt(0)).getLineNumber();
                    if(i_first > ln)
                    {
                        for (int j = cur + 1; j < hyp_mngr.getTotal(); j++)
                        {
                            LineNumbers = hyp_mngr.getLnFromHypNumber(j);
                            ln = ((HyperLinkItemLn)LineNumbers.elementAt(0)).getLineNumber();
                            if(i_first <= ln)
                            {
                                hyp_mngr.setCurrent(j);
                                break;
                            }
                        }
                    }
                    else
                    {
                        ln = ((HyperLinkItemLn)LineNumbers.elementAt(LineNumbersSize)).getLineNumber();
                        if(i < ln)
                        {
                            for (int j = cur - 1; j >= 0; j--)
                            {
                                LineNumbers = hyp_mngr.getLnFromHypNumber(j);
                                ln = ((HyperLinkItemLn)LineNumbers.elementAt(LineNumbers.size() - 1)).getLineNumber();
                                if(i >= ln)
                                {
                                    hyp_mngr.setCurrent(j);
                                    break;
                                }
                            }
                        }
                    }
                }
                canv.repaint();
            }
        }
    }
}
