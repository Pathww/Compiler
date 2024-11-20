package LLVM.Instr;

import LLVM.BasicBlock;
import LLVM.Type.IntegerType;
import LLVM.Value;
import MIPS.Immediate;
import MIPS.Instr.MipsInstrType;
import MIPS.MipsBuilder;
import MIPS.Register;

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

    public void buildMips() {
        // todo：需要写回寄存器吗？？？
        if (isCond) {
            Register rs = MipsBuilder.getAllocReg(getValue(0)); // 一定不会跨越函数？？？
            /// 写回基本块内的临时寄存器
            MipsBuilder.addBranchInst(MipsInstrType.BEQ, rs, Register.zero, getValue(2));
            MipsBuilder.addBranchInst(MipsInstrType.J, getValue(1));
        } else {
            /// 写回基本块内的临时寄存器
            MipsBuilder.addBranchInst(MipsInstrType.J, getValue(0));
        }
    }
}
