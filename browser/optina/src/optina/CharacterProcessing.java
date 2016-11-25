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
public class CharacterProcessing implements InterfaceCharacterProcessing {
    protected InterfaceCharacterProcessing component;
    
    public CharacterProcessing(InterfaceCharacterProcessing c)
    {component = c;}
    
    public void process(char c)
    {component.process(c);}
    public void close()
    {component.close();}
}
