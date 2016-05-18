
import java.awt.Graphics;
import java.awt.Rectangle;
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
public class Powerup {
    String powerUpName;
    int xPos, yPos;
    BufferedImage img;

    int scaling;
    int powerUpDimension;

    int counter = 0;
    boolean active = true;
    
    public Powerup(int x, int y, String name, int sc){
        xPos = x;
        yPos = y;
        powerUpName = name;
        scaling = sc;

        powerUpDimension = 30 * scaling;
    }
    
    public void draw(Graphics g) throws IOException{
        try{
            if(powerUpName.equals("shield")){
                img = ImageIO.read(new File("resources/Shield.png"));
            } else{
                img = ImageIO.read(new File("resources/Multishot.png"));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        
        g.drawImage(img, xPos, yPos, powerUpDimension, powerUpDimension, null);
    }
    
    public void animate(){
        counter++;
        if(counter == 20){
            yPos -= 3;
        } else if(counter == 40){
            yPos += 3;
            counter = 0;
        }
        
        if(!active){
            GameCanvas.spaceInvaders.powerUpList.remove(this);
        }
    }
    
    public Rectangle getBounds(){ return new Rectangle(xPos, yPos, powerUpDimension, powerUpDimension); }
    
    public String getName(){
        return powerUpName;
    }
}
