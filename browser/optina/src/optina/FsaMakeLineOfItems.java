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
public class FsaMakeLineOfItems extends LineOfItemsProcessing implements InterfaceItemProcessing {
    final int fsa_start = 0;
    final int fsa_work = 1;

    private int state = fsa_start;
    private long start_of_line = 0, end_of_line = 0;
    private int line_width = 0;
    private int w;
    private ItemsQueue iq;
    private int ln = 0;

    public LineOfItems GetLineFromItems(long start, long end)
    {
        ItemVisitor vis = new ItemVisitor(ln);
        BaseItem bi;

        while ((bi = iq.peek()) != null)
        {
            long bi_start = bi.getStart();
            long bi_end = bi.getEnd();
            long iend = end - 1;

            if ((bi_start >= start) && (bi_end <= iend))
            {
                bi.accept(vis);
                iq.remove();
            }
            else
            if (((bi_start >= start) && (bi_start <= iend)) && (bi_end > iend))
            {
                bi.part(bi_start, iend).accept(vis);
                break;
            }
            else
            if ((bi_start < start) && ((bi_end >= start) && (bi_end <= iend)))
            {
                bi.part(start, bi_end).accept(vis);
                iq.remove();
            }
            else
            if ((bi_start < start) && ((bi_end >= start) && (bi_end > iend)))
            {
                bi.part(start, iend).accept(vis);
                break;
            }
            else
                break;
        }
        return vis.getLine();
    }

    public FsaMakeLineOfItems(InterfaceLineOfItemsProcessing c, int width)
    {
        super(c);
        w = width;
        //iq = new ItemsQueue();
        iq = new ItemsQueue();
    }
    
    public void process(BaseItem bi)
    {
        switch (state)
        {
            case fsa_start:
                state = fsa_work;

            case fsa_work:
                iq.addElement(bi);
                int bi_length = bi.length();
                int prev_peace;
                int next_peace = 0;
                int prev_nl = 0;

                do
                {
                    prev_peace = next_peace;
                    int next_space = bi.indexOf(' ', prev_peace);
                    int next_nl = bi.indexOf('\n', prev_peace);
                    if (next_space == -1)
                        next_peace = bi_length;
                    else
                    {
                        if ((next_nl != -1) && (next_nl < next_space))
                            next_peace = next_nl + 1;
                        else
                            next_peace = next_space + 1;
                    }
                    
                    int next_width = bi.stringWidth(prev_peace, next_peace);

                    if (line_width + next_width < w)
                    {//Слово умещается
                        while (true)
                        {
                            if (prev_nl != -1)
                                next_nl = bi.indexOf('\n', prev_nl, next_peace);

                            if (next_nl == -1)
                            {
                                line_width += next_width;
                                prev_nl = next_peace;
                                break;
                            }
                            else
                            {
                                long bi_start = bi.getStart();
                                long bi_start_next_nl = bi_start + next_nl;
                                ProcessLine(start_of_line, bi_start_next_nl);
                                if (next_nl == bi_length - 1)
                                    GetLineFromItems(bi_start_next_nl, bi_start + bi_length);

                                start_of_line = bi_start_next_nl + 1;
                                next_width = bi.stringWidth(next_nl + 1, next_peace);
                                line_width = 0;
                                prev_nl = next_nl + 1;
                                state = fsa_start;
                            }
                        } // while (true)
                    } // if (line_width + next_width < w)
                    else
                    {//Слово не умещается
                        ProcessLine(start_of_line, end_of_line);
                        start_of_line = end_of_line;
                        line_width = next_width;
                        state = fsa_start;
                    }
                    end_of_line += (next_peace - prev_peace);
                } while (next_peace != bi_length);
                
                break;
        }
    }

    public void ProcessLine(long start, long end)
    {
        super.process(GetLineFromItems(start, end));
        ln++;
    }
    
    public void flush()
    {
        /*long i = 0;
        long imax = iq.length();
        
        if (imax > 0)
        {
            long nl;
            while ((nl = iq.indexOf('\n', i)) > -1)
            {
                ProcessLine(i, nl);
                GetLineFromItems(nl, nl + 1);
                i = nl + 1;
            }
            ProcessLine(i, imax);
        }
        state = fsa_start;
        super.flush();
        */
    }
}
