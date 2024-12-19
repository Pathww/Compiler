package Optimizer;

import LLVM.*;
import LLVM.Instr.*;
import LLVM.Module;

import java.util.Iterator;

public class ConstSpread {
    private Module module = IRBuilder.module;

    public ConstSpread() {
        InstrConstSpread();
        GlobalConstSpread();
    }

    public void InstrConstSpread() {
        for (Function func : module.functions) {
            boolean flag = true;
            while (flag) {
                flag = false;
                for (BasicBlock block : func.blocks) {
                    Iterator<Instruction> instrIt = block.instrs.iterator();
                    while (instrIt.hasNext()) {
                        Instruction instr = instrIt.next();
                        if (instr instanceof BinaryInst) {
                            Value value;
                            Value left = instr.getValue(0);
                            Value right = instr.getValue(1);
                            if (left instanceof ConstInteger && right instanceof ConstInteger) {
                                int result = ((BinaryInst) instr).calcConst((ConstInteger) left, (ConstInteger) right);
                                value = new ConstInteger(result, instr.getType());
                                for (Use use : instr.uses) {
                                    use.value = value;
                                    value.addUse(use);
                                    use.user.setValue(use.pos, value);
                                }
                                instrIt.remove();
                                flag = true;
                            } else if (instr.getInstrType() == InstrType.ADD) { /// a+0=0+a=a
                                if (left instanceof ConstInteger && ((ConstInteger) left).getValue() == 0) {
                                    value = right;
                                } else if (right instanceof ConstInteger && ((ConstInteger) right).getValue() == 0) {
                                    value = left;
                                } else {
                                    continue;
                                }
                                Iterator<Use> useIt = value.uses.iterator();
                                while (useIt.hasNext()) {
                                    Use use = useIt.next();
                                    if (use.user.equals(instr)) {
                                        useIt.remove();
                                    }
                                }
                                for (Use use : instr.uses) {
                                    use.value = value;
                                    value.addUse(use);
                                    use.user.setValue(use.pos, value);
                                }
                                instrIt.remove();
                                flag = true;
                            } else if (instr.getInstrType() == InstrType.SUB) {
                                if (right instanceof ConstInteger && ((ConstInteger) right).getValue() == 0) { /// a-0=a
                                    value = left;
                                    Iterator<Use> useIt = value.uses.iterator();
                                    while (useIt.hasNext()) {
                                        Use use = useIt.next();
                                        if (use.user.equals(instr)) {
                                            useIt.remove();
                                        }
                                    }
                                    for (Use use : instr.uses) {
                                        use.value = value;
                                        value.addUse(use);
                                        use.user.setValue(use.pos, value);
                                    }
                                    instrIt.remove();
                                    flag = true;
                                } else if (left.equals(right)) { /// a-a=0
                                    value = new ConstInteger(0, instr.getType());
                                    Iterator<Use> useIt = left.uses.iterator();
                                    while (useIt.hasNext()) {
                                        Use use = useIt.next();
                                        if (use.user.equals(instr)) {
                                            useIt.remove();
                                        }
                                    }
                                    for (Use use : instr.uses) {
                                        use.value = value;
                                        value.addUse(use);
                                        use.user.setValue(use.pos, value);
                                    }
                                    instrIt.remove();
                                    flag = true;
                                }
                            } else if (instr.getInstrType() == InstrType.MUL) {
                                if ((left instanceof ConstInteger && ((ConstInteger) left).getValue() == 0) ||
                                        (right instanceof ConstInteger && ((ConstInteger) right).getValue() == 0)) { /// a*0=0*a=0
                                    value = new ConstInteger(0, instr.getType());
                                    Iterator<Use> useIt;
                                    if (left instanceof ConstInteger) {
                                        useIt = right.uses.iterator();
                                    } else {
                                        useIt = left.uses.iterator();
                                    }
                                    while (useIt.hasNext()) {
                                        Use use = useIt.next();
                                        if (use.user.equals(instr)) {
                                            useIt.remove();
                                        }
                                    }
                                    for (Use use : instr.uses) {
                                        use.value = value;
                                        value.addUse(use);
                                        use.user.setValue(use.pos, value);
                                    }
                                    instrIt.remove();
                                    flag = true;
                                } else if ((left instanceof ConstInteger && ((ConstInteger) left).getValue() == 2) ||
                                        (right instanceof ConstInteger && ((ConstInteger) right).getValue() == 2)) { /// a*2=2*a=a<<1
                                    Iterator<Use> useIt;
                                    if (left instanceof ConstInteger) {
                                        useIt = right.uses.iterator();
                                        value = right;
                                    } else {
                                        useIt = left.uses.iterator();
                                        value = left;
                                    }
                                    while (useIt.hasNext()) {
                                        Use use = useIt.next();
                                        if (use.user.equals(instr)) {
                                            useIt.remove();
                                        }
                                    }
                                    instr.setInstrType(InstrType.ADD);
                                    instr.setValues(value, value);
                                } else if ((left instanceof ConstInteger && ((ConstInteger) left).getValue() == 1) ||
                                        (right instanceof ConstInteger && ((ConstInteger) right).getValue() == 1)) { /// a*1=1*a=a
                                    Iterator<Use> useIt;
                                    if (left instanceof ConstInteger) {
                                        useIt = right.uses.iterator();
                                        value = right;
                                    } else {
                                        useIt = left.uses.iterator();
                                        value = left;
                                    }
                                    while (useIt.hasNext()) {
                                        Use use = useIt.next();
                                        if (use.user.equals(instr)) {
                                            useIt.remove();
                                        }
                                    }
                                    for (Use use : instr.uses) {
                                        use.value = value;
                                        value.addUse(use);
                                        use.user.setValue(use.pos, value);
                                    }
                                    instrIt.remove();
                                    flag = true;
                                } else if ((left instanceof ConstInteger && ((ConstInteger) left).getValue() == -1) ||
                                        (right instanceof ConstInteger && ((ConstInteger) right).getValue() == -1)) { /// a*-1=-1*a=-a
                                    Iterator<Use> useIt;
                                    if (left instanceof ConstInteger) {
                                        useIt = right.uses.iterator();
                                        value = right;
                                    } else {
                                        useIt = left.uses.iterator();
                                        value = left;
                                    }
                                    while (useIt.hasNext()) {
                                        Use use = useIt.next();
                                        if (use.user.equals(instr)) {
                                            useIt.remove();
                                        }
                                    }
                                    instr.setInstrType(InstrType.SUB);
                                    instr.setValues(new ConstInteger(0, instr.getType()), value);
                                }
                            } else if (instr.getInstrType() == InstrType.SDIV) {
                                if (left instanceof ConstInteger && ((ConstInteger) left).getValue() == 0) { /// 0/a=0
                                    value = new ConstInteger(0, instr.getType());
                                    Iterator<Use> useIt = right.uses.iterator();
                                    while (useIt.hasNext()) {
                                        Use use = useIt.next();
                                        if (use.user.equals(instr)) {
                                            useIt.remove();
                                        }
                                    }
                                    for (Use use : instr.uses) {
                                        use.value = value;
                                        value.addUse(use);
                                        use.user.setValue(use.pos, value);
                                    }
                                    instrIt.remove();
                                    flag = true;
                                } else if (right instanceof ConstInteger && ((ConstInteger) right).getValue() == 1) { /// a/1=a
                                    value = left;
                                    Iterator<Use> useIt = left.uses.iterator();
                                    while (useIt.hasNext()) {
                                        Use use = useIt.next();
                                        if (use.user.equals(instr)) {
                                            useIt.remove();
                                        }
                                    }
                                    for (Use use : instr.uses) {
                                        use.value = value;
                                        value.addUse(use);
                                        use.user.setValue(use.pos, value);
                                    }
                                    instrIt.remove();
                                    flag = true;
                                } else if (left.equals(right)) { /// a/a=1
                                    value = new ConstInteger(1, instr.getType());
                                    Iterator<Use> useIt = left.uses.iterator();
                                    while (useIt.hasNext()) {
                                        Use use = useIt.next();
                                        if (use.user.equals(instr)) {
                                            useIt.remove();
                                        }
                                    }
                                    for (Use use : instr.uses) {
                                        use.value = value;
                                        value.addUse(use);
                                        use.user.setValue(use.pos, value);
                                    }
                                    instrIt.remove();
                                    flag = true;
                                }
                            } else if (instr.getInstrType() == InstrType.SREM) {
                                if (left instanceof ConstInteger && ((ConstInteger) left).getValue() == 0) { /// 0%a=0
                                    value = new ConstInteger(0, instr.getType());
                                    Iterator<Use> useIt = right.uses.iterator();
                                    while (useIt.hasNext()) {
                                        Use use = useIt.next();
                                        if (use.user.equals(instr)) {
                                            useIt.remove();
                                        }
                                    }
                                    for (Use use : instr.uses) {
                                        use.value = value;
                                        value.addUse(use);
                                        use.user.setValue(use.pos, value);
                                    }
                                    instrIt.remove();
                                    flag = true;
                                } else if (right instanceof ConstInteger && ((ConstInteger) right).getValue() == 1) { /// a%1=0
                                    value = new ConstInteger(0, instr.getType());
                                    Iterator<Use> useIt = left.uses.iterator();
                                    while (useIt.hasNext()) {
                                        Use use = useIt.next();
                                        if (use.user.equals(instr)) {
                                            useIt.remove();
                                        }
                                    }
                                    for (Use use : instr.uses) {
                                        use.value = value;
                                        value.addUse(use);
                                        use.user.setValue(use.pos, value);
                                    }
                                    instrIt.remove();
                                    flag = true;
                                } else if (left.equals(right)) { /// a%a=0
                                    value = new ConstInteger(0, instr.getType());
                                    Iterator<Use> useIt = left.uses.iterator();
                                    while (useIt.hasNext()) {
                                        Use use = useIt.next();
                                        if (use.user.equals(instr)) {
                                            useIt.remove();
                                        }
                                    }
                                    for (Use use : instr.uses) {
                                        use.value = value;
                                        value.addUse(use);
                                        use.user.setValue(use.pos, value);
                                    }
                                    instrIt.remove();
                                    flag = true;
                                }
                            }
                        } else if (instr instanceof CmpInst) {
                            Value left = instr.getValue(0);
                            Value right = instr.getValue(1);
                            if (left instanceof ConstInteger && right instanceof ConstInteger) {
                                int result = ((CmpInst) instr).cmpConst((ConstInteger) left, (ConstInteger) right);
                                Value value = new ConstInteger(result, instr.getType());
                                for (Use use : instr.uses) {
                                    use.value = value;
                                    value.addUse(use);
                                    use.user.setValue(use.pos, value);
                                    if (use.user instanceof BranchInst) {
                                        BranchInst branch = (BranchInst) use.user;
                                        branch.isCond = false;
                                        Iterator<Use> useIt = branch.getValue(1).uses.iterator();
                                        while (useIt.hasNext()) {
                                            Use tmpuse = useIt.next();
                                            if (tmpuse.user.equals(branch)) {
                                                useIt.remove();
                                            }
                                        }
                                        useIt = branch.getValue(2).uses.iterator();
                                        while (useIt.hasNext()) {
                                            Use tmpuse = useIt.next();
                                            if (tmpuse.user.equals(branch)) {
                                                useIt.remove();
                                            }
                                        }
                                        if (result == 1) {
                                            branch.setValues(branch.getValue(1));
                                        } else {
                                            branch.setValues(branch.getValue(2));
                                        }
                                    }
                                }
                                instrIt.remove();
                                flag = true;
                            }
                            /// br i1 1, label %1, label %2 可能需要重建 CFG
                        } else if (instr instanceof ConvertInst) {
                            if (instr.getInstrType() == InstrType.ZEXT) { /// 可能导致生成的llvm无法运行
                                Value value = instr.getValue(0);
                                Iterator<Use> useIt = value.uses.iterator();
                                while (useIt.hasNext()) {
                                    Use use = useIt.next();
                                    if (use.user.equals(instr)) {
                                        useIt.remove();
                                    }
                                }
                                for (Use use : instr.uses) {
                                    use.value = value;
                                    value.addUse(use);
                                    use.user.setValue(use.pos, value);
                                }
                                instrIt.remove();
                                flag = true;
                            } else if (instr.getInstrType() == InstrType.TRUNC) {
                                Value value = instr.getValue(0);
                                if (value instanceof ConstInteger) {
                                    int imm = ((ConstInteger) value).getValue() & 0xFF;
                                    value = new ConstInteger(imm, instr.getType());
                                    for (Use use : instr.uses) {
                                        use.value = value;
                                        value.addUse(use);
                                        use.user.setValue(use.pos, value);
                                    }
                                    instrIt.remove();
                                    flag = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void GlobalConstSpread() {
        Iterator<GlobalVariable> globalIt = module.globalVariables.iterator();
        while (globalIt.hasNext()) {
            GlobalVariable global = globalIt.next();
            if (global.isConst && !global.eletype.isArray()) {
                ConstInteger value = global.values.get(0); // todo: ensure not empty
                for (Use use : global.uses) {
                    Instruction loadInst = (Instruction) use.user; //
                    for (Use userUse : loadInst.uses) {
                        userUse.value = value;
                        value.addUse(userUse);
                        userUse.user.setValue(userUse.pos, value);
                    }
                    loadInst.curBlock.instrs.remove(loadInst);
                }
                globalIt.remove();
            } else if (global.isConst && global.eletype.isArray()) {
                Iterator<Use> useIt = global.uses.iterator();
                while (useIt.hasNext()) {
                    Use use = useIt.next();
                    Instruction gep = (Instruction) use.user; // 做完GVN再用一遍！
                    if (gep.getValue(1) instanceof ConstInteger) {
                        for (Use gepUse : gep.uses) {
                            if (gepUse.user instanceof LoadInst) { /// callInst
                                LoadInst loadInst = (LoadInst) gepUse.user;
                                ConstInteger value = global.values.get(((ConstInteger) gep.getValue(1)).getValue());
                                for (Use loadUse : loadInst.uses) {
                                    loadUse.value = value;
                                    value.addUse(loadUse);
                                    loadUse.user.setValue(loadUse.pos, value);
                                }
                                loadInst.curBlock.instrs.remove(loadInst);
                                gep.curBlock.instrs.remove(gep);
                                useIt.remove();
                            }
                        }
                    }
                }
            }
        }
    }
}
