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
public class ItemProcessing implements InterfaceItemProcessing {
    protected InterfaceItemProcessing component;

    public ItemProcessing(InterfaceItemProcessing c)
    {component = c;}

    public void process(BaseItem bi)
    {component.process(bi);}
    public void flush()
    {component.flush();}
    public void close()
    {component.close();}
}
