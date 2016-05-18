
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
    int xPos, yPos, width, height, yBorder, scaling;
    String direction;
    boolean playerHit = false;
    
    public Bullet(int x, int y, int w, int h, String d, int yB, int sc){
        xPos = x;
        yPos = y;
        width = w;
        height = h;
        direction = d;
        yBorder = yB;
        scaling = sc;
    }
    
    public Rectangle getBounds(){ return new Rectangle(xPos, yPos, width , height);}
    
    public void draw(Graphics g) throws IOException{
        try{
            img = ImageIO.read(new File("resources/Bullet.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
        
        if(direction.equals("Up")){
            g.drawImage(img, xPos, yPos, width, height, null);
        } else{
            AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -img.getHeight(null));
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            img = op.filter(img, null);
            
            g.drawImage(img, xPos, yPos, width, height, null);
        }
        
        //g.drawRect(xPos, yPos, width, height);
        //animate();
    }
    
    public void animate(){
        if(direction.equals("Up")){
            yPos -= 6;
        }else{
            yPos += 6;
        }
        
        if(playerHit || yPos > (yBorder + 800)*scaling || yPos < yBorder*scaling){
            if(direction.equals("Up")){
                GameCanvas.spaceInvaders.bulletList1.remove(this);
            } else{
                GameCanvas.spaceInvaders.bulletList2.remove(this);
            }
        }
    }
}
