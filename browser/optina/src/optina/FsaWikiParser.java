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
public class FsaWikiParser implements InterfaceCharacterProcessing {
    final int fsa_start = 0;
    final int fsa_hyp_start = 1;
    final int fsa_hyp_start_c2 = 2;
    final int fsa_hyp_end = 3;
    final int fsa_heading_start = 4;
    final int fsa_heading_title = 5;
    final int fsa_heading_end = 6;

    private int state;
    private int src_pos;
    private int dst_start;
    private int dst_end;
    private int item_start;
    private int item_mid;
    private int plain_text_start;
    private int heading_lvl;
    private int heading_lvl_end;
    private int numhyp;
    private String splain_text;
    private String sbuf1;
    private String sbuf2;
    private Vector queue;
    private HyperLinkManager hyp_mngr;
    private Graphics g;
    
    public FsaWikiParser(Graphics vg, HyperLinkManager vhyp_mngr, Vector vqueue)
    {
        g = vg;
        hyp_mngr = vhyp_mngr;
        queue = vqueue;
        src_pos = 0;
        dst_start = 0;
        dst_end = 0;
        splain_text = "";
        sbuf1 = "";
        sbuf2 = "";
        item_start = 0;
        item_mid = 0;
        plain_text_start = 0;
        heading_lvl = 0;
        heading_lvl_end = 0;
        numhyp = -1;
    }
    
    public void close()
    {
        if (src_pos > plain_text_start)
        {
            int splain_text_len = splain_text.length() - 1;
            dst_end = dst_start + splain_text_len - 1;
            PlainTextItem plt = new PlainTextItem(g, dst_start, dst_end, splain_text.substring(0, splain_text_len));
            dst_start = dst_end + 1;
            splain_text = "";
            queue.addElement(plt);
        }
    }

    final public void process(char c)
    {
        splain_text += c;

        switch (state)
        {
            case fsa_start:
                boolean bHit = true;
                switch (c)
                {
                    case '[':
                        item_start = src_pos;
                        item_mid = -1;
                        state = fsa_hyp_start;
                        break;
                    case '=':
                        item_start = src_pos;
                        heading_lvl= 1;
                        state = fsa_heading_start;
                        break;
                    default:
                        if (bHit)
                            bHit = false;
                }

                if (bHit && (src_pos > plain_text_start))
                {
                    int splain_text_len = splain_text.length() - 1;
                    dst_end = dst_start + splain_text_len - 1;
                    PlainTextItem plt = new PlainTextItem(g, dst_start, dst_end, splain_text.substring(0, splain_text_len));
                    dst_start = dst_end + 1;
                    splain_text = "";
                    queue.addElement(plt);

                }
                break;

            // hyperlink
            case fsa_hyp_start:
                if (c == '[')
                {
                    sbuf1 = "";
                    state = fsa_hyp_start_c2;
                }
                else
                    state = fsa_start;

                break;
            case fsa_hyp_start_c2:
                if ((item_mid == -1) && (c == '|'))
                {
                    sbuf2 = "";
                    item_mid = src_pos;
                }
                else
                if (c == ']')
                    state = fsa_hyp_end;
                else
                if(item_mid == -1)
                    sbuf1 += c;
                else
                    sbuf2 += c;
                break;
            case fsa_hyp_end:
                if (c == ']')
                {
                    dst_end = dst_start + sbuf2.length() - 1;
                    numhyp += 1;
                    HyperLinkItem hyp = new HyperLinkItem(g, hyp_mngr, dst_start, dst_end, sbuf2, numhyp, sbuf1);
                    // Здесь увеличиваем общее количество. Сами гиперлинки будем регистрировать позже, при разбиении строк.
                    hyp_mngr.incTotal();
                    dst_start = dst_end + 1;
                    queue.addElement(hyp);
                    plain_text_start = src_pos + 1;
                    splain_text = "";
                }
                state = fsa_start;
                break;

            // heading
            case fsa_heading_start:
                if (c == '=')
                    heading_lvl++;
                else
                {
                    sbuf1 = "" + c;
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
                    sbuf1 += c;
                break;
            case fsa_heading_end:
                if (c == '=')
                {
                    heading_lvl_end++;
                    if (heading_lvl_end == heading_lvl)
                    {
                        dst_end = dst_start + sbuf1.length() - 1;
                        HeadingItem hdi = new HeadingItem(g, dst_start, dst_end, sbuf1, heading_lvl);
                        dst_start = dst_end + 1;
                        sbuf1 = "";
                        queue.addElement(hdi);
                        plain_text_start = src_pos + 1;
                        splain_text = "";
                        state = fsa_start;
                    }
                }
                else
                    if (heading_lvl_end < heading_lvl)
                        state = fsa_start;
                break;
        }
        src_pos++;
    }
}
