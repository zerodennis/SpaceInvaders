
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author fielfaustino
 */
public class GameCanvas extends Canvas implements Runnable{

    Player player2 = new Player(0, 0, 50, 50, 2);
    Player player1 = new Player(750, 730, 50, 50, 1);    
    static boolean running = true;
    private Thread gameThread;
    
    static GameCanvas spaceInvaders;
    
    public ArrayList<Bullet> bulletList1 = new ArrayList<>();
    public ArrayList<Bullet> bulletList2 = new ArrayList<>();
    public ArrayList<Star> starList = new ArrayList<>();
    public ArrayList<Powerup> powerUpList = new ArrayList<>();
    boolean gameOver = false;
    
    Random randomPowerUp = new Random();
    int randSpawn = randomPowerUp.nextInt(200000 - 100000 + 1) + 100000;
    int randType = 1;
    
    public GameCanvas(){
        setBackground(Color.BLACK);
        setSize(1000, 1000);
        
        addKeyListener(new KeyEventHandler());
        
        for(int i = 0; i < 15; i++){
            int xMin = 0, xMax = 780;
            int yMin = 0, yMax = 780;
            
            Random random = new Random();
            int randX = random.nextInt(xMax - xMin + 1) + xMin;
            int randY = random.nextInt(yMax - yMin + 1) + yMin;
            
            Star star = new Star(randX, randY);
            starList.add(star);
        }
    }
    
    private synchronized void startGame(){
        //if(running){
          //  return;
        //}
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
        
    }

    private synchronized void stopGame(){
        if(!running){
            return;
        }
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        long previousTime = System.nanoTime();
        final double limitFPS = 60.0;
        double divider = 1000000000 / limitFPS;
        double timePassed = 0;
        int frames = 0;
        int updates = 0;
        int test =0;
        long timer = System.currentTimeMillis();
        
        while(running){
            long currentTime = System.nanoTime();
            timePassed += (currentTime - previousTime) / divider;
            previousTime = currentTime;
            test++;
            
            if(test%10000 == 0){
                player1.fire();
                player2.fire();
            }
            
            if(test%randSpawn == 0){
                if(powerUpList.size() < 1){
                    randType = randomPowerUp.nextInt(2 - 1 + 1) + 1;
                    Powerup powerup;
                    
                    int xMin = 0, xMax = 700;
                    int yMin = 0, yMax = 700;
            
                    int randX = randomPowerUp.nextInt(xMax - xMin + 1) + xMin;
                    int randY = randomPowerUp.nextInt(yMax - yMin + 1) + yMin;
                            
                    if(randType == 1){
                        powerup = new Powerup(randX, randY, "multishot");
                    } else{
                        powerup = new Powerup(randX, randY, "shield");
                    }
                    powerUpList.add(powerup);
                }
                randSpawn = randomPowerUp.nextInt(200000 - 100000 + 1) + 100000;
            }
            
            if(timePassed >= 1){
                animate();
                timePassed--;
                updates++;
            }
            frames++;
            repaint();
            revalidate();
            
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                //System.out.println(updates + "FPS, " + frames);
                updates = 0;
                frames = 0;
            }
        }
        stopGame();
    }
    
    public static void main(String args[]){
        JFrame frame = new JFrame();
        spaceInvaders = new GameCanvas();
        
        frame.setSize(800, 800);
        frame.setVisible(true);
        frame.add(spaceInvaders);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        spaceInvaders.startGame();
    }

    public void update(Graphics g) {
        Graphics offgc;
        Image offscreen;
        Dimension d = size();


        offscreen = createImage(d.width, d.height);
        offgc = offscreen.getGraphics();

        offgc.setColor(getBackground());
        offgc.fillRect(0, 0, d.width, d.height);
        offgc.setColor(getForeground());

        paint(offgc);

        g.drawImage(offscreen, 0, 0, this);
    }

    @Override
    public void paint(Graphics g){
        for(int i = 0; i < starList.size(); i++){
            try {
                starList.get(i).draw(g);
            } catch (IOException ex) {
                Logger.getLogger(GameCanvas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        for(int i = 0; i < powerUpList.size(); i++){
            try {
                powerUpList.get(i).draw(g);
            } catch (IOException ex) {
                Logger.getLogger(GameCanvas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        for(int i = 0; i <= bulletList1.size() - 1; i++){
            try {
                bulletList1.get(i).draw(g);
            } catch (IOException ex) {
                Logger.getLogger(GameCanvas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for(int i = 0; i <= bulletList2.size() - 1; i++){
            try {
                bulletList2.get(i).draw(g);
            } catch (IOException ex) {
                Logger.getLogger(GameCanvas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
            player1.draw(g);
            player2.draw(g);
        } catch (IOException ex) {
            Logger.getLogger(GameCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(gameOver){
            g.setColor(Color.WHITE);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 70)); 
            if(player1.alive){
                g.drawString("Player 1 Wins!", 200, 400);
            }else{
                g.drawString("Player 2 Wins!", 200, 400);
            }
        }
    }
    
    public void animate(){
        if(!gameOver){
            player1.animate();
            player2.animate();
            for(int i = 0; i <= bulletList1.size() - 1; i++){
                bulletList1.get(i).animate();
            }
            for(int i = 0; i <= bulletList2.size() - 1; i++){
                bulletList2.get(i).animate();
            }
            for(int i = 0; i < starList.size(); i++){
                //starList.get(i).animate();
            }
            for(int i = 0; i < powerUpList.size(); i++){
                powerUpList.get(i).animate();
                
            }
            
            //System.out.println(powerUpList.size());
        }
    }
}
