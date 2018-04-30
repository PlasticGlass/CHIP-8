package ca.plasticglass.chip8;

/**
 * Created by Zubair Waheed on 3/13/2018.
 */
public class CHIP8 {
    private static short opcode; //Java short - 2 bytes, same size as opcode
    private static CPU cpu;
    private static Memory memory;
    private static Screen screen;
    private static String filename = "ROM";
    private static boolean redraw;


    public static void main(String[] args) {
        memory = new Memory();
        screen = new Screen();
        cpu = new CPU(memory, screen);

        redraw = false;

        memory.loadFile(filename);

        while(true){
            cpu.cycle();


            if(cpu.redrawRequired()){
                screen.redraw();
            }

            cpu.updateKeysPressed();


        }
    }
}
