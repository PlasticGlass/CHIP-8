package ca.plasticglass.chip8;

/**
 * Created by Zubair Waheed on 3/13/2018.
 */
public class CPU {
    private byte[] V; //General purpose registers -- V15 is flag -- should not be used by any instruction
    private short I; //Used to store memory addresses
    private short pc;
    private short opcode;
    private byte[] pixels;
    private byte delayTimer;
    private byte soundTimer;
    private short[] stack;
    private short stackPointer;
    private byte[] keypad;
    private Screen screen;
    private Memory memory;
    private boolean redrawStatus;

    public CPU(Memory memory, Screen screen){
        V = new byte[16];
        pixels = new byte[2048];
        stack = new short[16];
        pc = memory.getLoadPoint();
        this.screen = screen;
        this.memory = memory;
    }

    /**
     * Fetches current instruction
     * Each instruction is 2 bytes in size but memory is byte addressable so bytes need to be merged
     * Stored in Big-Endian so top first
     */
    private void fetch(){
        short top = memory.get(pc++);
        short bottom = memory.get(pc++);
        opcode = (short) ((top << 8) | bottom);
    }

    /**
     * Decodes and executes current instruction
     */
    private void execute(){
        //Filter based on first hex digit first
        switch(opcode & 0xF000){
            case 0x0000:
                switch(opcode) {
                    case 0x00E0: //CLS
                        break;
                    case 0x00EE: //Return from subroutine
                        break;
                    default: //Call program at nnn

                }
                break;
            case 0x1000:
                break;
            case 0x2000:
                break;
            case 0x3000:
                break;
            case 0x4000:
                break;
            case 0x5000:
                break;
            case 0x6000:
                break;
            case 0x7000:
                break;
            case 0x8000:
                //Filter based on last hex digit
                switch(opcode & 0x000F) {
                    case 0x0000: //CLS
                        break;
                    case 0x0001:
                        break;
                    case 0x0002:
                        break;
                    case 0x0003:
                        break;
                    case 0x0004:
                        break;
                    case 0x0005:
                        break;
                    case 0x0006:
                        break;
                    case 0x0007: //Return from subroutine
                        break;
                    case 0x000E:
                        break;
                    default: //Invalid

                }
                break;
            case 0x9000:
                break;
            case 0xA000:
                break;
            case 0xB000:
                break;
            case 0xC000:
                break;
            case 0xD000:
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

    public short getCurrentOpcode(){
        return I;
    }

    public boolean redrawRequired(){
        return true;
    }
}
