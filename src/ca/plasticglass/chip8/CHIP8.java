package ca.plasticglass.chip8;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
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
    public class CHIP8 extends JPanel {

    private BufferedImage canvas;

    public CHIP8(int width, int height) {
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        fillCanvas(Color.BLUE);

        drawRect(Color.RED, 20, 20, 10, 10);
    }

    public Dimension getPreferredSize() {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }


    public void fillCanvas(Color c) {
        int color = c.getRGB();
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                canvas.setRGB(x, y, color);
            }
        }
        repaint();
    }


    public void drawLine(Color c, int x1, int y1, int x2, int y2) {
        // Implement line drawing
        repaint();
    }

    public void drawRect(Color c, int x1, int y1, int width, int height) {
        int color = c.getRGB();
        // Implement rectangle drawing
        for (int x = x1; x < x1 + width; x++) {
            for (int y = y1; y < y1 + height; y++) {
                canvas.setRGB(x, y, color);
            }
        }
        repaint();
    }

    public void drawOval(Color c, int x1, int y1, int width, int height) {
        // Implement oval drawing
        repaint();
    }


    public static void main(String[] args) {
        int width = 640;
        int height = 320;
        JFrame frame = new JFrame("Direct draw demo");
        Keyboard k = new Keyboard();

        // CHIP8 panel = new CHIP8(width, height);
        Screen panel = new Screen();

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(k);

        for (int i = 0; i < 64; i++) {
            //if(i%2 == 0) {
                panel.redrawPixel(i, 0, 1);
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                }
           // }

        }
    }
}


