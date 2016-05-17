import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Anton Suba on 5/17/2016.
 *
 */
public class GameClient extends Thread{

    Socket socket;
    PrintStream output;
    Scanner sc;

    String ipAddress;
    String data;

    final int port = 8000;

    boolean clientRunning = true;

    public void startClient(){
        ipAddress = JOptionPane.showInputDialog("Input server IP Address: ");

        try {
            socket = new Socket(ipAddress,port);
            output = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));
            sc = new Scanner(new InputStreamReader(new BufferedInputStream(socket.getInputStream())));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startClient();

        while (clientRunning){

            sendData();

            try{
                data = sc.nextLine();
            }catch (Exception e){
                System.out.println("Connection lost");
                clientRunning = false;
                break;
            }

            readData();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void readData(){

        System.out.println(data);

        try {
            String[] dataParts = data.split("\\|");

            int xBorder = GameCanvas.xBorder;
            int yBorder = GameCanvas.yBorder;
            int scale = GameCanvas.scaling;

            int xPos = ((xBorder + (Integer.parseInt(dataParts[0]))) * scale);
            int yPos = ((yBorder + (Integer.parseInt(dataParts[1]))) * scale);
            boolean immune = Boolean.valueOf(dataParts[2]);
            boolean multiShot = Boolean.valueOf(dataParts[3]);
            boolean enemyLoss = Boolean.valueOf(dataParts[4]);
            int enemyLives = Integer.parseInt(dataParts[5]);

            GameCanvas.spaceInvaders.player1.update(xPos, yPos, immune, multiShot, enemyLives);

        }catch (Exception e){
            //Do nothing
        }

    }

    public void sendData() {
        String dataToBeSent;

        try {
            int xBorder = GameCanvas.xBorder;
            int yBorder = GameCanvas.yBorder;
            int scale = GameCanvas.scaling;

            String xPos = String.valueOf(((GameCanvas.spaceInvaders.player2.xPos) - xBorder) / scale);
            String yPos = String.valueOf(((GameCanvas.spaceInvaders.player2.yPos) - yBorder) / scale);
            String immune = Boolean.toString(GameCanvas.spaceInvaders.player2.immune);
            String multiShot = Boolean.toString(GameCanvas.spaceInvaders.player2.multishot);
            String playerLoss = Boolean.toString(GameCanvas.spaceInvaders.gameOver);
            String numLives = String.valueOf(GameCanvas.spaceInvaders.player2.life);

            dataToBeSent = xPos + "|" + yPos + "|" + immune + "|" + multiShot + "|" + playerLoss + "|" + numLives;

            output.println(dataToBeSent);
            output.flush();

        }catch(Exception e){
            //Do nothing
        }
    }

}

