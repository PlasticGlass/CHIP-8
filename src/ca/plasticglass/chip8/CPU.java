package ca.plasticglass.chip8;

import java.util.Random;

/**
 * Created by Zubair Waheed on 3/13/2018.
 */
public class CPU {
    private int[] R; //General purpose registers -- R15 is flag -- should not be used by any instruction
    private int I; //Used to store memory addresses
    private int pc;
    private int opcode;
    private int[] pixels;
    private int delayTimer;
    private int soundTimer;
    private int[] stack;
    private int stackPointer;
    private int[] keypad;
    private Screen screen;
    private int[] memory;
    private int[] keys;
    private boolean redrawStatus;
    private Random rand;
    private Keyboard keyboard;

    public CPU(Memory memory, Screen screen, Keyboard input) {
        this.screen = screen;
        this.memory = memory.getMemory();
        this.keyboard = keyboard;

        R = new int[16];
        pixels = new int[2048];
        stack = new int[16];
        keys = new int[16];
        rand = new Random();

        I = 0;
        stackPointer = 0;
        opcode = 0;
        pc = memory.getLoadPoint(); //0x200
    }

    /**
     * Fetches current instruction
     * Each instruction is 2 bytes in size but memory is byte addressable so bytes need to be merged
     * Stored in Big-Endian so top first
     */
    private void fetch() {
        int top = memory[pc];
        int bottom = memory[pc+1];
        opcode = ((top << 8) | bottom);
    }

    /**
     * Decodes and executes current instruction
     * opcode details from :
     * https://en.wikipedia.org/wiki/CHIP-8
     * http://devernay.free.fr/hacks/chip8/C8TECH10.HTM
     */
    private void execute() {
        //Filter based on first hex digit
        switch (opcode & 0xF000) {
            case 0x0000:
                switch (opcode) {
                    case 0x00E0: //CLS
                        screen.clear();
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
                stackPointer++;
                stack[stackPointer] = pc;
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
                R[(opcode & 0x0F00) >> 8] += (opcode & 0x0FF);
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

                        R[i14] += R[i24];

                        //All registers are 8 bits -- value greater than 255 requires carry
                        R[0x000F] = (R[i14] > 0x0FF) ? 1 : 0; //R15 is flag
                        break;
                    case 0x0005: //Sub
                        int i15 = ((opcode & 0x0F00) >> 8);
                        int i25 = ((opcode & 0x00F0) >> 4);

                        R[0x000F] = (R[i15] > R[i25]) ? 1 : 0; //Flag set to NOT borrow

                        R[i15] -= R[i25];
                        break;
                    case 0x0006: //(8xy6)SHR?
                        R[0x000F] = ((R[(opcode & 0x0F00) >> 8] & 0x0001) == 1) ? 1 : 0; //Flag set to carry
                        R[(opcode & 0x0F00) >> 8] >>= 1; //Rx divided by 2
                        break;
                    case 0x0007:
                        int i17 = ((opcode & 0x0F00) >> 8);
                        int i27 = ((opcode & 0x00F0) >> 4);

                        R[0x000F] = (R[i27] > R[i17]) ? 1 : 0; //Flag set to NOT borrow

                        R[i17] = (R[i27] - R[i17]);
                        break;
                    case 0x000E:
                        R[0x000F] = ((R[(opcode & 0x0F00) >> 8] >> 7) == 1) ? 1 : 0;
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
                 */
                int x = R[((opcode & 0x0F00) >> 8)]; //Don't want last 2 digits
                int y = R[((opcode & 0x00F0) >> 4)]; //Don't want last digit (4 bits)
                int bytes = (opcode & 0x000F);
                int sprite;

                R[0xF] = 0;

                for(int i = 0;i<bytes;i++){
                    sprite = memory[I+i];


                }
                screen.setPixel(x,y);

                break;
            case 0xE000:
                switch (opcode & 0x00FF) {
                    case 0x009E: //Skip next instruction if key with the value of Vx is pressed.
                        if(keyboard.keyCurrentlyPressed((char) (R[(opcode & 0x0F00) >> 8]))) {
                            pc += 4;
                        }
                        break;
                    case 0x00A1: //Skip next instruction if key with the value of Vx is not pressed.
                        if(!keyboard.keyCurrentlyPressed((char) (R[(opcode & 0x0F00) >> 8]))){
                            pc += 4;
                        }
                        break;
                    default:
                }
                break;
            case 0xF000:
                switch (opcode & 0x00FF) {
                    case 0x0007:
                        R[(opcode & 0x0F00) >> 8] = delayTimer;
                        pc += 2;
                        break;
                    case 0x000A:
                        R[(opcode & 0x0F00) >> 8] = waitForKeypress();
                        pc += 2;
                        break;
                    case 0x0015:
                        delayTimer = R[(opcode & 0x0F00) >> 8];
                        pc += 2;
                        break;
                    case 0x0018:
                        soundTimer = R[(opcode & 0x0F00) >> 8];
                        pc += 2;
                        break;
                    case 0x001E:
                        I += R[(opcode & 0x0F00) >> 8];
                        pc += 2;
                        break;
                    case 0x0029:
                        I = R[(opcode & 0x0F00) >> 8] * 5; //Sprites are 5 bytes long
                        pc += 2;
                        break;
                    case 0x0033:
                        //Copied from
                        //http://www.multigesture.net/wp-content/uploads/mirror/goldroad/chip8.shtml
                        memory[I] = (R[((opcode & 0x0F00) >> 8)] / 100);
                        memory[I + 1] = ((R[((opcode & 0x0F00) >> 8)] / 10) % 10);
                        memory[I + 2] = ((R[((opcode & 0x0F00) >> 8)] % 100) % 10);
                        pc += 2;
                        break;
                    case 0x0055:
                        for(int i = 0;i<((opcode & 0x0F00) >> 8);i++){
                            memory[I+i] = R[i];
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

    public void updateKeysPressed() {

    }

    private int waitForKeypress() {
        return 0;
    }

    public int getCurrentOpcode() {
        return I;
    }

    public boolean redrawRequired() {
        return true;
    }
}
