/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package probolego;

/**
 *
 * @author Rogerio
 */
public class Cor {
    public int red, green, blue;
    
    public Cor(int red, int green, int blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    public Cor( java.awt.Color c )
    {
        red = c.getRed();
        green = c.getGreen();
        blue = c.getBlue();
    }
}
