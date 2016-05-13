
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
public class Player {
    BufferedImage img;
    int xPos;
    int yPos;
    int width;
    int height;
    int playerNum;
    
    int xVelocity = 0;
    int yVelocity = 0;
    final int xSpeed = 1;
    int maxXVelocity = 15;
    int maxYVelocity = 15;
    
    boolean movingLeft = false;
    boolean movingRight = false;
    boolean movingUp = false;
    boolean movingDown = false;
    
    int life = 3;
    boolean alive = true;
    
    ArrayList<Life> lifeList = new ArrayList<Life>();
    
    public Player(int x, int y, int w, int h, int num){
        xPos = x;
        yPos = y;
        width = w;
        height = h;
        playerNum = num;
        
        lifeList.add(new Life());
        lifeList.add(new Life());
        lifeList.add(new Life());
        
    }
    
    public Rectangle getBounds(){ return new Rectangle(xPos, yPos, width , height); }
    
    public void draw(Graphics g) throws IOException{
        try{
            img = ImageIO.read(new File("resources/Spaceship.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
        
        if(playerNum == 1){
            g.drawImage(img, xPos, yPos, width, height, null);
        } else{
            AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -img.getHeight(null));
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            img = op.filter(img, null);
            
            g.drawImage(img, xPos, yPos, width, height, null);
        }
        
        
        if(playerNum == 1){
            int x = -5;
            for(int i = 0; i < lifeList.size(); i++){
                
                lifeList.get(i).draw(g, x + (i*50), 730);
            }
        } else{
            int x = 640;
            for(int i = 0; i < lifeList.size(); i++){
                lifeList.get(i).draw(g, x + (i*50), 0);
            }
        }
        
        //g.drawRect(xPos, yPos, width, height);
        //animate();
    }
    
    public void animate() {
        //Character Movement        
        if (movingLeft) {
            xPos -= xVelocity;

            xVelocity += xSpeed;
            if (xVelocity >= maxXVelocity) {
                xVelocity = maxXVelocity;
            }
            
            if(xPos <= 0){
                xPos = 0;
            }
        } else if (movingRight) {
            xPos += xVelocity;
            
            xVelocity += xSpeed;
            if (xVelocity >= maxXVelocity) {
                xVelocity = maxXVelocity;
            }
            
            if(xPos >= 750){
                xPos = 750;
            }
        } else if(movingUp){
            yPos -= xVelocity;
            
            xVelocity += xSpeed;
            if (xVelocity >= maxXVelocity) {
                xVelocity = maxXVelocity;
            }
            
            if(yPos <= 0){
                yPos = 0;
            }
        } else if(movingDown){
            yPos += xVelocity;
            
            xVelocity += xSpeed;
            if (xVelocity >= maxXVelocity) {
                xVelocity = maxXVelocity;
            }
            
            if(yPos >= 730){
                yPos = 730;
            }
        }
        
        bulletCollision();
    }
    
    public void fire(){
        Bullet bullet;
        if(playerNum == 1){
            bullet = new Bullet(xPos + (width/2 - 5), yPos - 10, 10, 10, "Up");            
        }else{
            bullet = new Bullet(xPos + (width/2 - 5), yPos + height + 5, 10, 10, "Down");
        }
        
        GameCanvas.spaceInvaders.bulletList.add(bullet);
    }
    
    public void bulletCollision(){
        for(int i = 0; i <= GameCanvas.spaceInvaders.bulletList.size() - 1; i++){
            Rectangle bullet = GameCanvas.spaceInvaders.bulletList.get(i).getBounds();
            
            if(bullet.intersects(getBounds())){
                //System.out.println("Hit");
                GameCanvas.spaceInvaders.bulletList.get(i).playerHit = true;
                
                if(alive){
                    lifeList.remove(lifeList.get(life - 1));
                    life--;
                }
                
                if(life < 1){
                    alive = false;
                }
            }
        }
    }
}
