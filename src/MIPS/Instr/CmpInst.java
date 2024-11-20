package MIPS.Instr;

import MIPS.Operand;

public class CmpInst extends Instruction {
    private MipsInstrType instrType;
    private Operand result, left, right;

    public CmpInst(MipsInstrType instrType, Operand result, Operand left, Operand right) {
        super();
        this.instrType = instrType;
        this.result = result;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s, %s\n", instrType, result, left, right);
    }

    public MipsInstrType getInstrType() {
        return instrType;
    }
}
