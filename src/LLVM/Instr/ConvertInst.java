package LLVM.Instr;

import LLVM.ConstInteger;
import LLVM.Type.IRType;
import LLVM.Value;
import MIPS.Immediate;
import MIPS.Instr.MipsInstrType;
import MIPS.MipsBuilder;
import MIPS.Register;

public class ConvertInst extends Instruction {
    public ConvertInst(InstrType type, Value value, IRType irType) {
        super(type, irType);
        this.addValue(value, 0);
    }

    public void buildMips() {
        Register rd, rs;
        if (MipsBuilder.hasAlloc(this)) {
            rd = MipsBuilder.getAllocReg(this);
        } else {
            rd = MipsBuilder.allocTemp(this);
        }

        Value value = getValue(0);
        if (getInstrType() == InstrType.ZEXT) { /// todo 优化：消除 zext
            if (value instanceof ConstInteger) {
                MipsBuilder.addLoadInst(MipsInstrType.LI, rd, new Immediate(value.getName()));
            } else {
                if (MipsBuilder.hasAlloc(value)) {
                    MipsBuilder.addMoveInst(rd, MipsBuilder.getAllocReg(value));
                } else {
                    int offset = MipsBuilder.getOffset(value);
                    MipsBuilder.addLoadInst(rd, new Immediate(offset), Register.sp);
                }
            }
        } else if (getInstrType() == InstrType.TRUNC) { /// 一定是转i8吗？？？
            if (value instanceof ConstInteger) {
                int imm = ((ConstInteger) value).getValue() & 0xFF;
                MipsBuilder.addLoadInst(MipsInstrType.LI, rd, new Immediate(imm));
            } else {
                rs = MipsBuilder.getAllocReg(value);
                MipsBuilder.addBinaryInst(MipsInstrType.AND, rd, rs, new Immediate("0xFF"));
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s %s to %s\n", getName(), getInstrType().toString(), getValue(0).getType(), getValue(0).getName(), getType());
    }
}
