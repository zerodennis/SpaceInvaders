
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
public class Bullet {
    BufferedImage img;
    int xPos, yPos, width, height;
    String direction;
    boolean playerHit = false;
    
    public Bullet(int x, int y, int w, int h, String d){
        xPos = x;
        yPos = y;
        width = w;
        height = h;
        direction = d;
    }
    
    public Rectangle getBounds(){ return new Rectangle(xPos, yPos, width , height);}
    
    public void draw(Graphics g) throws IOException{
        try{
            img = ImageIO.read(new File("Bullet.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
        
        g.drawImage(img, xPos, yPos, width, height, null);
        
        //g.drawRect(xPos, yPos, width, height);
        //animate();
    }
    
    public void animate(){
        if(direction.equals("Up")){
            yPos -= 10;
        }else{
            yPos += 10;
        }
        
        if(playerHit == true || yPos > 800 || yPos < 0){
            GameCanvas.spaceInvaders.bulletList.remove(this);
        }
    }
    
    public void playerCollision(){
        Rectangle player = GameCanvas.spaceInvaders.player1.getBounds();
        
        if(player.intersects(getBounds())){
            //nothing
        }
    }
}
