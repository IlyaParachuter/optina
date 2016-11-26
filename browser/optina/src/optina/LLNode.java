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
public class LLNode extends LLBaseNode{
    private Object data;
    public LLNode prev;

    public LLNode(Object item)
    {
        data = item;
        next = prev = null;
    }
    
    public Object getData()
    {return data;}
}
