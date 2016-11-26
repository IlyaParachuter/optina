/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 *
 * @author Goncharov.Ilia
 */
public class IndexFile {
    private FileConnection fc;
    DataInputStream disIdx = null;
    private OutputStream osIdx = null;
    private DataOutputStream dosIdx = null;

    public IndexFile(String path) throws IOException
    {
        fc=(FileConnection)Connector.open(path, Connector.READ_WRITE);
        if (!fc.canRead())
            fc.create();
    }
    
    public void write(LineOfItems loi, int y) throws IOException
    {
        int idx_ofs = loi.getLineNumber() * IndexElement.on_disk_size;
        if (idx_ofs >= fc.fileSize())
        {
            if (osIdx == null)
            {
                osIdx = fc.openOutputStream(idx_ofs);
                dosIdx = new DataOutputStream(osIdx);
            }
            write(new IndexElement(loi, y));
        }
    }

    private void write(IndexElement ie) throws IOException
    {ie.write(dosIdx);}
    
    private IndexElement find(DataInputStream dis, int y, long skipped, long start, long end) throws IOException
    {
        IndexElement res;
        long mid = start + ((end - start + 1) / (2 * IndexElement.on_disk_size)) * IndexElement.on_disk_size;

        skipped = mid - skipped;
        dis.skip(skipped);
        int t = dis.readInt();
        
        if (t == y)
        {
            res = new IndexElement(t, dis);
            dis.close();
        }
        else
        {
            if (t > y)
            {
                dis.close();
                res = find(fc.openDataInputStream(), y, 0, start, mid);
            }
            else // t < y
            {
                skipped += IndexElement.on_disk_size; // пропускаем текущий элемент
                if (skipped >= end)
                {
                    res = new IndexElement(t, dis);
                    dis.close();
                }
                else
                {
                    dis.skip(IndexElement.on_disk_size - 4);
                    res = find(dis, y, skipped, mid, end);
                }
            }
        }
        return res;
    }
    
    public IndexElement read(int y) throws IOException // read ofs
    {
        long size = fc.fileSize();
        DataInputStream dis = fc.openDataInputStream();
        return find(dis, y, 0, 0, size - 1);
    }
    
    public void close() throws IOException
    {
        if (dosIdx != null)
        {
            dosIdx.close();
            dosIdx = null;
        }
        if (osIdx != null)
        {
            osIdx.close();
            osIdx = null;
        }
    }
}
