package MIPS.Instr;

import MIPS.Operand;

public class LoadInst extends Instruction {
    private MipsInstrType type;
    private Operand rt;
    private Operand offset;
    private Operand base;

    public LoadInst(Operand rt, Operand offset, Operand base) {
        super();
        this.type = MipsInstrType.LW;
        this.rt = rt;
        this.offset = offset;
        this.base = base;
    }

    public LoadInst(MipsInstrType type, Operand rt, Operand base) {
        super();
        this.type = type;
        this.rt = rt;
        this.base = base;
    }

    @Override
    public String toString() {
        if (type == MipsInstrType.LW) {
            return String.format("%s %s, %s(%s)\n", type, rt, offset, base);
        } else {
            return String.format("%s %s, %s\n", type, rt, base);
        }
    }

    public MipsInstrType getInstrType() {
        return type;
    }
}
