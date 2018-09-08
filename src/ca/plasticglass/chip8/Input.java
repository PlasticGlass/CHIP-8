package ca.plasticglass.chip8;

/**
 * Created by Zubair Waheed on 3/14/2018.
 */
public class Input {
    public int[] keys;

    public Input(){
        keys = new int[16];
    }

    public int getKey(int key){
        return keys[key];
    }

    public void setKey(int key, int value) {
        keys[key] = value;
    }
}
