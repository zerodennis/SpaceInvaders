
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
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
    
    public ArrayList<Bullet> bulletList = new ArrayList<>();
    boolean gameOver = false;
    
    public GameCanvas(){
        setBackground(Color.GRAY);
        setSize(1000, 1000);
        
        addKeyListener(new KeyEventHandler());
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
            
            if(test%5000 == 0){
                player1.fire();
                player2.fire();
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
        
        for(int i = 0; i <= bulletList.size() - 1; i++){
            try {
                bulletList.get(i).draw(g);
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
            for(int i = 0; i <= bulletList.size() - 1; i++){
                bulletList.get(i).animate();
            }
        }
    }
}
