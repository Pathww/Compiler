package LLVM.Instr;

import LLVM.ConstInteger;
import LLVM.GlobalVariable;
import LLVM.Type.IntegerType;
import LLVM.Value;
import MIPS.Immediate;
import MIPS.Instr.MipsInstrType;
import MIPS.MipsBuilder;
import MIPS.Operand;
import MIPS.Register;

public class StoreInst extends Instruction {
    public StoreInst(Value value, Value pointer) {
        super(InstrType.STORE, IntegerType.VOID);
        this.addValue(value, 0);
        this.addValue(pointer, 1);
        hasName = false;
    }

    public void buildMips() {
        /// todo;有些寄存器用完可以接着释放，添加一个释放寄存器的方法？？？？release？？？
        Value value = getValue(0);
        Value pointer = getValue(1);
        if (pointer instanceof GlobalVariable) { // 全局变量一定为地址，无寄存器
            Operand rs;
            if (value instanceof ConstInteger) {
                if (((ConstInteger) value).getValue() == 0) {
                    rs = Register.zero;
                } else {
                    rs = MipsBuilder.allocTemp(value);
                    MipsBuilder.addLoadInst(MipsInstrType.LI, rs, new Immediate(value.getName()));
                }
            } else {
                rs = MipsBuilder.getAllocReg(value);
            }
            MipsBuilder.addStoreInst(rs, new Operand(pointer.getName().substring(1)), Register.zero);
        } else if (pointer instanceof GetElementPtrInst) {
            Operand rt;
            if (value instanceof ConstInteger) {
                if (((ConstInteger) value).getValue() == 0) {
                    rt = Register.zero;
                } else {
                    rt = MipsBuilder.allocTemp(value);
                    MipsBuilder.addLoadInst(MipsInstrType.LI, rt, new Immediate(value.getName()));
                }
            } else {
                rt = MipsBuilder.getAllocReg(value);
            }

            Value index = ((GetElementPtrInst) pointer).getIndex();
            pointer = ((GetElementPtrInst) pointer).getPointer();

            if (pointer instanceof GlobalVariable) {
                Operand base;
                if (index instanceof ConstInteger) {
                    if (((ConstInteger) index).getValue() == 0) {
                        base = Register.zero;
                    } else {
                        base = Register.v0;
                        MipsBuilder.addLoadInst(MipsInstrType.LI, base, new Immediate(((ConstInteger) index).getValue() * 4));
                    }
                } else {
                    base = Register.v0;
                    MipsBuilder.addBinaryInst(MipsInstrType.SLL, base, MipsBuilder.getAllocReg(index), new Immediate(2));
                }
                MipsBuilder.addStoreInst(rt, new Operand(pointer.getName().substring(1)), base);
            } else { // 局部数组
                if (MipsBuilder.hasAlloc(pointer) || pointer instanceof LoadInst) { /// 函数参数；数组 分配寄存器/存到栈上
                    Operand base; /// 和前面的globalvalue类似，转SSA会不会有问题？？？
                    if (index instanceof ConstInteger) {
                        base = new Immediate(((ConstInteger) index).getValue() * 4);
                        MipsBuilder.addStoreInst(rt, base, MipsBuilder.getAllocReg(pointer));
                    } else {
                        base = MipsBuilder.getAllocReg(index);
                        MipsBuilder.addBinaryInst(MipsInstrType.SLL, Register.v0, base, new Immediate(2));
                        MipsBuilder.addBinaryInst(MipsInstrType.ADD, Register.v0, Register.v0, MipsBuilder.getAllocReg(pointer));
                        MipsBuilder.addStoreInst(rt, new Immediate(0), Register.v0);
                    }
                } else {
                    int offset = MipsBuilder.getOffset(pointer);
                    if (index instanceof ConstInteger) {
                        MipsBuilder.addStoreInst(rt, new Immediate(offset + ((ConstInteger) index).getValue() * 4), Register.sp);
                    } else {
                        Operand base = MipsBuilder.getAllocReg(index);
                        MipsBuilder.addBinaryInst(MipsInstrType.SLL, Register.v0, base, new Immediate(2)); /// rd 可以用同一个寄存器吗？？？
                        MipsBuilder.addBinaryInst(MipsInstrType.ADD, Register.v0, Register.v0, new Immediate(offset));
                        MipsBuilder.addBinaryInst(MipsInstrType.ADD, Register.v0, Register.v0, Register.sp);
                        MipsBuilder.addStoreInst(rt, new Immediate(0), Register.v0);
                    }
                }
            }
        } else {  // 转为寄存器操作，一定是全局寄存器变量？？？
            if (MipsBuilder.hasAlloc(pointer)) {
                Operand rt = MipsBuilder.getAllocReg(pointer);
                if (value instanceof ConstInteger) {
                    MipsBuilder.addLoadInst(MipsInstrType.LI, rt, new Immediate(value.getName()));
                } else {
                    Operand rs;
                    if (MipsBuilder.hasAlloc(value)) {
                        rs = MipsBuilder.getAllocReg(value);
                        MipsBuilder.addMoveInst(rt, rs);
                    } else {
                        int offset = MipsBuilder.getOffset(value);
                        MipsBuilder.addLoadInst(rt, new Immediate(offset), Register.sp);
                    }
                }
            } else {
                int offset = MipsBuilder.getOffset(pointer);
                if (value instanceof ConstInteger) {
                    if (((ConstInteger) value).getValue() == 0) {
                        MipsBuilder.addStoreInst(Register.zero, new Immediate(offset), Register.sp);
                    } else {
                        MipsBuilder.addLoadInst(MipsInstrType.LI, Register.v0, new Immediate(value.getName()));
                        MipsBuilder.addStoreInst(Register.v0, new Immediate(offset), Register.sp);
                    }
                } else {
                    Operand rs = MipsBuilder.getAllocReg(value);
                    MipsBuilder.addStoreInst(rs, new Immediate(offset), Register.sp);
                }
            }
        }
    }

    @Override
    public String toString() {
        return String.format("store %s %s, %s %s\n", getValue(0).getType(), getValue(0).getName(), getValue(1).getType(), getValue(1).getName());
    }
}
