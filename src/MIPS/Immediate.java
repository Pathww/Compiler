package MIPS;

public class Immediate extends Operand {
    private int value;

    public Immediate(String value) {
        super(value);
        this.value = Integer.parseInt(value);
    }

    public Immediate(int value) {
        super(String.valueOf(value));
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
