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
            KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8,
            KeyEvent.VK_9, KeyEvent.VK_0, KeyEvent.VK_A, KeyEvent.VK_B,
            KeyEvent.VK_C, KeyEvent.VK_D, KeyEvent.VK_E, KeyEvent.VK_F
    };

    public Keyboard(){
        keysCurrentlyPressed = new ArrayList<>();
    }

    public boolean keyCurrentlyPressed(char key){
        return keysCurrentlyPressed.contains(key);
    }

    public void keyPressed(KeyEvent e){
        char key = e.getKeyChar();

        if(keyValid(key)){
            if(!keysCurrentlyPressed.contains(key)) {
                keysCurrentlyPressed.add(key);
            }
            System.out.println("DEBUG: " + keysCurrentlyPressed);
        }
    }

    public void keyReleased(KeyEvent e){
        keysCurrentlyPressed.removeIf(key -> key == e.getKeyChar());
        System.out.println("DEBUG: " + keysCurrentlyPressed);
    }

    private boolean keyValid(char key){
        for(int i = 0;i<16;i++){
            if(keys[i] == key){
                return true;
            }
        }

        return false;
    }

}
