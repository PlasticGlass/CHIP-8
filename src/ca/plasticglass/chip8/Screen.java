package ca.plasticglass.chip8;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Zubair Waheed on 3/14/2018.
 */
public class Screen extends JPanel {
    private BufferedImage b;
    private final int scale = 10;
    private int white;
    private int black;
    private int[][] pixels;

    public Screen(){
        b = new BufferedImage(640, 320, BufferedImage.TYPE_INT_ARGB);
        black = 0x000000;//new Color(0,0,0).getRGB();
        white = 0xFFFFFF;//new Color(255,255,255).getRGB();
        pixels = new int[32][64];
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(b, null, null);
    }

    public void setPixel(int x, int y) {
        pixels[y][x] ^= 1;
    }

    public int getPixel(int x, int y){
        return pixels[y][x];
    }

    public void redrawPixel(int x, int y, int c) {
        //Color c = (colour == 0) ? Color.white : Color.black;
        c = (c == 0) ? black : white;
        for(int i = x*scale;i<(x*scale)+scale;i++) {
            for (int j = y * scale; j < (y * scale) + scale; j++) {
                b.setRGB(i, j, c);
            }
        }
    }

    public void redraw(){
        for(int i = 0;i<32;i++){
            for(int j = 0;j<64;j++){
                redrawPixel(j,i,pixels[i][j]);
            }
        }
        repaint();
    }

    public void clear(){
        for(int i = 0;i<32;i++){
            for(int j = 0;j<64;j++){
                redrawPixel(j,i,0);
            }
        }
        repaint();
    }
}
