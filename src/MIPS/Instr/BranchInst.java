package MIPS.Instr;

import MIPS.Operand;

public class BranchInst extends Instruction {
    private MipsInstrType type;
    private Operand rs;
    private Operand rt;
    public Operand label;
    private boolean isCond;

    public BranchInst(MipsInstrType type, Operand rs, Operand rt, Operand label) {
        super();
        this.type = type;
        this.rs = rs;
        this.rt = rt;
        this.label = label;
        this.isCond = true;
    }

    public BranchInst(MipsInstrType type, Operand label) {
        super();
        this.type = type;
        this.label = label;
        this.isCond = false;
    }

    @Override
    public String toString() {
        if (isCond) {
            return String.format("%s %s, %s, %s\n", type, rs, rt, label);
        } else {
            return String.format("%s %s\n", type, label);
        }
    }

    public MipsInstrType getInstrType() {
        return type;
    }
}
