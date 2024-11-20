package LLVM.Instr;

import LLVM.ConstInteger;
import LLVM.Value;
import MIPS.Immediate;
import MIPS.Instr.MipsInstrType;
import MIPS.MipsBuilder;
import MIPS.Operand;
import MIPS.Register;

public class BinaryInst extends Instruction {

    public BinaryInst(InstrType instrType, Value left, Value right) {
        super(instrType, left.getType());
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

        /// 勿忘全是常数的情况
        if (left instanceof ConstInteger && right instanceof ConstInteger) {
            int result = calcConst((ConstInteger) left, (ConstInteger) right);
            MipsBuilder.addLoadInst(MipsInstrType.LI, rd, new Immediate(result));
            return;
        }

        if (left instanceof ConstInteger) { /// 减法无法交换顺序！！！
            if (((ConstInteger) left).getValue() == 0) {
                rs = Register.zero;
            } else {
                if (getInstrType() == InstrType.SUB || getInstrType() == InstrType.MUL || getInstrType() == InstrType.SDIV || getInstrType() == InstrType.SREM) {
                    rs = rd;
                    MipsBuilder.addLoadInst(MipsInstrType.LI, rs, new Immediate(left.getName()));
                } else {
                    rs = new Immediate(left.getName());
                }
            }
        } else {
            rs = MipsBuilder.getAllocReg(left);
        }

        if (right instanceof ConstInteger) {
            if (((ConstInteger) right).getValue() == 0) {
                rt = Register.zero;
            } else {
                if (getInstrType() == InstrType.MUL || getInstrType() == InstrType.SDIV || getInstrType() == InstrType.SREM) {
                    rt = rd;
                    MipsBuilder.addLoadInst(MipsInstrType.LI, rt, new Immediate(right.getName()));
                } else {
                    rt = new Immediate(right.getName());
                }
            }
        } else {
            rt = MipsBuilder.getAllocReg(right);
        }

        MipsInstrType instrType = null;
        switch (getInstrType()) {
            case ADD:
                instrType = MipsInstrType.ADDU;
                break;
            case SUB:
                instrType = MipsInstrType.SUBU;
                break;
            case MUL:
                instrType = MipsInstrType.MULT;
                break;
            case SDIV:
                instrType = MipsInstrType.DIV;
                break;
            case SREM:
                instrType = MipsInstrType.REM;
                break;
            case AND:
                instrType = MipsInstrType.AND;
                break;
            case OR:
                instrType = MipsInstrType.OR;
                break;
        }

        if (rs instanceof Immediate || rt instanceof Immediate) {
            if (instrType == MipsInstrType.ADDU) {
                instrType = MipsInstrType.ADDIU;
            } else if (instrType == MipsInstrType.SUBU) {
                instrType = MipsInstrType.ADDIU;
                rt = new Immediate(-Integer.parseInt(rt.getName()));
            }
        }

        if (rs instanceof Immediate) {
            MipsBuilder.addBinaryInst(instrType, rd, rt, rs);
        } else {
            MipsBuilder.addBinaryInst(instrType, rd, rs, rt);
        }
    }

    private int calcConst(ConstInteger left, ConstInteger right) {
        int result = 0;
        switch (getInstrType()) {
            case ADD:
                result = left.getValue() + right.getValue();
                break;
            case SUB:
                result = left.getValue() - right.getValue();
                break;
            case MUL:
                result = left.getValue() * right.getValue();
                break;
            case SDIV:
                result = left.getValue() / right.getValue();
                break;
            case SREM:
                result = left.getValue() % right.getValue();
                break;
            case AND:
                result = left.getValue() & right.getValue();
                break;
            case OR:
                result = left.getValue() | right.getValue();
                break;
        }
        return result;
    }

    @Override
    public String toString() {
        return
                String.format("%s = %s %s %s, %s\n", getName(),
                        getInstrType().toString(),
                        getValue(0).getType(),
                        getValue(0).getName(),
                        getValue(1).getName());

    }
}
