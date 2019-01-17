package ca.plasticglass.chip8;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zubair Waheed on 3/14/2018.
 */
public class Keyboard extends KeyAdapter {
    private List<Character> keysCurrentlyPressed;
    private final char[] keys = new char[] {
            KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4,
            KeyEvent.VK_Q, KeyEvent.VK_W, KeyEvent.VK_E, KeyEvent.VK_R,
            KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_F,
            KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_C
    };

    public Keyboard(){
        keysCurrentlyPressed = new ArrayList<>();
    }

    public boolean keyCurrentlyPressed(int key){
        return keysCurrentlyPressed.contains(keys[key]);
    }

    public void keyPressed(KeyEvent e){
        char key = e.getKeyChar();

        if(keyValid(key)){
            if(!keysCurrentlyPressed.contains(key)) {
                keysCurrentlyPressed.add(key);
            }
            //System.out.println("DEBUG: " + keysCurrentlyPressed);
        }
    }

    public void keyReleased(KeyEvent e){
        keysCurrentlyPressed.removeIf(key -> key == e.getKeyChar());
        //System.out.println("DEBUG: " + keysCurrentlyPressed);
    }

    private boolean keyValid(char key){
        for(int i = 0;i<15;i++){
            if(keys[i] == key){
                return true;
            }
        }

        return false;
    }

    public int waitForKeypress(){
        while(keysCurrentlyPressed.isEmpty()){
            try {
                Thread.sleep(100);
            } catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
        Character key = keysCurrentlyPressed.get(0);
        for(int i = 0;i<keys.length;i++){
            if(keys[i] == key){
                return i;
            }
        }
        return -1;
    }
}
