package LLVM.Instr;

import LLVM.Type.IntegerType;
import LLVM.Value;

public class CmpInst extends Instruction {
    InstrType cond;

    public CmpInst(InstrType cond, Value left, Value right) {
        super(InstrType.ICMP, IntegerType.I1);
        this.cond = cond;
        this.addValue(left, 0);
        this.addValue(right, 1);
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s %s, %s\n", getName(), cond.toString(), getValue(0).getType(), getValue(0).getName(), getValue(1).getName());
    }
}
