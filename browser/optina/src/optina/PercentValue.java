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
public class PercentValue {
    private int current;
    private int max;
    
    public PercentValue()
    {
        current = 0;
        max = 0;
    }
    
    public void setMax(int i)
    {max = i;}
    public void setCurrent(int i)
    {current = i;}
    public int get()
    {
        return (max == 0) ? 0 : (current * 100) / max;
    }
}
