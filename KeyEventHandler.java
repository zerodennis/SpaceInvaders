
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fielfaustino
 */
public class KeyEventHandler implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {
        //No need
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int Key = e.getKeyCode();

        switch (Key) {
            case KeyEvent.VK_A:
                GameCanvas.spaceInvaders.player1.movingLeft = true;
                GameCanvas.spaceInvaders.player1.movingRight = false;
                break;
            case KeyEvent.VK_D:
                GameCanvas.spaceInvaders.player1.movingLeft = false;
                GameCanvas.spaceInvaders.player1.movingRight = true;
                break;
            case KeyEvent.VK_W:
                GameCanvas.spaceInvaders.player1.movingUp = true;
                GameCanvas.spaceInvaders.player1.movingDown = false;
                break;
            case KeyEvent.VK_S:
                GameCanvas.spaceInvaders.player1.movingDown = true;
                GameCanvas.spaceInvaders.player1.movingUp = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int Key = e.getKeyCode();

        switch (Key) {
            case KeyEvent.VK_A:
                GameCanvas.spaceInvaders.player1.movingLeft = false;
                break;
            case KeyEvent.VK_D:
                GameCanvas.spaceInvaders.player1.movingRight = false;
                break;
            case KeyEvent.VK_W:
                GameCanvas.spaceInvaders.player1.movingUp = false;
                break;
            case KeyEvent.VK_S:
                GameCanvas.spaceInvaders.player1.movingDown = false;
                break;
            default:
                break;
        }
    }
    
}
