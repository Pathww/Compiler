package LLVM.Instr;

import LLVM.BasicBlock;
import LLVM.Type.IntegerType;
import LLVM.Value;

public class BranchInst extends Instruction {
    private boolean isCond;

    public BranchInst(Value cond, BasicBlock iftrue, BasicBlock iffalse) {
        super(InstrType.BR, IntegerType.VOID);
        this.isCond = true;
        this.addValue(cond, 0);
        this.addValue(iftrue, 1);
        this.addValue(iffalse, 2);
        hasName = false;
    }

    public BranchInst(BasicBlock dest) {
        super(InstrType.BR, IntegerType.VOID);
        this.isCond = false;
        this.addValue(dest, 0);
        hasName = false;
    }

    @Override
    public String toString() {
        if (isCond) {
            return String.format("br i1 %s, label %s, label %s\n", getValue(0).getName(), getValue(1).getName(), getValue(2).getName());
        } else {
            return String.format("br label %s\n", getValue(0).getName());
        }
    }
}
