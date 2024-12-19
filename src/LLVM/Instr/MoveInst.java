package LLVM.Instr;

import LLVM.ConstInteger;
import LLVM.Type.IntegerType;
import LLVM.Value;
import MIPS.Immediate;
import MIPS.Instr.MipsInstrType;
import MIPS.MipsBuilder;
import MIPS.Operand;
import MIPS.Register;

import java.util.ArrayList;

public class MoveInst extends Instruction {
    public MoveInst(Value left, Value right) {
        super(InstrType.MOVE, IntegerType.VOID);
        addValue(left);
        addValue(right);
        hasName = false;
    }

    public void buildMips() {
        Value left = getValue(0);
        Value right = getValue(1);
        if (MipsBuilder.hasAlloc(left)) {
            if (MipsBuilder.isGlobal(left)) { /// 同 else-if 分支
                int offset = MipsBuilder.getOffset(left);
                if (right instanceof ConstInteger) {
                    if (((ConstInteger) right).getValue() == 0) {
                        MipsBuilder.addStoreInst(Register.zero, new Immediate(offset), Register.sp);
                    } else {
                        MipsBuilder.addLoadInst(MipsInstrType.LI, Register.v0, new Immediate(right.getName()));
                        MipsBuilder.addStoreInst(Register.v0, new Immediate(offset), Register.sp);
                    }
                } else {
                    MipsBuilder.addStoreInst(MipsBuilder.getAllocReg(right), new Immediate(offset), Register.sp);
                }
            } else {
                Operand rd = MipsBuilder.getAllocReg(left);
                if (right instanceof ConstInteger) {
                    MipsBuilder.addLoadInst(MipsInstrType.LI, rd, new Immediate(right.getName()));
                } else {
                    if (MipsBuilder.hasAlloc(right)) {
                        MipsBuilder.addMoveInst(rd, MipsBuilder.getAllocReg(right));
                    } else {
                        int offset = MipsBuilder.getOffset(right);
                        MipsBuilder.addLoadInst(rd, new Immediate(offset), Register.sp);
                    }
                }
            }
        } else if (MipsBuilder.hasOffset(left)) {
            int offset = MipsBuilder.getOffset(left);
            if (right instanceof ConstInteger) {
                if (((ConstInteger) right).getValue() == 0) {
                    MipsBuilder.addStoreInst(Register.zero, new Immediate(offset), Register.sp);
                } else {
                    MipsBuilder.addLoadInst(MipsInstrType.LI, Register.v0, new Immediate(right.getName()));
                    MipsBuilder.addStoreInst(Register.v0, new Immediate(offset), Register.sp);
                }
            } else {
                MipsBuilder.addStoreInst(MipsBuilder.getAllocReg(right), new Immediate(offset), Register.sp);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("move %s %s, %s %s\n", getValue(0).getType(),
                getValue(0).getName(),
                getValue(1).getType(),
                getValue(1).getName());
    }

    public Value getDef() {
        return getValue(0);
    }

    public ArrayList<Value> getUses() {
        ArrayList<Value> uses = new ArrayList<>();
        if (isLiveVar(getValue(1))) {
            uses.add(getValue(1));
        }
        return uses;
    }
}
