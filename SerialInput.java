/**
 * Created by Anton Suba on 5/17/2016.
 *
 */

import java.io.BufferedReader;                    //BufferedReader makes reading operation efficient
import java.io.InputStreamReader;         //InputStreamReader decodes a stream of bytes into a character set
import java.io.OutputStream;          //writes stream of bytes into serial port
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;            //deals with possible events in serial port (eg: data received)
import gnu.io.SerialPortEventListener; //listens to the a possible event on serial port and notifies when it does
import java.util.Enumeration;
import gnu.io.PortInUseException;           //all the exceptions.Never mind them for now
import gnu.io.UnsupportedCommOperationException;

public class SerialInput implements SerialPortEventListener, Runnable{

    private SerialPort serialPort;         //defining serial port object
    private CommPortIdentifier portId = null;       //my COM port
    private static final int TIME_OUT = 2000;    //time in milliseconds
    private static final int BAUD_RATE = 9600; //baud rate to 9600bps
    private BufferedReader input;               //declaring my input buffer
    private OutputStream output;                //declaring output stream
    private String name;        //user input name string

    private boolean running = true;
    Thread serialThread;

    //method initialize
    public void initialize() {
        CommPortIdentifier ports;      //to browse through each port identified
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers(); //store all available ports
        while (portEnum.hasMoreElements()) {  //browse through available ports
            ports = (CommPortIdentifier) portEnum.nextElement();
            //following line checks whether there is the port i am looking for and whether it is serial
            if (ports.getPortType() == CommPortIdentifier.PORT_SERIAL && ports.getName().equals("COM5")) {

                System.out.println("COM port found:COM5");
                portId = ports;                  //initialize my port
                break;
            }

        }
        //if serial port am looking for is not found
        if (portId == null) {
            System.out.println("COM port not found");
            System.exit(1);
        }

    }

    //end of initialize method

    //connect method

    public void portConnect() {
        //connect to port
        try {
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);   //down cast the comm port to serial port
            //give the name of the application
            //time to wait
            System.out.println("Port open successful: COM5");

            //set serial port parameters
            serialPort.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);


        } catch (PortInUseException e) {
            System.out.println("Port already in use");
            System.exit(1);
        } catch (NullPointerException e2) {
            System.out.println("COM port maybe disconnected");
        } catch (UnsupportedCommOperationException e3) {
            System.out.println(e3.toString());
        }

        //input and output channels
        try {
            //defining reader and output stream
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();
            //adding listeners to input and output streams
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            serialPort.notifyOnOutputEmpty(true);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
    //end of portConnect method

    //readWrite method

    public void serialEvent(SerialPortEvent evt) {

        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) { //if data available on serial port
            try {
                String inputLine = input.readLine();
                //System.out.println(inputLine);
                switch (inputLine) {
                    case "left":
                        GameCanvas.spaceInvaders.player1.movingLeft = true;
                        GameCanvas.spaceInvaders.player1.movingRight = false;
                        GameCanvas.spaceInvaders.player1.movingUp = false;
                        GameCanvas.spaceInvaders.player1.movingDown = false;
                        break;
                    case "right":
                        GameCanvas.spaceInvaders.player1.movingLeft = false;
                        GameCanvas.spaceInvaders.player1.movingRight = true;
                        GameCanvas.spaceInvaders.player1.movingUp = false;
                        GameCanvas.spaceInvaders.player1.movingDown = false;
                        break;
                    case "up":
                        GameCanvas.spaceInvaders.player1.movingLeft = false;
                        GameCanvas.spaceInvaders.player1.movingRight = false;
                        GameCanvas.spaceInvaders.player1.movingUp = true;
                        GameCanvas.spaceInvaders.player1.movingDown = false;
                        break;
                    case "down":
                        GameCanvas.spaceInvaders.player1.movingLeft = false;
                        GameCanvas.spaceInvaders.player1.movingRight = false;
                        GameCanvas.spaceInvaders.player1.movingUp = false;
                        GameCanvas.spaceInvaders.player1.movingDown = true;
                        break;
                }

            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }

    }
    //end of serialEvent method

    //closePort method
    private void close() {
        if (serialPort != null) {
            serialPort.close(); //close serial port
        }
        input = null;        //close input and output streams
        output = null;
    }

    public synchronized void startSerial(){
        running = true;
        serialThread = new Thread();
        serialThread.start();
    }

    public synchronized void stopSerial(){
        if(!running){
            return;
        }
        running = false;
        try {
            serialThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long previousTime = System.nanoTime();
        final double limitFPS = 120.0;
        double divider = 1000000000 / limitFPS;
        double timePassed = 0;
        int frames = 0;
        int updates = 0;
        long timer = System.currentTimeMillis();

        while(running){
            long currentTime = System.nanoTime();
            timePassed += (currentTime - previousTime) / divider;
            previousTime = currentTime;

            if(timePassed >= 1){
                timePassed--;
                updates++;
            }
            frames++;

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                //System.out.println(updates + "Serial FPS, " + frames);
                updates = 0;
                frames = 0;
            }
        }
        stopSerial();
    }
}
