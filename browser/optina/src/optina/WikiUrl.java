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
public class WikiUrl {
    private String url;

    public WikiUrl(String url)
    {
        this.url = url;
    }
    
    public String getFilePath()
    {
        String res;

        res = url.replace(':', '_');
        res = res.replace(';', '_');
        int i = res.indexOf('#'); // todo: реализовать переход по хештегу
        if (i > -1)
            res = res.substring(0, i);

        // dbg:
        //res = "test";
        return "file:///memorycard/opt/" + res;
    }
}
