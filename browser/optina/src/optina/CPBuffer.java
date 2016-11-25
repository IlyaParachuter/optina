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
public class CPBuffer implements InterfaceCharacterProcessing {
    private StringBuffer buf;
    
    public CPBuffer()
    {buf = new StringBuffer();}

    public void process(char c)
    {buf.append(c);}
    public void close()
    {}
    
    public String getBuffer()
    {return buf.toString();}
}
