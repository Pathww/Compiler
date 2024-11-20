package MIPS;

public class Operand {
    private String name;

    public Operand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }
}
