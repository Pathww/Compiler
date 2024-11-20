package MIPS;

public class Immediate extends Operand {
    public Immediate(String value) {
        super(value);
    }

    public Immediate(int value) {
        super(String.valueOf(value));
    }
}
