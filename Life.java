
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fielfaustino
 */
public class Life {
    BufferedImage img;
    
    public void draw(Graphics g, int x, int y) throws IOException{
        img = ImageIO.read(new File("resources/Life.png"));
        
        g.drawImage(img, x, y, 70, 70, null);
    }
}
