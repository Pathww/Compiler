package MIPS.Instr;


public class Instruction {
    private String name;

    public Instruction(String name) {
        this.name = name;
    }

    public Instruction() {
    }

    public String toString() {
        return name;
    }

    public MipsInstrType getInstrType() {
        return null;
    }
}
