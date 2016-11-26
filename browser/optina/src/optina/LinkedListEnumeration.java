/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package optina;

import java.util.Enumeration;

/**
 *
 * @author home
 */
public class LinkedListEnumeration implements Enumeration {
    private LLBaseNode current;

    public LinkedListEnumeration(LLBaseNode current)
    {
        this.current = current;
    }
    public LinkedListEnumeration(LinkedListEnumeration other)
    {current = other.current;}

    public boolean hasMoreElements()
    {return (current.next != null);}

    public Object nextElement()
    {
        LLNode res = current.next;
        current = current.next;
        return res;
    }
}
