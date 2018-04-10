package ca.plasticglass.chip8;

/**
 * Created by Zubair Waheed on 3/13/2018.
 *
 */
public class Memory {

    private static short[] memory;
    private final short PROGRAM_LOAD_POINT = (short) 0x200;
    private final short MEMORY_SIZE = 0xFFF + 1; //+1 because of zero index

    public Memory(){
        memory = new short[MEMORY_SIZE];
    }

    public void loadFile(String fileName){

    }

    public short[] getMemory(){
        return memory;
    }

    public short get(int address){
        return memory[address];
    }

    public short getLoadPoint(){
        return PROGRAM_LOAD_POINT;
    }




}
