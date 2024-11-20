package LLVM.Instr;

import LLVM.ConstInteger;
import LLVM.Type.IntegerType;
import LLVM.Value;
import MIPS.Immediate;
import MIPS.Instr.MipsInstrType;
import MIPS.MipsBuilder;
import MIPS.Operand;
import MIPS.Register;

public class CmpInst extends Instruction {
    InstrType cond;

    public CmpInst(InstrType cond, Value left, Value right) {
        super(InstrType.ICMP, IntegerType.I1);
        this.cond = cond;
        this.addValue(left, 0);
        this.addValue(right, 1);
    }

    public void buildMips() {
        Operand rd, rs, rt;
        Value left = getValue(0);
        Value right = getValue(1);

        if (MipsBuilder.hasAlloc(this)) {
            rd = MipsBuilder.getAllocReg(this);
        } else {
            rd = MipsBuilder.allocTemp(this);
        }

        if (left instanceof ConstInteger && right instanceof ConstInteger) {
            int result = cmpConst((ConstInteger) left, (ConstInteger) right);
            MipsBuilder.addLoadInst(MipsInstrType.LI, rd, new Immediate(result));
            return;
        }

        if (left instanceof ConstInteger) {
            if (((ConstInteger) left).getValue() == 0) {
                rs = Register.zero;
            } else {
                rs = new Immediate(left.getName());
            }
        } else {
            rs = MipsBuilder.getAllocReg(left);
        }

        if (right instanceof ConstInteger) {
            if (((ConstInteger) right).getValue() == 0) {
                rt = Register.zero;
            } else {
                rt = new Immediate(right.getName());
            }
        } else {
            rt = MipsBuilder.getAllocReg(right);
        }

        MipsInstrType instrType = null;
        switch (cond) {
            case EQ:
                instrType = MipsInstrType.SEQ;
                break;
            case NE:
                instrType = MipsInstrType.SNE;
                break;
            case SLT:
                instrType = MipsInstrType.SLT;
                break;
            case SGT:
                instrType = MipsInstrType.SGT;
                break;
            case SLE:
                instrType = MipsInstrType.SLE;
                break;
            case SGE:
                instrType = MipsInstrType.SGE; /// 拓展指令，不能使用 $1
                break;
        }

        if (rs instanceof Immediate) {
            if (instrType == MipsInstrType.SLT) {
                instrType = MipsInstrType.SGT;
            } else if (instrType == MipsInstrType.SGT) {
                instrType = MipsInstrType.SLTI;
            } else if (instrType == MipsInstrType.SLE) {
                instrType = MipsInstrType.SGE;
            } else if (instrType == MipsInstrType.SGE) {
                instrType = MipsInstrType.SLE;
            }
            MipsBuilder.addCmpInst(instrType, rd, rt, rs);
        } else {
            if (rt instanceof Immediate && instrType == MipsInstrType.SLT) {
                instrType = MipsInstrType.SLTI;
            }
            MipsBuilder.addCmpInst(instrType, rd, rs, rt);
        }
    }

    /// todo: 添加功能时别忘改这里，考虑全面
    private int cmpConst(ConstInteger left, ConstInteger right) {
        boolean result = false;
        switch (cond) { /// not getInstrType() 细心，改bug要改全！！！
            case EQ:
                result = left.getValue() == right.getValue();
                break;
            case NE:
                result = left.getValue() != right.getValue();
                break;
            case SLT:
                result = left.getValue() < right.getValue();
                break;
            case SGT:
                result = left.getValue() > right.getValue();
                break;
            case SLE:
                result = left.getValue() <= right.getValue();
                break;
            case SGE:
                result = left.getValue() >= right.getValue();
                break;
        }
        if (result) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s %s, %s\n", getName(), cond.toString(), getValue(0).getType(), getValue(0).getName(), getValue(1).getName());
    }
}
