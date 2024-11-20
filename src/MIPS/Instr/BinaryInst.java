package MIPS.Instr;

import MIPS.Operand;

public class BinaryInst extends Instruction {
    private MipsInstrType instrType;
    private Operand result, left, right;

    public BinaryInst(MipsInstrType instrType, Operand result, Operand left, Operand right) {
        super();
        this.instrType = instrType;
        this.result = result;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (instrType == MipsInstrType.MULT) {
            sb.append(String.format("%s %s, %s\n", instrType, left, right));
            sb.append(String.format("\tmflo %s\n", result));
        } else if (instrType == MipsInstrType.DIV) {
            sb.append(String.format("%s %s, %s\n", instrType, left, right));
            sb.append(String.format("\tmflo %s\n", result));
        } else if (instrType == MipsInstrType.REM) {
            sb.append(String.format("%s %s, %s\n", instrType, left, right));
            sb.append(String.format("\tmfhi %s\n", result));
        } else {
            sb.append(String.format("%s %s, %s, %s\n", instrType, result, left, right));
        }
        return sb.toString();
    }

    public MipsInstrType getInstrType() {
        return instrType;
    }
}
