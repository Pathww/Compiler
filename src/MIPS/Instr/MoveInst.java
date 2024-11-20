package MIPS.Instr;

import MIPS.Operand;

public class MoveInst extends Instruction {
    private MipsInstrType type;
    private Operand dst;
    private Operand src;

    public MoveInst(Operand dst, Operand src) {
        super();
        type = MipsInstrType.MOVE;
        this.dst = dst;
        this.src = src;
    }

    public String toString() {
        return String.format("move %s, %s\n", dst, src);
    }

    public MipsInstrType getInstrType() {
        return type;
    }
}
