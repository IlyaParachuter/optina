/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import java.util.Vector;
import javax.microedition.lcdui.*;

/**
 *
 * @author Goncharov.Ilia
 */
public class FsaWikiParser extends ItemProcessing implements InterfaceCharacterProcessing {
    final int fsa_start = 0;
    final int fsa_hyp_start = 1;
    final int fsa_hyp_start_c2 = 2;
    final int fsa_hyp_end = 3;
    final int fsa_heading_start = 4;
    final int fsa_heading_title = 5;
    final int fsa_heading_end = 6;

    private long ofs = 0;
    private int state = fsa_start;
    private long i;
    private long dst_start;
    private long dst_end;
    private long item_mid;
    private long plain_text_start;
    private int heading_lvl;
    private int heading_lvl_end;
    private int numhyp;
    private long plain_text_ofs = 0;
    private String plain_text;
    private String buf1;
    private String buf2;
    //private Vector queue;
    private HyperLinkManager hyp_mngr;
    private Graphics g;
    
    public FsaWikiParser(InterfaceItemProcessing c, Graphics g, int ofs, HyperLinkManager hyp_mngr)
    {
        super(c);
        this.g = g;
        this.hyp_mngr = hyp_mngr;
        reset();
    }
    
    public void reset()
    {
        i = 0;
        dst_start = 0;
        dst_end = 0;
        plain_text = "";
        buf1 = "";
        buf2 = "";
        item_mid = 0;
        plain_text_start = 0;
        heading_lvl = 0;
        heading_lvl_end = 0;
        numhyp = -1;
    }
    
    public void flush()
    {
        if (i > plain_text_start)
        {
            int splain_text_len = plain_text.length() - 1;
            dst_end = dst_start + splain_text_len - 1;
            PlainTextItem plt = new PlainTextItem(g, plain_text_ofs, dst_start, dst_end, plain_text.substring(0, splain_text_len));
            dst_start = dst_end + 1;
            plain_text_ofs = ofs;
            plain_text = "";
            super.process(plt);
        }
        super.flush();
    }

    final public void process(char c, int ofs)
    {
        plain_text += c;

        switch (state)
        {
            case fsa_start:
                this.ofs = ofs;
                boolean bHit = true;
                switch (c)
                {
                    case '[':
                        item_mid = -1;
                        state = fsa_hyp_start;
                        break;
                    case '=':
                        heading_lvl= 1;
                        state = fsa_heading_start;
                        break;
                    default:
                        if (bHit)
                            bHit = false;
                }

                if (bHit && (i > plain_text_start))
                {
                    int splain_text_len = plain_text.length() - 1;
                    dst_end = dst_start + splain_text_len - 1;
                    PlainTextItem plt = new PlainTextItem(g, plain_text_ofs, dst_start, dst_end, plain_text.substring(0, splain_text_len));
                    dst_start = dst_end + 1;
                    plain_text_ofs = ofs;
                    plain_text = "";
                    //queue.addElement(plt);
                    super.process(plt);
                    plain_text_start = i + 1;
                }
                break;

            // hyperlink
            case fsa_hyp_start:
                if (c == '[')
                {
                    buf1 = "";
                    state = fsa_hyp_start_c2;
                }
                else
                    state = fsa_start;

                break;
            case fsa_hyp_start_c2:
                if ((item_mid == -1) && (c == '|'))
                {
                    buf2 = "";
                    item_mid = i;
                }
                else
                if (c == ']')
                    state = fsa_hyp_end;
                else
                if(item_mid == -1)
                    buf1 += c;
                else
                    buf2 += c;
                break;
            case fsa_hyp_end:
                if (c == ']')
                {
                    dst_end = dst_start + buf2.length() - 1;
                    numhyp += 1;
                    HyperLinkItem hyp = new HyperLinkItem(g, this.ofs, hyp_mngr, dst_start, dst_end, buf2, numhyp, buf1);
                    // Здесь увеличиваем общее количество. Сами гиперлинки будем регистрировать позже, при разбиении строк.
                    hyp_mngr.incTotal();
                    dst_start = dst_end + 1;
                    //queue.addElement(hyp);
                    super.process(hyp);
                    plain_text_start = i + 1;
                    plain_text_ofs = ofs;
                    plain_text = "";
                }
                state = fsa_start;
                break;

            // heading
            case fsa_heading_start:
                if (c == '=')
                    heading_lvl++;
                else
                {
                    buf1 = "" + c;
                    state = fsa_heading_title;
                }
                break;
            case fsa_heading_title:
                if (c == '=')
                {
                    heading_lvl_end = 1;
                    state = fsa_heading_end;
                }
                else
                    buf1 += c;
                break;
            case fsa_heading_end:
                if (c == '=')
                {
                    heading_lvl_end++;
                    if (heading_lvl_end == heading_lvl)
                    {
                        dst_end = dst_start + buf1.length() - 1;
                        HeadingItem hdi = new HeadingItem(g, this.ofs, dst_start, dst_end, buf1, heading_lvl);
                        dst_start = dst_end + 1;
                        buf1 = "";
                        //queue.addElement(hdi);
                        super.process(hdi);
                        plain_text_start = i + 1;
                        plain_text_ofs = ofs;
                        plain_text = "";
                        state = fsa_start;
                    }
                }
                else
                    if (heading_lvl_end < heading_lvl)
                        state = fsa_start;
                break;
        }
        i++;
    }
}
