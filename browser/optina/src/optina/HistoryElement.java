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
public class HistoryElement {
    private String url;
    private int current_hyp;
    
    public HistoryElement(String url, int current_hyp)
    {
        this.url = url;
        this.current_hyp = current_hyp;
    }
    
    public String getUrl()
    {return url;}
    public int getCurrentHyp()
    {return current_hyp;}
    public void setCurrentHyp(int n)
    {current_hyp = n;}
}
