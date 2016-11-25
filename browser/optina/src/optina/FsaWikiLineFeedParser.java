/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

/**
 *
 * @author Goncharov.Ilia
 */
public class FsaWikiLineFeedParser extends CharacterProcessing {
    final int fsa_after_r_wait = 1;
    final int fsa_after_rn_wait = 2;
    final int fsa_after_rnr_wait = 3;
    final int fsa_start = 0;
    private int state = fsa_start;
    private int src_pos;

    public FsaWikiLineFeedParser(InterfaceCharacterProcessing c)
    {
        super(c);
        src_pos = 0;
    }
    
    public void process(char c)
    {
        // разбираемся с переносами:
        //parse_percent.setMax(content_len - 1);

            //parse_percent.setCurrent(src_pos);

        switch (state)
        {
            case fsa_start:
                switch (c)
                {
                    case '\r':
                        state = fsa_after_r_wait;
                        break;
                    default:
                        super.process(c);
                }
                break;
            case fsa_after_r_wait:
                switch (c)
                {
                    case '\n':
                        state = fsa_after_rn_wait;
                        break;
                    default:
                        super.process(c);
                        state = fsa_start;
                }
                break;
            case fsa_after_rn_wait:
                switch (c)
                {
                    case '\r':
                        state = fsa_after_rnr_wait;
                        break;
                    case ' ':
                        super.process('\n');
                        state = fsa_start;
                        break;
                    default:
                        super.process(' ');
                        super.process(c);
                        state = fsa_start;
                }
                break;
            case fsa_after_rnr_wait:
                switch (c)
                {
                    case '\n':
                        super.process(c);
                        break;
                    default:
                        super.process(' ');
                }
                state = fsa_start;
                break;
        }
        src_pos++;
    }
}
