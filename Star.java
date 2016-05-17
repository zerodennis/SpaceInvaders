
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
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
public class Star {
    int xPos, yPos;
    BufferedImage img;
    
    public Star(int x,int y){
        xPos = x;
        yPos = y;
    }
    
    public void draw(Graphics g) throws IOException{
        try{
            img = ImageIO.read(new File("resources/Star.png"));
        } catch(IOException e){
            e.printStackTrace();
        }
        
        g.drawImage(img, xPos, yPos, 5, 5, null);
    }
    
    public void animate(){
        yPos -= 2;
        
        if(yPos < 0){
            int min = 0, max = 780;
            Random random = new Random();
            int randX = random.nextInt(max - min + 1) + min;
            
            xPos = randX;
            yPos = 800;
        }
    }
}
