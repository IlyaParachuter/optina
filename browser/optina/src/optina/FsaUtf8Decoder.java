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
public class FsaUtf8Decoder extends CharacterProcessing {
    final int fsa_start = 0;
    final int fsa_wait_234_c2 = 1;
    final int fsa_wait_234_c3 = 2;
    final int fsa_wait_234_c4 = 3;
    final int fsa_wait_23_c2 = 4;
    final int fsa_wait_23_c3 = 5;
    final int fsa_wait_2_c2 = 6;

    private int ofs = 0;
    private int state = fsa_start;
    private int c1, c2, c3, c4;

    public FsaUtf8Decoder(InterfaceCharacterProcessing c)
    {super(c);}
    
    public void process(char c, int ofs)
    {
        switch (state)
        {
            case fsa_start:
                this.ofs = ofs;
                c1 = c;
                if (c1 < 0x80)
                    super.process((char)c1, ofs);
                else
                if (c1 > 0xef && c1 < 0xf8)
                    state = fsa_wait_234_c2;
                else
                if (c1 > 0xdf)
                    state = fsa_wait_23_c2;
                else
                if (c1 > 0xbf)
                    state = fsa_wait_2_c2;
                //else // иначе надо бросить исключение

                break;

            case fsa_wait_234_c2:
                c2 = c;
                state = fsa_wait_234_c3;
                break;
            case fsa_wait_234_c3:
                c3 = c;
                state = fsa_wait_234_c4;
                break;
            case fsa_wait_234_c4:
                c4 = c;
                super.process((char)((c1-0xf0)*0x40000+(c2-0x80)*0x1000+(c3-0x80)*0x40+c4-0x80), this.ofs);
                state = fsa_start;
                break;

            case fsa_wait_23_c2:
                c2 = c;
                state = fsa_wait_23_c3;
                break;
            case fsa_wait_23_c3:
                c3 = c;
                super.process((char)((c1-0xe0)*0x1000+(c2-0x80)*0x40+c3-0x80), this.ofs);
                state = fsa_start;
                break;

            case fsa_wait_2_c2:
                c2 = c;
                super.process((char)((c1-0xc0)*0x40+c2-0x80), this.ofs);
                state = fsa_start;
                break;
        }
    }
}
