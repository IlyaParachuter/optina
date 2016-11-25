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
public class ItemVisitor {
    private LineOfItems line;
    private int ln;

    public ItemVisitor(int vln)
    {
        line = new LineOfItems();
        ln = vln;
    }
    
    public LineOfItems getLine()
    {return line;}
    public void visit(HyperLinkItem item)
    {line.addElement(new HyperLinkItemLn(item, ln));}
    public void visit(PlainTextItem item)
    {line.addElement(item);}
    public void visit(HeadingItem item)
    {line.addElement(item);}
}
