package ca.plasticglass.chip8;

/**
 * Created by Zubair Waheed on 3/13/2018.
 *
 */
public class Memory {

    private static byte[] memory;
    private final byte PROGRAM_LOAD_POINT = (byte) 0x200;
    private final short MEMORY_SIZE = 0xFFF + 1; //+1 because of zero index

    public Memory(){
        memory = new byte[MEMORY_SIZE];
    }

    public void loadFile(String fileName){

    }

    public byte[] getMemory(){
        return memory;
    }

    public byte get(int address){
        return memory[address];
    }

    public byte getLoadPoint(){
        return PROGRAM_LOAD_POINT;
    }




}
