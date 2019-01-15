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

            KeyboardFocusManager
                    .getCurrentKeyboardFocusManager()
                    .addKeyEventDispatcher(new KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
                    System.out.println(e.getKeyChar() + " Key Pressed!!!");

                    return false;
                }
            });
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

            CHIP8 panel = new CHIP8(width, height);

            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            /*for(int i = 0;i<10;i++) {

                int width1 = 100;
                panel.drawRect(Color.RED, 0 + (i*width1), 0, width1, 50);
                try {
                    Thread.sleep(1000);
                } catch(Exception ex){}*/
            }
        }


