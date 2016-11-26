/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Goncharov.Ilia
 */
public class IndexElement {
    public final static int on_disk_size = 20;
    private int y;
    private long ofs;
    private long start;

    public IndexElement(LineOfItems loi, int y)
    {this(y, loi.getOfs(), loi.getStart());}
    public IndexElement(int y, DataInputStream dis) throws IOException
    {this(y, dis.readLong(), dis.readLong());}

    public IndexElement(int y, long ofs, long start)
    {
        this.y = y;
        this.ofs = ofs;
        this.start = start;
    }
    
    public void write(DataOutputStream dos) throws IOException
    {
        dos.writeInt(y);
        dos.writeLong(ofs);
        dos.writeLong(start);
    }

    public int getY()
    {return y;}
    public long getOfs()
    {return ofs;}
    public long getStart()
    {return start;}
}
