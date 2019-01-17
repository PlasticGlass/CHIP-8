package ca.plasticglass.chip8;

import javax.swing.*;

/**
 * Created by Zubair Waheed on 3/13/2018.
 */
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

    public void loadFile(String fileName) {
        this.memory.loadFile(fileName);
    }

    public void cycle() {
        this.cpu.cycle();
    }

    public void redraw() {
        if(this.cpu.redrawRequired()){
            this.screen.redraw();
            this.cpu.redrawComplete();
        }
    }

}


