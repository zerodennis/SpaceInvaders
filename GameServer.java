import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Anton Suba on 5/17/2016.
 *
 */

public class GameServer extends Thread{
    ServerSocket serverSocket;
    Socket socket;
    final int port = 8000;

    Scanner sc;
    PrintStream output;

    boolean serverRunning = true;
    boolean clientConnected = false;

    public void startServer() {
        System.out.println("Server started");

        try {
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            System.out.println("Client has connected");
            sc = new Scanner(new InputStreamReader(new BufferedInputStream(socket.getInputStream())));
            output = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        startServer();

        while (serverRunning) {
            try {
                readData();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void readData() throws IOException{
        String data;
        while(sc.hasNext()){
            data = sc.nextLine();

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

                GameCanvas.spaceInvaders.player2.update(xPos, yPos, immune, multiShot, enemyLives);

            }catch(Exception e){
                //Do nothing
            }

            sendData();
        }
    }

    public void sendData(){
        String dataToBeSent;

        try {
            int xBorder = GameCanvas.xBorder;
            int yBorder = GameCanvas.yBorder;
            int scale = GameCanvas.scaling;

            String xPos = String.valueOf(((GameCanvas.spaceInvaders.player1.xPos) - xBorder) / scale);
            String yPos = String.valueOf(((GameCanvas.spaceInvaders.player1.yPos) - yBorder) / scale);
            String immune = Boolean.toString(GameCanvas.spaceInvaders.player1.immune);
            String multiShot = Boolean.toString(GameCanvas.spaceInvaders.player1.multishot);
            String playerLoss = Boolean.toString(GameCanvas.spaceInvaders.gameOver);
            String numLives = String.valueOf(GameCanvas.spaceInvaders.player1.life);

            dataToBeSent = xPos + "|" + yPos + "|" + immune + "|" + multiShot + "|" + playerLoss + "|" + numLives;

            output.println(dataToBeSent);
            output.flush();

        }catch(Exception e){
            //Do nothing
        }
    }
}
