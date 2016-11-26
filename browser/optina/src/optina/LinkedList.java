/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optina;

import java.util.Enumeration;

/**
 *
 * @author Goncharov.Ilia
 */
public class LinkedList {
    private LLBaseNode header;
    private LLNode head, tail;
    private int size = 0;

    public LinkedList()
    {
        head = tail = null;
        header = new LLBaseNode();
        header.next = head;
    }

    public void insertFirst(Object i)
    {
        if(head == null)
        {
            head = tail = new LLNode(i);
            header.next = head;
        }
        else
        {
            LLNode t = new LLNode(i);
            LLNode headCopy = head;
            head = t;
            t.next = headCopy;
            headCopy.prev = t;
        }
        size++;
    }

    public void insertLast(Object i)
    {
        if(tail == null)
        {
            tail = head = new LLNode(i);
            header.next = head;
        }
        else
        {
            LLNode t = new LLNode(i);
            LLNode rear_copy = tail;
            tail = t;
            t.prev = rear_copy;
            rear_copy.next = t;
        }
        size++;
    }

    public LLNode removeFirst()
    {
        LLNode res;
        if(head == null)
            res = head;
        else
        {
            LLNode t = head;
            head = head.next;
            if (head == null)
                header.next = tail = null;
            else
                head.prev = null;

            res = t;
        }
        size--;
        return res;
    }

    public LLNode removeLast()
    {
        LLNode res;
        if(tail == null)
            res = tail;
        else
        {
            LLNode t = tail;
            tail = tail.prev;
            if (tail == null)
                header.next = head = null;
            else
                tail.next = null;
            res = t;
        }
        size--;
        return res;
    }
    
    public LLNode getHead()
    {return head;}
    public LLNode getTail()
    {return tail;}
    public int getSize()
    {return size;}
    public Enumeration elements()
    {return new LinkedListEnumeration(header);}
}
