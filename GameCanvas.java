
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

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
    
    Player player2;
    Player player1;
    static boolean running = true;
    private Thread gameThread;
    
    static GameCanvas spaceInvaders;
    static SerialInput serialInput;
    static GameServer gameServer;
    static GameClient gameClient;
    
    public ArrayList<Bullet> bulletList1 = new ArrayList<>();
    public ArrayList<Bullet> bulletList2 = new ArrayList<>();
    public ArrayList<Star> starList = new ArrayList<>();
    public ArrayList<Powerup> powerUpList = new ArrayList<>();
    boolean gameOver = false;

    static int xResolution;
    static int yResolution;
    static int scaling;
    static int xBorder;
    static int yBorder;
    
    Random randomPowerUp = new Random();
    int randSpawn = randomPowerUp.nextInt(200000 - 100000 + 1) + 100000;
    int randType = 1;
    
    public GameCanvas(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();

        xResolution = (int) screenWidth;
        yResolution = (int) screenHeight;
        setScaling();
        setBorders();

        Object[] startOptions = {"Host", "Join"};
        int option = JOptionPane.showOptionDialog(this, "Start a New Game", null,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null, startOptions,null);
        if(option == 0){
            startServer();
        }else {
            startClient();
        }

        int playerDimensions = 50 * scaling;
        player2 = new Player((xBorder)*scaling, (yBorder)*scaling, playerDimensions, playerDimensions, 2, xBorder, yBorder, scaling);
        player1 = new Player((xBorder + 1280 - playerDimensions)*scaling, (yBorder + (720 - playerDimensions))*scaling, playerDimensions, playerDimensions, 1, xBorder, yBorder, scaling);

        setBackground(Color.BLACK);

        for(int i = 0; i < 15; i++){
            int xMin = xBorder, xMax = (xBorder + 1280)*scaling;
            int yMin = yBorder, yMax = (yBorder + 720)*scaling;
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
                    
                    int xMin = (xBorder)*scaling, xMax = (xBorder + 1250)*scaling;
                    int yMin = (yBorder)*scaling, yMax = (yBorder + 690)*scaling;
            
                    int randX = randomPowerUp.nextInt(xMax - xMin + 1) + xMin;
                    int randY = randomPowerUp.nextInt(yMax - yMin + 1) + yMin;
                            
                    if(randType == 1){
                        powerup = new Powerup(randX, randY, "multishot", scaling);
                    } else{
                        powerup = new Powerup(randX, randY, "shield", scaling);
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
                //System.out.println(updates + "Game FPS, " + frames);
                updates = 0;
                frames = 0;
            }
        }
        stopGame();
    }

    //Full Screen Mode
    private static boolean isMacOSX() {
        return System.getProperty("os.name").contains("Mac OS X");
    }

    public static void enableFullScreenMode(Window window) {
        String className = "com.apple.eawt.FullScreenUtilities";
        String methodName = "setWindowCanFullScreen";

        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, Window.class, boolean.class);
            method.invoke(null, window, true);
        } catch (Throwable t) {
            System.err.println("Full screen mode is not supported");
            t.printStackTrace();
        }
    }
    //Full Screen Mode

    //Set Scaling and Border
    private void setScaling(){
        if(xResolution >= 3840 && yResolution >= 2160){
            scaling = 3;
        }
        else if(xResolution >= 2560 && yResolution >= 1600){
            scaling = 2;
        }
        else{
            scaling = 1;
        }
    }

    private void setBorders(){
        xBorder = (xResolution - (1280*scaling)) / 2;
        yBorder = (yResolution - (720*scaling)) / 2;
    }
    //Set Scaling and Border

    //Networking
    public static void startServer(){
        gameServer = new GameServer();
        gameServer.start();
        //serverRunning = true;
    }

    public static void startClient(){
        gameClient = new GameClient();
        gameClient.start();
        //clientRunning = true;
    }
    //Networking
    
    public static void main(String args[]){
        JFrame frame = new JFrame();
        spaceInvaders = new GameCanvas();
        serialInput = new SerialInput();
        
        try{
            serialInput.initialize();
            serialInput.portConnect();
            serialInput.startSerial();
        } catch(Throwable t){
            System.err.println("Serial input cannot be initialized");
            t.printStackTrace();
        }

        if (isMacOSX()) {
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name",
                    "Full Screen Demo");
            enableFullScreenMode(frame);
        } else{
            frame.setUndecorated(true);
            //gameFrame.setSize(1280,720);
            frame.setResizable(false);
        }

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.setVisible(true);
        frame.add(spaceInvaders);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        spaceInvaders.startGame();
    }

    //Buffer Strategy
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
    //Buffer Strategy

    @Override
    public void paint(Graphics g){
        for (Star aStarList : starList) {
            try {
                aStarList.draw(g);
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
                g.drawString("Player 1 Wins!", (xBorder + 440)*scaling, (yBorder + 320)*scaling);
            }else{
                g.drawString("Player 2 Wins!", (xBorder + 440)*scaling, (yBorder + 320)*scaling);
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
            //for(int i = 0; i < starList.size(); i++){
                //starList.get(i).animate();
            //}
            for(int i = 0; i < powerUpList.size(); i++){
                powerUpList.get(i).animate();

            }
            
            //System.out.println(powerUpList.size());
        }
    }
}
