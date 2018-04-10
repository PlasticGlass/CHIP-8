package ca.plasticglass.chip8;

/**
 * Created by Zubair Waheed on 3/13/2018.
 */
public class CPU {
    private short[] V; //General purpose registers -- V15 is flag -- should not be used by any instruction
    private int I; //Used to store memory addresses
    private int pc;
    private int opcode;
    private short[] pixels;
    private short delayTimer;
    private short soundTimer;
    private int[] stack;
    private int stackPointer;
    private short[] keypad;
    private Screen screen;
    private short[] memory;
    private boolean redrawStatus;

    public CPU(Memory memory, Screen screen){
        V = new short[16];
        pixels = new short[2048];
        stack = new int[16];
        pc = memory.getLoadPoint();
        this.screen = screen;
        this.memory = memory.getMemory();
    }

    /**
     * Fetches current instruction
     * Each instruction is 2 bytes in size but memory is byte addressable so bytes need to be merged
     * Stored in Big-Endian so top first
     */
    private void fetch(){
        short top = memory[pc++];
        short bottom = memory[pc++];
        opcode = (short) ((top << 8) | bottom);
    }

    /**
     * Decodes and executes current instruction
     * opcode details from :
     * https://en.wikipedia.org/wiki/CHIP-8
     * http://devernay.free.fr/hacks/chip8/C8TECH10.HTM
     */
    private void execute(){
        //Filter based on first hex digit
        switch(opcode & 0xF000){
            case 0x0000:
                switch(opcode) {
                    case 0x00E0: //CLS
                        screen.clear();
                        break;
                    case 0x00EE: //Return from subroutine
                        stackPointer--;
                        pc = stack[stackPointer];
                        break;
                }
                pc +=2;
                break;
            case 0x1000: //Jump to NNN
                pc = (opcode & 0x0FFF);
                break;
            case 0x2000: //Call subroutine at NNN
                stackPointer++;
                stack[stackPointer] = pc;
                pc = (opcode & 0x0FFF);
                break;
            case 0x3000:
                int val3 = opcode & 0x00FF;
                short index3 = (short) ((opcode & 0x0F00) >> 8);
                if(V[index3] == val3)
                    pc += 4;
                else
                    pc += 2;
                break;
            case 0x4000:
                int val4 = (opcode & 0x00FF);
                short index4 = (short) ((opcode & 0x0F00) >> 8);
                if(V[index4] != val4)
                    pc += 4;
                else
                    pc += 2;
                break;
            case 0x5000:
                if(V[(opcode & 0x0F00) >> 8] == V[(opcode & 0x00F0) >> 4])
                    pc += 4;
                else
                    pc += 2;
                break;
            case 0x6000:
                V[(opcode & 0x0F00) >> 8] = (short)(opcode & 0x00FF);
                pc += 2;
                break;
            case 0x7000:
                V[(opcode & 0x0F00) >> 8] += (opcode & 0x0FF);
                pc += 2;
                break;
            case 0x8000:
                //Filter based on last hex digit
                switch(opcode & 0x000F) {
                    case 0x0000: //Store
                        V[(opcode & 0x0F00) >> 8] = V[(opcode & 0x00F0) >> 4];
                        break;
                    case 0x0001: //Or
                        V[(opcode & 0x0F00) >> 8] |= V[(opcode & 0x00F0) >> 4];
                        break;
                    case 0x0002: //And
                        V[(opcode & 0x0F00) >> 8] &= V[(opcode & 0x00F0) >> 4];
                        break;
                    case 0x0003: //Xor
                        V[(opcode & 0x0F00) >> 8] ^= V[(opcode & 0x00F0) >> 4];
                        break;
                    case 0x0004: //add register at second digit to register at third digit
                        int i14 = ((opcode & 0x0F00) >> 8); //Don't want last 2 digits
                        int i24 = ((opcode & 0x00F0) >> 4); //Don't want last digit (4 bits)

                        V[i14] += V[i24];

                        //All registers are 8 bits -- value greater than 255 requires carry
                         V[0x000F] = (V[i14] > 0x0FF) ? (short) 1 : 0; //V15 is flag
                        break;
                    case 0x0005: //Sub
                        int i15 = ((opcode & 0x0F00) >> 8);
                        int i25 = ((opcode & 0x00F0) >> 4);

                        V[0x000F] = (V[i15] > V[i25]) ? (short) 1 : 0; //Flag set to NOT borrow

                        V[i15] -= V[i25];
                        break;
                    case 0x0006: //SHR?
                        V[0x000F] = ((V[(opcode & 0x0F00) >> 8] & 0x0001) == 1) ? (short) 1 : 0;
                        V[(opcode & 0x0F00) >> 8] >>= 1; //Shift off lsb --> /= 2
                        break;
                    case 0x0007:
                        int i17 =  ((opcode & 0x0F00) >> 8);
                        int i27 = ((opcode & 0x00F0) >> 4);

                        V[0x000F] = (V[i27] > V[i17]) ? (short) 1 : 0; //Flag set to NOT borrow

                        V[i17] = (short) (V[i27] - V[i17]);
                        break;
                    case 0x000E:
                        V[0x000F] =  ((V[(opcode & 0x0F00) >> 8] >> 7) == 1) ? (short) 1 : 0;
                        V[(opcode & 0x0F00) >> 8] <<= 1;
                        break;
                    default: //Invalid
                }
                pc += 2;
                break;
            case 0x9000:
                if(V[(opcode & 0x0F00) >> 8] != V[(opcode & 0x00F0) >> 4])
                    pc += 4;
                else
                    pc += 2;
                break;
            case 0xA000:
                I = (short) (opcode & 0x0FFF);
                pc += 2;
                break;
            case 0xB000:
                pc = (short) ((opcode & 0x0FFF) + V[0]);
                break;
            case 0xC000:
                break;
            case 0xD000:
                int i1 = ((opcode & 0x0F00) >> 8); //Don't want last 2 digits
                int i2 = ((opcode & 0x00F0) >> 4); //Don't want last digit (4 bits)
                int height = (opcode & 0x000F);

                break;
            case 0xE000:
                switch(opcode & 0x00FF) {
                    case 0x009E: //
                        break;
                    case 0x00A1: //
                        break;
                    default: //Invalid
                }
                break;
            case 0xF000:
                switch(opcode & 0x00FF) {
                    case 0x0007:
                        break;
                    case 0x000A:
                        break;
                    case 0x0015:
                        break;
                    case 0x0018:
                        break;
                    case 0x001E:
                        break;
                    case 0x0029:
                        break;
                    case 0x0033:
                        //Copied from
                        //http://www.multigesture.net/wp-content/uploads/mirror/goldroad/chip8.shtml
                        memory[I] = (short) (V[((opcode&0x0F00)>>8)]/100);
                        memory[I+1] = (short) ((V[((opcode&0x0F00)>>8)]/10)%10);
                        memory[I+2] = (short) ((V[((opcode&0x0F00)>>8)]%100)%10);
                        pc+=2;
                        break;
                    case 0x0055:
                        break;
                    case 0x0065:
                        break;
                    default: //Invalid

                }
                break;
        }
    }

    public void cycle(){
        fetch();
        execute();
    }

    public void updateKeysPressed(){

    }

    public int getCurrentOpcode(){
        return I;
    }

    public boolean redrawRequired(){
        return true;
    }
}
