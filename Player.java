
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
    int maxXVelocity = 10;
    int maxYVelocity = 10;
    
    boolean movingLeft = false;
    boolean movingRight = false;
    boolean movingUp = false;
    boolean movingDown = false;
    
    int life = 3;
    boolean alive = true;
    boolean immune = false;
    boolean multishot = false;
    int counter;
    BufferedImage shield;
    
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
        Graphics2D g2 = (Graphics2D) g;
        
        try{
            img = ImageIO.read(new File("resources/Spaceship.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
        
        if(playerNum == 2){
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
        
        if(immune){
            shield = ImageIO.read(new File("resources/Overshield.png"));
            float opacity = 0.5f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            
            g.drawImage(shield, xPos - 5, yPos - 5, 60, 60, null);
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
        
        if(immune){
            counter++;
            
            if(counter == 400){
                immune = false;
                counter = 0;
            }
        }
        
        if(multishot){
            counter++;
            
            if(counter == 300){
                multishot = false;
                counter = 0;
            }
        }
        
        bulletCollision();
        powerUpCollision();
    }
    
    public void fire(){
        if(multishot){
            Bullet bullet1, bullet2, bullet3;
            
            if(playerNum == 1){
                bullet1 = new Bullet(xPos + (width/2 - 3), yPos - 14, 7, 14, "Up");
                bullet2 = new Bullet(xPos, yPos - 14, 7, 14, "Up");
                bullet3 = new Bullet((xPos + width) - 7, yPos - 14, 7, 14, "Up");
                
                GameCanvas.spaceInvaders.bulletList1.add(bullet1);
                GameCanvas.spaceInvaders.bulletList1.add(bullet2);
                GameCanvas.spaceInvaders.bulletList1.add(bullet3);
            }else{
                bullet1 = new Bullet(xPos + (width/2 - 3), yPos - 14, 7, 14, "Down");
                bullet2 = new Bullet(xPos, yPos - 14, 7, 14, "Down");
                bullet3 = new Bullet((xPos + width) - 7, yPos - 14, 7, 14, "Down");
                
                GameCanvas.spaceInvaders.bulletList2.add(bullet1);
                GameCanvas.spaceInvaders.bulletList2.add(bullet2);
                GameCanvas.spaceInvaders.bulletList2.add(bullet3);
            }
        } else{
            Bullet bullet;
            if(playerNum == 1){
                bullet = new Bullet(xPos + (width/2 - 3), yPos - 14, 7, 14, "Up");
                GameCanvas.spaceInvaders.bulletList1.add(bullet);
            }else{
                bullet = new Bullet(xPos + (width/2 - 3), yPos + height + 14, 7, 14, "Down");
                GameCanvas.spaceInvaders.bulletList2.add(bullet);
            }
        }
    }
    
    public void bulletCollision(){
        if(playerNum == 1){
            if(!GameCanvas.spaceInvaders.player1.immune){
                for(int i = 0; i <= GameCanvas.spaceInvaders.bulletList2.size() - 1; i++){
                    Rectangle bullet = GameCanvas.spaceInvaders.bulletList2.get(i).getBounds();

                    if(bullet.intersects(getBounds())){
                        //System.out.println("Hit");
                        GameCanvas.spaceInvaders.bulletList2.get(i).playerHit = true;

                        if(alive){
                            lifeList.remove(lifeList.get(life - 1));
                            life--;
                        }

                        if(life < 1){
                            alive = false;
                            GameCanvas.spaceInvaders.gameOver = true;
                        }
                    }
                }
            }
        } else{
            if(!GameCanvas.spaceInvaders.player2.immune){
                for(int i = 0; i <= GameCanvas.spaceInvaders.bulletList1.size() - 1; i++){
                    Rectangle bullet = GameCanvas.spaceInvaders.bulletList1.get(i).getBounds();

                    if(bullet.intersects(getBounds())){
                        //System.out.println("Hit");
                        GameCanvas.spaceInvaders.bulletList1.get(i).playerHit = true;

                        if(alive){
                            lifeList.remove(lifeList.get(life - 1));
                            life--;
                        }

                        if(life < 1){
                            alive = false;
                            GameCanvas.spaceInvaders.gameOver = true;
                        }
                    }
                }
            }
        }
    }
    
    public void powerUpCollision(){
        for(int i = 0; i < GameCanvas.spaceInvaders.powerUpList.size(); i++){
            Rectangle powerup = GameCanvas.spaceInvaders.powerUpList.get(i).getBounds();
            String powerUp = GameCanvas.spaceInvaders.powerUpList.get(i).powerUpName;
        
            if(powerup.intersects(getBounds())){
                GameCanvas.spaceInvaders.powerUpList.get(i).active = false;
                
                if(powerUp.equals("shield")){
                    if(!immune){
                        immune = true;
                    }
                } else{
                    multishot = true;
                }
            }
        }
        
    }
}
