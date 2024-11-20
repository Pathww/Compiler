package LLVM.Instr;

import LLVM.ConstInteger;
import LLVM.Function;
import LLVM.GlobalVariable;
import LLVM.Type.IntegerType;
import LLVM.Type.PointerType;
import LLVM.Value;
import MIPS.Immediate;
import MIPS.Instr.MipsInstrType;
import MIPS.MipsBuilder;
import MIPS.Operand;
import MIPS.Register;

import java.util.ArrayList;

public class CallInst extends Instruction {
    public CallInst(Function func, ArrayList<Value> values) {
        super(InstrType.CALL, func.getType());
        this.addValue(func, 0);
        for (int i = 0; i < values.size(); i++) {
            this.addValue(values.get(i), i + 1);
        }
        if (func.getType() == IntegerType.VOID) {
            hasName = false;
        }
    }

    public void buildMips() {
        Function func = (Function) getValue(0);
        switch (func.getName()) {
            case "@getint":
            case "@getchar":
                MipsBuilder.addSyscallInst((func.getName().equals("@getint")) ? 5 : 12);
                MipsBuilder.addMoveInst(MipsBuilder.allocTemp(this), Register.v0);
                return;
            case "@putint": /// 勿忘参数为常数的情况！！！
            case "@putch":
                // 一定在reg里吗？？？
                if (getValue(1) instanceof ConstInteger) {
                    MipsBuilder.addLoadInst(MipsInstrType.LI, Register.a0, new Immediate(getValue(1).getName()));
                } else {
                    if (MipsBuilder.hasAlloc(getValue(1))) {
                        MipsBuilder.addMoveInst(Register.a0, MipsBuilder.getAllocReg(getValue(1)));
                    } else {
                        int offset = MipsBuilder.getOffset(getValue(1));
                        MipsBuilder.addLoadInst(Register.a0, new Immediate(offset), Register.sp);
                    }
                }
                MipsBuilder.addSyscallInst((func.getName().equals("@putint")) ? 1 : 11);
                return;
            case "@putstr":
                // 参数一定为 gep
                Value pointer = ((GetElementPtrInst) getValue(1)).getPointer();
                MipsBuilder.addLoadInst(MipsInstrType.LA, Register.a0, new Operand(pointer.getName().substring(2)));
                MipsBuilder.addSyscallInst(4);
                return;
        }

        int raOffset = 0;
        if (!MipsBuilder.curFunction.getName().equals("main")) {
            raOffset = MipsBuilder.addOffset(-4);
            MipsBuilder.addStoreInst(Register.ra, new Immediate(raOffset), Register.sp);
        }
        MipsBuilder.saveGlobals();
        MipsBuilder.saveTemps();
//        MipsBuilder.clearTemps();
        // 之后可以随意选择寄存器使用
        for (int i = values.size() - 1; i >= 1; i--) {
            Value value = getValue(i);
            if (i <= 3) { ///todo: 可以用a0吗？？？
                Register reg = Register.get(i + 4);
                if (value instanceof ConstInteger) {
                    MipsBuilder.addLoadInst(MipsInstrType.LI, reg, new Immediate(value.getName()));
                } else if (value instanceof GlobalVariable) { /// 会有这种情况吗？？？ SSA会遇到？？？
                    if (((PointerType) value.getType()).getRefType().isArray()) { /// todo 统一约定存数组起始地址
                        MipsBuilder.addLoadInst(MipsInstrType.LA, reg, new Operand(value.getName().substring(1)));
                    } else {
                        MipsBuilder.addLoadInst(reg, new Operand(value.getName().substring(1)), Register.zero);
                    }
                } else if (value instanceof GetElementPtrInst) { /// 一定为全局/局部数组
                    Value pointer = ((GetElementPtrInst) value).getPointer();
                    if (pointer instanceof GlobalVariable) {
                        MipsBuilder.addLoadInst(MipsInstrType.LA, reg, new Operand(pointer.getName().substring(1)));
                    } else {
                        if (MipsBuilder.hasAlloc(pointer)) { /// 函数传参中的数组才会在reg中 会出现这种情况吗
                            MipsBuilder.addMoveInst(reg, MipsBuilder.getAllocReg(pointer));
                        } else {
                            /// 忽略了一种情况：函数传参中的数组存在栈上，load store也是，改！！！
                            int off = MipsBuilder.getOffset(pointer);
                            MipsBuilder.addBinaryInst(MipsInstrType.ADD, reg, Register.sp, new Immediate(off));
                        }
                    }
                } else {
                    if (MipsBuilder.hasAlloc(value)) {
                        MipsBuilder.addMoveInst(reg, MipsBuilder.getAllocReg(value));
                    } else {
                        int offset = MipsBuilder.getOffset(value);
                        MipsBuilder.addLoadInst(reg, new Immediate(offset), Register.sp);
                    }
                }
            } else {
                int offset = MipsBuilder.addOffset(-4);
                if (value instanceof ConstInteger) {
                    if (((ConstInteger) value).getValue() == 0) {
                        MipsBuilder.addStoreInst(Register.zero, new Immediate(offset), Register.sp);
                    } else {
                        MipsBuilder.addLoadInst(MipsInstrType.LI, Register.v0, new Immediate(value.getName()));
                        MipsBuilder.addStoreInst(Register.v0, new Immediate(offset), Register.sp);
                    }
                } else if (value instanceof GlobalVariable) {
                    if (((PointerType) value.getType()).getRefType().isArray()) { /// todo 统一约定存数组起始地址
                        MipsBuilder.addLoadInst(MipsInstrType.LA, Register.v0, new Operand(value.getName().substring(1)));
                    } else {
                        MipsBuilder.addLoadInst(Register.v0, new Operand(value.getName().substring(1)), Register.zero);
                    }
                    MipsBuilder.addStoreInst(Register.v0, new Immediate(offset), Register.sp);
                } else if (value instanceof GetElementPtrInst) {
                    Value pointer = ((GetElementPtrInst) value).getPointer();
                    if (pointer instanceof GlobalVariable) {
                        MipsBuilder.addLoadInst(MipsInstrType.LA, Register.v0, new Operand(pointer.getName().substring(1)));
                        MipsBuilder.addStoreInst(Register.v0, new Immediate(offset), Register.sp);
                    } else {
                        if (MipsBuilder.hasAlloc(pointer)) { /// 函数传参中的数组才会在reg中
                            MipsBuilder.addStoreInst(MipsBuilder.getAllocReg(pointer), new Immediate(offset), Register.sp);
                        } else {
                            int off = MipsBuilder.getOffset(pointer);
                            MipsBuilder.addBinaryInst(MipsInstrType.ADD, Register.v0, Register.sp, new Immediate(off));
                            MipsBuilder.addStoreInst(Register.v0, new Immediate(offset), Register.sp);
                        }
                    }
                } else {
                    if (MipsBuilder.hasAlloc(value)) {
                        MipsBuilder.addStoreInst(MipsBuilder.getAllocReg(value), new Immediate(offset), Register.sp);
                    } else {
                        int off = MipsBuilder.getOffset(value);
                        MipsBuilder.addLoadInst(Register.v0, new Immediate(off), Register.sp);
                        MipsBuilder.addStoreInst(Register.v0, new Immediate(offset), Register.sp);
                    }
                }
            }
        }
        /// 优化措施？？？
//        MipsBuilder.clearGlobals();
        MipsBuilder.clearTemps();

        int offset = MipsBuilder.getOffset();
        if (offset != 0) {
            MipsBuilder.addBinaryInst(MipsInstrType.ADD, Register.sp, Register.sp, new Immediate(offset));
        }
        MipsBuilder.addBranchInst(MipsInstrType.JAL, new Operand(func.getName().substring(1)));
        if (offset != 0) {
            MipsBuilder.addBinaryInst(MipsInstrType.ADD, Register.sp, Register.sp, new Immediate(-offset));
        }

        MipsBuilder.restoreGlobals();
//        MipsBuilder.restoreTemps();

        if (!MipsBuilder.curFunction.getName().equals("main")) {
            MipsBuilder.addLoadInst(Register.ra, new Immediate(raOffset), Register.sp);
        }

        if (getType() != IntegerType.VOID) {
            MipsBuilder.addMoveInst(MipsBuilder.allocTemp(this), Register.v0);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getType() != IntegerType.VOID) {
            sb.append(String.format("%s = ", getName()));
        }
        sb.append(String.format("call %s %s(", values.get(0).getType(), values.get(0).getName()));
        for (int i = 1; i < values.size(); i++) {
            sb.append(String.format("%s %s", values.get(i).getType(), values.get(i).getName()));
            if (i < values.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")\n");
        return sb.toString();
    }
}
