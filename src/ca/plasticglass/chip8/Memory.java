package ca.plasticglass.chip8;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Zubair Waheed on 3/13/2018.
 *
 */
public class Memory {

    private short[] memory;
    private final int PROGRAM_LOAD_POINT = 0x200;
    private final int MEMORY_SIZE = 0xFFF; //+1 because of zero index
    private int[] fontset =
            {
                    0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
                    0x20, 0x60, 0x20, 0x20, 0x70, // 1
                    0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
                    0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
                    0x90, 0x90, 0xF0, 0x10, 0x10, // 4
                    0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
                    0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
                    0xF0, 0x10, 0x20, 0x40, 0x40, // 7
                    0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
                    0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
                    0xF0, 0x90, 0xF0, 0x90, 0x90, // A
                    0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
                    0xF0, 0x80, 0x80, 0x80, 0xF0, // C
                    0xE0, 0x90, 0x90, 0x90, 0xE0, // D
                    0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
                    0xF0, 0x80, 0xF0, 0x80, 0x80  // F
            };

    public Memory(){
        memory = new short[MEMORY_SIZE];
        loadFontset();
    }

    public void loadFile(String fileName){
        try {
            byte[] myBytes = Files.readAllBytes(Paths.get(System.getProperty("user.dir") + "\\" + fileName));
            System.out.println(myBytes.length);
            for(int i =0;i<myBytes.length;i++){
                memory[PROGRAM_LOAD_POINT + i] = (short) (myBytes[i] & 0xFF); //Make byte unsigned
            }
        }catch (IOException ex) {
            ex.printStackTrace();
        }

        //printMemoryState();
    }

    private void loadFontset() {
        for(int i = 0; i< fontset.length;i++){
            memory[i] = (byte) fontset[i];
        }
    }

    public short[] getMemory(){
        return memory;
    }


    public int getLoadPoint(){
        return PROGRAM_LOAD_POINT;
    }

    private void printMemoryState() {
        for(int i = 0; i<MEMORY_SIZE;i++)
            System.out.println(String.format("%02X ", memory[i]));
    }
}
