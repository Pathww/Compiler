package LLVM.Instr;

import LLVM.BasicBlock;
import LLVM.ConstInteger;
import LLVM.Type.IntegerType;
import LLVM.Value;
import MIPS.Immediate;
import MIPS.Instr.MipsInstrType;
import MIPS.MipsBuilder;
import MIPS.Register;

import java.util.ArrayList;

public class BranchInst extends Instruction {
    public boolean isCond;

    public BranchInst(Value cond, BasicBlock iftrue, BasicBlock iffalse) {
        super(InstrType.BR, IntegerType.VOID);
        this.isCond = true;
        this.addValue(cond);
        this.addValue(iftrue);
        this.addValue(iffalse);
        hasName = false;
    }

    public BranchInst(BasicBlock dest) {
        super(InstrType.BR, IntegerType.VOID);
        this.isCond = false;
        this.addValue(dest);
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
        if (isCond) {
            if (getValue(0) instanceof ConstInteger) {
                if (((ConstInteger) getValue(0)).getValue() == 1) {
                    MipsBuilder.addBranchInst(MipsInstrType.J, getValue(1));
                } else {
                    MipsBuilder.addBranchInst(MipsInstrType.J, getValue(2));
                }
            } else {
                Register rs = MipsBuilder.getAllocReg(getValue(0)); // 一定不会跨越函数？？？
                /// 写回基本块内的临时寄存器
                MipsBuilder.addBranchInst(MipsInstrType.BEQ, rs, Register.zero, getValue(2));
                MipsBuilder.addBranchInst(MipsInstrType.J, getValue(1));
            }
        } else {
            /// 写回基本块内的临时寄存器
            MipsBuilder.addBranchInst(MipsInstrType.J, getValue(0));
        }
    }

    public Value getDef() {
        return null;
    }

    public ArrayList<Value> getUses() {
        ArrayList<Value> uses = new ArrayList<>();
        if (isCond) {
            if (isLiveVar(getValue(0))) {
                uses.add(getValue(0));
            }
        }
        return uses;
    }
}
