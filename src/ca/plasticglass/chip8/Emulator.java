package ca.plasticglass.chip8;

public class Emulator {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Filename not provided");
            System.exit(-1);
        }

        String fileName = args[0];
        CHIP8 chip8 = new CHIP8();
        chip8.loadFile(fileName);

        while (true) {
            chip8.cycle();
            chip8.redraw();
        }
    }
}
