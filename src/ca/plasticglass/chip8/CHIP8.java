package ca.plasticglass.chip8;

import javax.swing.*;

/**
 * Created by Zubair Waheed on 3/13/2018.
 */
//public class CHIP8 {
    /*private static short opcode; //Java short - 2 bytes, same size as opcode
    private static CPU cpu;
    private static Memory memory;
    private static Screen screen;
    private static String filename = "ROM";
    private static boolean redraw;


    public static void main(String[] args) {
        JFrame frame = new JFrame("CHIP 8");




        memory = new Memory();
        screen = new Screen();
        cpu = new CPU(memory, screen);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(screen);
        frame.setVisible(true);
        frame.setResizable(false);

        redraw = false;

        memory.loadFile(filename);

        while(true){
            cpu.cycle();


            if(cpu.redrawRequired()){
                screen.redraw();
            }

            cpu.updateKeysPressed();


        }
    }*/
public class CHIP8 {

    private CPU cpu;
    private Memory memory;
    private Screen screen;
    private Keyboard keyboard;
    private JFrame display;

    public CHIP8() {
        initSystem();
        initGUI();
    }

    private void initSystem() {
        this.keyboard = new Keyboard();
        this.memory = new Memory();
        this.screen = new Screen();
        this.cpu = new CPU(memory, screen, keyboard);
    }

    private void initGUI() {
        this.display = new JFrame("CHIP-8");
        this.display.add(screen);
        this.display.pack();
        this.display.setVisible(true);
        this.display.setResizable(false);
        this.display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.display.addKeyListener(keyboard);
    }
}


