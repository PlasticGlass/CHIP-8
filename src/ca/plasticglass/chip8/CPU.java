package ca.plasticglass.chip8;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Zubair Waheed on 3/13/2018.
 */
public class CPU {
    private int[] R; //General purpose registers -- R15 is flag -- should not be used by any instruction
    private int I; //Used to store memory addresses
    private int pc;
    private int opcode;
    private int lastOpcode;
    private int delay;
    private int sound;
    private int[] stack;
    private int stackPointer;
    private Screen screen;
    private short[] memory;
    private int[] keys;
    private boolean redrawRequired;
    private Random rand;
    private Keyboard keyboard;
    private Timer delayTimer;
    private Timer soundTimer;

    public CPU(Memory memory, Screen screen, Keyboard keyboard) {
        this.screen = screen;
        this.memory = memory.getMemory();
        this.keyboard = keyboard;

        initComponents();
        initTimers();

        pc = memory.getLoadPoint(); //0x200
    }

    private void initComponents() {
        R = new int[16];
        stack = new int[16];
        keys = new int[16];
        rand = new Random();

        I = 0;
        stackPointer = 0;
        opcode = 0;
        lastOpcode = opcode;
    }

    private void initTimers() {
        delay = 0;
        delayTimer = new Timer();
        delayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                delay--;
            }
        },17, 17); //Run 60 times/second (17ms*60) = 1020ms = 1s

        sound = 0;
        soundTimer = new Timer();
        soundTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(sound > 0) {
                    sound--;
                }
            }
        },17, 17);
    }

    /**
     * Fetches current instruction
     * Each instruction is 2 bytes in size but memory is byte addressable so bytes need to be merged
     * Stored in Big-Endian so top first
     */
    private void fetch() {
        lastOpcode = opcode;
        short top = memory[pc];
        short bottom = memory[pc+1];
        opcode = ((top << 8) | bottom);
        //System.out.println("DEBUG: Fetched opcode: " + String.format("%02X ", opcode));
    }

    private void printRegisterStatus () {
        for(int i = 0;i<16;i++){
            System.out.println("R["+i+"]: " + R[i]);
        }
    }

    /**
     * Decodes and executes current instruction
     * opcode details from :
     * https://en.wikipedia.org/wiki/CHIP-8
     * http://devernay.free.fr/hacks/chip8/C8TECH10.HTM
     */
    private void execute() {
        //printRegisterStatus();
        //System.out.println("About to execute: " +String.format("%02X ", opcode));
        //Filter based on first hex digit
        switch (opcode & 0xF000) {
            case 0x0000:
                switch (opcode) {
                    case 0x00E0: //CLS
                        screen.clear();
                        redrawRequired = true;
                        break;
                    case 0x00EE: //Return from subroutine
                        stackPointer--;
                        pc = stack[stackPointer];
                        break;
                }
                pc += 2;
                break;
            case 0x1000: //Jump to NNN ie Set PC to NNN
                pc = (opcode & 0x0FFF);
                break;
            case 0x2000: //Call subroutine at NNN
                stack[stackPointer] = pc;
                stackPointer++;

                pc = (opcode & 0x0FFF);
                break;
            case 0x3000: //(3xkk)Skip next instruction if Rx = kk
                int val3 = opcode & 0x00FF;
                int index3 = ((opcode & 0x0F00) >> 8);
                if (R[index3] == val3)
                    pc += 4;
                else
                    pc += 2;
                break;
            case 0x4000: //(4xkk)Skip next instruction if Rx != kk
                int val4 = (opcode & 0x00FF);
                int index4 = ((opcode & 0x0F00) >> 8);
                if (R[index4] != val4)
                    pc += 4;
                else
                    pc += 2;
                break;
            case 0x5000: //(5xy0)Skip next instruction if Rx = Ry
                if (R[(opcode & 0x0F00) >> 8] == R[(opcode & 0x00F0) >> 4])
                    pc += 4;
                else
                    pc += 2;
                break;
            case 0x6000: //(6xkk)Set Rx = kk
                R[(opcode & 0x0F00) >> 8] = (opcode & 0x00FF);
                pc += 2;
                break;
            case 0x7000://(7xkk)Set Rx = Rx + kk
                R[(opcode & 0x0F00) >> 8] += (opcode & 0x00FF);
                if(R[(opcode & 0x0F00) >> 8] >= 256){
                    R[(opcode & 0x0F00) >> 8] -= 256; //Prevent overflow/out of bounds array index
                }
                pc += 2;
                break;
            case 0x8000:
                //Filter based on last hex digit
                switch (opcode & 0x000F) {
                    case 0x0000: //Store
                        R[(opcode & 0x0F00) >> 8] = R[(opcode & 0x00F0) >> 4];
                        break;
                    case 0x0001: //Or
                        R[(opcode & 0x0F00) >> 8] |= R[(opcode & 0x00F0) >> 4];
                        break;
                    case 0x0002: //And
                        R[(opcode & 0x0F00) >> 8] &= R[(opcode & 0x00F0) >> 4];
                        break;
                    case 0x0003: //Xor
                        R[(opcode & 0x0F00) >> 8] ^= R[(opcode & 0x00F0) >> 4];
                        break;
                    case 0x0004: //add register at second digit to register at third digit
                        int i14 = ((opcode & 0x0F00) >> 8); //Don't want last 2 digits
                        int i24 = ((opcode & 0x00F0) >> 4); //Don't want last digit (4 bits)

                        //All registers are 8 bits -- value greater than 255 requires carry
                        R[0xF] = (R[i24] > (0xFF - R[i14])) ? 1 : 0; //R15 is flag

                        R[i14] += R[i24];
                        break;
                    case 0x0005: //Sub
                        int i15 = ((opcode & 0x0F00) >> 8);
                        int i25 = ((opcode & 0x00F0) >> 4);

                        R[0xF] = (R[i15] >= R[i25]) ? 1 : 0; //Flag set to NOT borrow

                        R[i15] -= R[i25];
                        break;
                    case 0x0006: //(8xy6)SHR?
                        R[0xF] = (R[(opcode & 0x0F00) >> 8] & 0x1); //Flag set to carry
                        R[(opcode & 0x0F00) >> 8] >>= 1; //Rx divided by 2
                        break;
                    case 0x0007:
                        int i17 = ((opcode & 0x0F00) >> 8);
                        int i27 = ((opcode & 0x00F0) >> 4);

                        R[0x000F] = (R[i27] >= R[i17]) ? 1 : 0; //Flag set to NOT borrow

                        R[i17] = (R[i27] - R[i17]);
                        break;
                    case 0x000E:
                        R[0xF] = (R[(opcode & 0x0F00) >> 8] >> 7);
                        R[(opcode & 0x0F00) >> 8] <<= 1;
                        break;
                    default:
                }
                pc += 2;
                break;
            case 0x9000:
                if (R[(opcode & 0x0F00) >> 8] != R[(opcode & 0x00F0) >> 4])
                    pc += 4;
                else
                    pc += 2;
                break;
            case 0xA000:
                I = (opcode & 0x0FFF);
                pc += 2;
                break;
            case 0xB000:
                pc = ((opcode & 0x0FFF) + R[0]);
                break;
            case 0xC000:
                R[(opcode & 0x0F00) >> 8] = rand.nextInt(255) & (opcode & 0x00FF);
                pc += 2;
                break;
            case 0xD000:
                /*
                Dxyn - DRW Vx, Vy, nibble
                Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.

                The interpreter reads n bytes from memory, starting at the address stored in I. These bytes
                are then displayed as sprites on screen at coordinates (Vx, Vy). Sprites are XORed onto the
                existing screen. If this causes any pixels to be erased, VF is set to 1, otherwise it is set
                to 0. If the sprite is positioned so part of it is outside the coordinates of the display, it
                 wraps around to the opposite side of the screen. See instruction 8xy3 for more information on
                 XOR, and section 2.4, Display, for more information on the Chip-8 screen and sprites.

                Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels.
                Each row of 8 pixels is read as bit-coded starting from memory location I; I value doesn’t change
                after the execution of this instruction. As described above, VF is set to 1 if any screen pixels are
                flipped from set to unset when the sprite is drawn, and to 0 if that doesn’t happen
                 */
                int x = R[((opcode & 0x0F00) >> 8)]; //Don't want last 2 digits
                int y = R[((opcode & 0x00F0) >> 4)]; //Don't want last digit (4 bits)
                int bytes = (opcode & 0x000F);
                int spritePiece;
                int mask = 0x80; //0x80 = 0b1000 0000

                R[0xF] = 0;

                for(int i = 0;i<bytes;i++){
                    spritePiece = memory[I+i];
                    //Draw the horizontal sprite piece
                    //Horizontal pieces have fixed width of 1 byte
                    for(int xc = 0;xc<8;xc++) {
                        if ((spritePiece & (mask >> xc)) != 0){
                            if(screen.getPixel(x + xc, y + i) == 1){
                                R[0xF] = 1; //Collision flag
                            }
                            screen.setPixel(x + xc, y + i);
                        }
                    }
                }
                redrawRequired = true;
                pc += 2;
                break;
            case 0xE000:
                switch (opcode & 0x00FF) {
                    case 0x009E: //Skip next instruction if key with the value of Vx is pressed.
                        if(keyboard.keyCurrentlyPressed((R[(opcode & 0x0F00) >> 8]))) {
                            pc += 4;
                        } else {
                            pc += 2;
                        }
                        break;
                    case 0x00A1: //Skip next instruction if key with the value of Vx is not pressed.
                        if(!keyboard.keyCurrentlyPressed((R[(opcode & 0x0F00) >> 8]))){
                            pc += 4;
                        } else {
                            pc += 2;
                        }
                        break;
                    default:
                }
                break;
            case 0xF000:
                switch (opcode & 0x00FF) {
                    case 0x0007:
                        R[(opcode & 0x0F00) >> 8] = delay;
                        pc += 2;
                        break;
                    case 0x000A:
                        R[(opcode & 0x0F00) >> 8] = (int) keyboard.waitForKeypress();
                        pc += 2;
                        break;
                    case 0x0015:
                        delay = R[(opcode & 0x0F00) >> 8];
                        pc += 2;
                        break;
                    case 0x0018:
                        sound = R[(opcode & 0x0F00) >> 8];
                        pc += 2;
                        break;
                    case 0x001E:
                        if(I + R[(opcode & 0x0F00) >> 8] > 0xFFF)
                            R[0xF] = 1;
                        else
                            R[0xF] = 0;
                        I += R[(opcode & 0x0F00) >> 8];
                        pc += 2;
                        break;
                    case 0x0029:
                        I = R[(opcode & 0x0F00) >> 8] * 0x5; //Sprites are 5 bytes long
                        pc += 2;
                        break;
                    case 0x0033:
                        //Copied from
                        //http://www.multigesture.net/wp-content/uploads/mirror/goldroad/chip8.shtml
                        memory[I] = (short)(R[((opcode & 0x0F00) >> 8)] / 100);
                        memory[I + 1] = (short)((R[((opcode & 0x0F00) >> 8)] / 10) % 10);
                        memory[I + 2] = (short)((R[((opcode & 0x0F00) >> 8)] % 100) % 10);
                        pc += 2;
                        break;
                    case 0x0055:
                        for(int i = 0;i<((opcode & 0x0F00) >> 8);i++){
                            memory[I+i] = (short) R[i];
                        }
                        pc += 2;
                        break;
                    case 0x0065:
                        for(int i = 0;i<((opcode & 0x0F00) >> 8);i++){
                            R[i] = memory[I+i];
                        }
                        pc += 2;
                        break;
                    default: //Invalid

                }
                break;
        }
    }

    public void cycle() {
        fetch();
        execute();
    }

    public int getCurrentOpcode() {
        return I;
    }

    public boolean redrawRequired() {
        return redrawRequired;
    }

    public void redrawComplete() {
        redrawRequired = false;
    }
}
