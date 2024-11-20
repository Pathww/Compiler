package MIPS.Instr;

import MIPS.Operand;

public class StoreInst extends Instruction {
    private MipsInstrType type;
    private Operand rt;
    private Operand offset;
    private Operand base;

    public StoreInst(Operand rt, Operand offset, Operand base) {
        super();
        type = MipsInstrType.SW;
        this.rt = rt;
        this.offset = offset;
        this.base = base;
    }

    @Override
    public String toString() {
        return String.format("sw %s, %s(%s)\n", rt, offset, base);
    }

    public MipsInstrType getInstrType() {
        return type;
    }
}
