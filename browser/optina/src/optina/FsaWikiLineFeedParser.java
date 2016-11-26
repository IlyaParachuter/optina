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
    private int ofs = 0;
    private int state = fsa_start;

    public FsaWikiLineFeedParser(InterfaceCharacterProcessing c)
    {super(c);}
    
    public void process(char c, int ofs)
    {
        switch (state)
        {
            case fsa_start:
                this.ofs = ofs;
                switch (c)
                {
                    case '\r':
                        state = fsa_after_r_wait;
                        break;
                    default:
                        super.process(c, this.ofs);
                }
                break;
            case fsa_after_r_wait:
                switch (c)
                {
                    case '\n':
                        state = fsa_after_rn_wait;
                        break;
                    default:
                        super.process(c, this.ofs);
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
                        super.process('\n', this.ofs);
                        state = fsa_start;
                        break;
                    default:
                        super.process(' ', this.ofs);
                        super.process(c, this.ofs);
                        state = fsa_start;
                }
                break;
            case fsa_after_rnr_wait:
                switch (c)
                {
                    case '\n':
                        super.process(c, this.ofs);
                        break;
                    default:
                        super.process(' ', this.ofs);
                }
                state = fsa_start;
                break;
        }
    }
}
