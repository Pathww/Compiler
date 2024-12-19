package LLVM.Instr;

import LLVM.ConstInteger;
import LLVM.GlobalVariable;
import LLVM.Value;
import MIPS.Immediate;
import MIPS.Instr.MipsInstrType;
import MIPS.MipsBuilder;
import MIPS.Operand;
import MIPS.Register;

import java.util.ArrayList;

public class BinaryInst extends Instruction {

    public BinaryInst(InstrType instrType, Value left, Value right) {
        super(instrType, left.getType());
        this.addValue(left);
        this.addValue(right);
    }

    public void buildMips() {
        Operand rd, rs, rt;
        Value left = getValue(0);
        Value right = getValue(1);

        rd = MipsBuilder.getAllocReg(this);

        /// 勿忘全是常数的情况
        if (left instanceof ConstInteger && right instanceof ConstInteger) {
            int result = calcConst((ConstInteger) left, (ConstInteger) right);
            MipsBuilder.addLoadInst(MipsInstrType.LI, rd, new Immediate(result));
            if (MipsBuilder.isGlobal(this)) {
                MipsBuilder.writeBack(this);
            }
            return;
        }

        if (left instanceof ConstInteger || right instanceof ConstInteger) {
            if (getInstrType() == InstrType.MUL) {
                if (optMul(left, right, rd)) {
                    if (MipsBuilder.isGlobal(this)) {
                        MipsBuilder.writeBack(this);
                    }
                    return;
                }
            } else if (getInstrType() == InstrType.SDIV) {
                if (optDiv(left, right, rd)) {
                    if (MipsBuilder.isGlobal(this)) {
                        MipsBuilder.writeBack(this);
                    }
                    return;
                }
            } else if (getInstrType() == InstrType.SREM) {
                if (optRem(left, right, rd)) {
                    if (MipsBuilder.isGlobal(this)) {
                        MipsBuilder.writeBack(this);
                    }
                    return;
                }
            }
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

        if (MipsBuilder.isGlobal(this)) {
            MipsBuilder.writeBack(this);
        }
    }

    private boolean optRem(Value left, Value right, Operand rd) {
        if (left instanceof ConstInteger && ((ConstInteger) left).getValue() == 0) {
            MipsBuilder.addLoadInst(MipsInstrType.LI, rd, new Immediate(0));
            return true;
        } else if (right instanceof ConstInteger) {
            optDiv(left, right, rd);
            if (isPowerOfTwo(((ConstInteger) right).getValue())) {
                int value = ((ConstInteger) right).getValue();
                int shift = Integer.numberOfTrailingZeros(Math.abs(value));
                MipsBuilder.addBinaryInst(MipsInstrType.SLL, rd, rd, new Immediate(shift));
                if (value < 0) {
                    MipsBuilder.addBinaryInst(MipsInstrType.SUBU, rd, Register.zero, rd);
                }
            } else {
                MipsBuilder.addLoadInst(MipsInstrType.LI, Register.v0, new Immediate(((ConstInteger) right).getValue()));
                MipsBuilder.addBinaryInst(MipsInstrType.MULT, rd, rd, Register.v0);
            }
            MipsBuilder.addBinaryInst(MipsInstrType.SUBU, rd, MipsBuilder.getAllocReg(left), rd);
            return true;
        }
        return false;
    }

    private boolean optDiv(Value left, Value right, Operand rd) {
        if (left instanceof ConstInteger && ((ConstInteger) left).getValue() == 0) {
            MipsBuilder.addLoadInst(MipsInstrType.LI, rd, new Immediate(0));
            return true;
        } else if (right instanceof ConstInteger) {
            int value = ((ConstInteger) right).getValue();
            int d = Math.abs(value);
            if (d == 1) {
                if (MipsBuilder.hasAlloc(left)) {
                    MipsBuilder.addMoveInst(rd, MipsBuilder.getAllocReg(left));
                } else {
                    MipsBuilder.addLoadInst(rd, new Immediate(MipsBuilder.getOffset(left)), Register.sp);
                }
            } else if (isPowerOfTwo(d)) {
                int k = Integer.numberOfTrailingZeros(d);
                Operand rs = MipsBuilder.getAllocReg(left);
                MipsBuilder.addBinaryInst(MipsInstrType.SRA, rd, rs, new Immediate(k - 1));
                MipsBuilder.addBinaryInst(MipsInstrType.SRL, rd, rd, new Immediate(32 - k));
                MipsBuilder.addBinaryInst(MipsInstrType.ADDU, rd, rd, rs);
                MipsBuilder.addBinaryInst(MipsInstrType.SRA, rd, rd, new Immediate(k));
            } else {
                long low = 1L << 31;
                long high = low + 1;
                long l, m;
//                for (l = 0; ; l++) {
//                    m = (low + d - 1) / d;
//                    if (m * d <= high) {
//                        break;
//                    }
//                    low <<= 1;
//                    high <<= 1;
//                }
                for (l = 1; ; l++) {
                    low <<= 1;
                    high <<= 1;
                    m = (low + d - 1) / d;
                    if (m * d <= high) {
                        break;
                    }
                }
                Operand rs = MipsBuilder.getAllocReg(left);
                if (m < (1L << 31)) {
                    MipsBuilder.addLoadInst(MipsInstrType.LI, rd, new Immediate((int) m));
                    MipsBuilder.addBinaryInst(MipsInstrType.MULSH, rd, rd, rs);
                    MipsBuilder.addBinaryInst(MipsInstrType.SRA, rd, rd, new Immediate((int) (l - 1)));
                    MipsBuilder.addBinaryInst(MipsInstrType.SRL, Register.v0, rs, new Immediate(31));
                    MipsBuilder.addBinaryInst(MipsInstrType.ADDU, rd, rd, Register.v0);
                } else {
                    MipsBuilder.addLoadInst(MipsInstrType.LI, rd, new Immediate((int) (m - (1L << 32))));
                    MipsBuilder.addBinaryInst(MipsInstrType.MULSH, rd, rd, rs);
                    MipsBuilder.addBinaryInst(MipsInstrType.ADDU, rd, rd, rs);
                    MipsBuilder.addBinaryInst(MipsInstrType.SRA, rd, rd, new Immediate((int) (l - 1)));
                    MipsBuilder.addBinaryInst(MipsInstrType.SRL, Register.v0, rs, new Immediate(31));
                    MipsBuilder.addBinaryInst(MipsInstrType.ADDU, rd, rd, Register.v0);
                }
            }
            if (value < 0) {
                MipsBuilder.addBinaryInst(MipsInstrType.SUBU, rd, Register.zero, rd);
            }
            return true;
        }
        return false;
    }

    private boolean isPowerOfTwo(int x) {
        x = Math.abs(x);
        return (x & (x - 1)) == 0;
    }

    private boolean optMul(Value left, Value right, Operand rd) {
        if ((left instanceof ConstInteger && ((ConstInteger) left).getValue() == 0) ||
                (right instanceof ConstInteger && ((ConstInteger) right).getValue() == 0)) { /// GVN中可以直接优化！！！
            MipsBuilder.addLoadInst(MipsInstrType.LI, rd, new Immediate(0));
            return true;
        } else if (left instanceof ConstInteger && isPowerOfTwo(((ConstInteger) left).getValue())) {
            int value = ((ConstInteger) left).getValue();
            int shift = Integer.numberOfTrailingZeros(Math.abs(value));
            MipsBuilder.addBinaryInst(MipsInstrType.SLL, rd, MipsBuilder.getAllocReg(right), new Immediate(shift));
            if (value < 0) {
                MipsBuilder.addBinaryInst(MipsInstrType.SUBU, rd, Register.zero, rd);
            }
            return true;
        } else if (right instanceof ConstInteger && isPowerOfTwo(((ConstInteger) right).getValue())) {
            int value = ((ConstInteger) right).getValue();
            int shift = Integer.numberOfTrailingZeros(Math.abs(value));
            MipsBuilder.addBinaryInst(MipsInstrType.SLL, rd, MipsBuilder.getAllocReg(left), new Immediate(shift));
            if (value < 0) {
                MipsBuilder.addBinaryInst(MipsInstrType.SUBU, rd, Register.zero, rd);
            }
            return true;
        }
        return false;
    }

    public int calcConst(ConstInteger left, ConstInteger right) {
        int result = 0;
        try {
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
        } catch (ArithmeticException e) {
            result = 1;
        }
        return result;
    }

    public Value getDef() {
        return this;
    }

    public ArrayList<Value> getUses() {
        ArrayList<Value> uses = new ArrayList<>();
        if (isLiveVar(getValue(0))) {
            uses.add(getValue(0));
        }
        if (isLiveVar(getValue(1))) {
            uses.add(getValue(1));
        }
        return uses;
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

    public String hash() {
        String left = getValue(0).getName();
        String right = getValue(1).getName();
        if (left.compareTo(right) > 0) {
            return right + getInstrType() + left;
        } else {
            return left + getInstrType() + right;
        }
    }
}
