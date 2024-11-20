package LLVM.Instr;

import LLVM.ConstInteger;
import LLVM.Type.IntegerType;
import LLVM.Value;
import MIPS.Immediate;
import MIPS.Instr.MipsInstrType;
import MIPS.MipsBuilder;
import MIPS.Register;

public class ReturnInst extends Instruction {
    public ReturnInst() {
        super(InstrType.RET, IntegerType.VOID);
        hasName = false;
    }

    public ReturnInst(Value value) {
        super(InstrType.RET, value.getType());
        this.addValue(value, 0);
        hasName = false;
    }

    public void buildMips() {
        if (getType() != IntegerType.VOID) {
            /// todo; 处理寄存器的问题！！！
            Value ret = getValue(0);
            if (ret instanceof ConstInteger) {
                MipsBuilder.addLoadInst(MipsInstrType.LI, Register.v0, new Immediate(ret.getName()));
            } else {
                if (MipsBuilder.hasAlloc(ret)) {
                    MipsBuilder.addMoveInst(Register.v0, MipsBuilder.getAllocReg(ret));
                } else {
                    MipsBuilder.addLoadInst(Register.v0, new Immediate(MipsBuilder.getOffset(ret)), Register.sp);
                }
            }
        }
        MipsBuilder.addBranchInst(MipsInstrType.JR, Register.ra);
    }

    @Override
    public String toString() {
        if (getType() == IntegerType.VOID) {
            return "ret void\n";
        } else {
            return String.format("ret %s %s\n", getType().toString(), getValue(0).getName());
        }
    }
}
