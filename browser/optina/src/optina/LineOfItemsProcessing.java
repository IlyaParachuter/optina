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
public class LineOfItemsProcessing implements InterfaceLineOfItemsProcessing {
    private InterfaceLineOfItemsProcessing component;
    
    public LineOfItemsProcessing(InterfaceLineOfItemsProcessing c)
    {component = c;}

    public void process(LineOfItems loi)
    {component.process(loi);}
    public void flush()
    {component.flush();}
    public void close()
    {component.close();}
}
