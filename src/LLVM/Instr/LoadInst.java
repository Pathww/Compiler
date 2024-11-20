package LLVM.Instr;

import LLVM.ConstInteger;
import LLVM.GlobalVariable;
import LLVM.Type.PointerType;
import LLVM.Value;
import MIPS.Immediate;
import MIPS.Instr.MipsInstrType;
import MIPS.MipsBuilder;
import MIPS.Operand;
import MIPS.Register;

public class LoadInst extends Instruction {
    public LoadInst(Value pointer) {
        super(InstrType.LOAD, ((PointerType) pointer.getType()).getRefType());
        this.addValue(pointer, 0);
    }

    public void buildMips() {
        Operand rt;
        if (MipsBuilder.hasAlloc(this)) {
            rt = MipsBuilder.getAllocReg(this);
        } else {
            rt = MipsBuilder.allocTemp(this);
        }
        Value pointer = getValue(0);
        if (pointer instanceof GlobalVariable) {
            MipsBuilder.addLoadInst(rt, new Operand(pointer.getName().substring(1)), Register.zero);
        } else if (pointer instanceof GetElementPtrInst) {
            Value index = ((GetElementPtrInst) pointer).getIndex();
            pointer = ((GetElementPtrInst) pointer).getPointer();

            if (pointer instanceof GlobalVariable) {
                Operand base;
                if (index instanceof ConstInteger) {
                    if (((ConstInteger) index).getValue() == 0) {
                        base = Register.zero;
                    } else {
                        base = rt;
                        MipsBuilder.addLoadInst(MipsInstrType.LI, base, new Immediate(((ConstInteger) index).getValue() * 4));
                    }
                } else {
                    base = rt;
                    MipsBuilder.addBinaryInst(MipsInstrType.SLL, base, MipsBuilder.getAllocReg(index), new Immediate(2));
                }
                MipsBuilder.addLoadInst(rt, new Operand(pointer.getName().substring(1)), base);
            } else { // 局部数组
                if (MipsBuilder.hasAlloc(pointer) || pointer instanceof LoadInst) { /// 函数参数；数组
                    Operand base; /// 和前面的globalvalue类似
                    if (index instanceof ConstInteger) {
                        base = new Immediate(((ConstInteger) index).getValue() * 4);
                        MipsBuilder.addLoadInst(rt, base, MipsBuilder.getAllocReg(pointer));
                    } else {
                        base = MipsBuilder.getAllocReg(index);
                        MipsBuilder.addBinaryInst(MipsInstrType.SLL, rt, base, new Immediate(2));
                        MipsBuilder.addBinaryInst(MipsInstrType.ADD, rt, rt, MipsBuilder.getAllocReg(pointer));
                        MipsBuilder.addLoadInst(rt, new Immediate(0), rt);
                    }
                } else {
                    int offset = MipsBuilder.getOffset(pointer);
                    if (index instanceof ConstInteger) {
                        MipsBuilder.addLoadInst(rt, new Immediate(offset + ((ConstInteger) index).getValue() * 4), Register.sp);
                    } else {
                        Operand base = MipsBuilder.getAllocReg(index);
                        /// 常量传播！！！
                        MipsBuilder.addBinaryInst(MipsInstrType.SLL, rt, base, new Immediate(2)); /// rd 可以用同一个寄存器吗？？？
                        MipsBuilder.addBinaryInst(MipsInstrType.ADD, rt, rt, new Immediate(offset));
                        MipsBuilder.addBinaryInst(MipsInstrType.ADD, rt, rt, Register.sp);
                        MipsBuilder.addLoadInst(rt, new Immediate(0), rt);
                    }
                }
            }
        } else {
            if (MipsBuilder.hasAlloc(pointer)) {
                MipsBuilder.addMoveInst(rt, MipsBuilder.getAllocReg(pointer)); /// 对吗？？？？？区分reg存地址还是值？？？
            } else {
                int offset = MipsBuilder.getOffset(pointer);
                MipsBuilder.addLoadInst(rt, new Immediate(offset), Register.sp);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s = load %s, %s %s\n", getName(), getType(), getValue(0).getType(), getValue(0).getName());
    }
}
