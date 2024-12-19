package Optimizer;

import LLVM.*;
import LLVM.Instr.InstrType;
import LLVM.Instr.Instruction;
import LLVM.Instr.StoreInst;
import LLVM.Module;
import LLVM.Type.PointerType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class Localizer {
    private Module module = IRBuilder.module;
    private HashSet<GlobalVariable> gvs = new HashSet<>();

    public Localizer() {
        for (GlobalVariable gv : module.globalVariables) {
            if (!((PointerType) gv.getType()).getRefType().isArray()) {
                HashSet<Function> funcs = new HashSet<>();
                for (Use use : gv.uses) {
                    funcs.add(((Instruction) use.user).curBlock.curFunc);
                }
                if (funcs.size() == 1) {
                    HashSet<BasicBlock> blocks = new HashSet<>();
                    for (Use use : gv.uses) {
                        blocks.add(((Instruction) use.user).curBlock);
                    }
                    if (blocks.size() == 1) {
                        gvs.add(gv);
                    }
                }
            }
        }
        insertPhi();
        for (GlobalVariable gv : gvs) {
            BasicBlock block = ((Instruction) gv.uses.get(0).user).curBlock.curFunc.blocks.get(0);
            rename(gv, block);
        }
        for (GlobalVariable gv : module.globalVariables) {
            removeUseless(gv);
        }
        for (Function func : module.functions) {
            removeUseless(func);
            for (BasicBlock block : func.blocks) {
                removeUseless(block);
                for (Instruction instr : block.instrs) {
                    removeUseless(instr);
                }
            }
        }
        for (GlobalVariable gv : gvs) {
            module.globalVariables.remove(gv);
        }
    }


    private HashMap<Value, HashSet<Instruction>> useInstrs = new HashMap<>();
    private HashMap<Value, HashSet<Instruction>> defInstrs = new HashMap<>();
    private HashMap<Value, Stack<Value>> curValues = new HashMap<>();

    public void insertPhi() {
        for (GlobalVariable value : gvs) {
            defInstrs.put(value, new HashSet<>());
            useInstrs.put(value, new HashSet<>());
            Stack<Value> stack = new Stack<>();
            if (value.values == null) {
                stack.push(new ConstInteger(0, ((PointerType) value.getType()).getRefType()));
            } else {
                stack.push(value.values.get(0));
            }

            curValues.put(value, stack);

            HashSet<BasicBlock> defBlocks = new HashSet<>();
            for (Use use : value.uses) {
                Instruction user = (Instruction) use.user;
                if (user instanceof StoreInst) {
                    defInstrs.get(value).add(user);
                    defBlocks.add(user.curBlock);
                } else { /// LoadInst
                    useInstrs.get(value).add(user);
                }
            }
        }
    }

    public void rename(Value value, BasicBlock block) {
        int cnt = 0;
        Iterator<Instruction> it = block.instrs.iterator();
        while (it.hasNext()) {
            Instruction instr = it.next();
            if (defInstrs.get(value).contains(instr)) {
                cnt++;
                if (instr.getInstrType() == InstrType.STORE) {
                    curValues.get(value).push(instr.getValue(0));
                    instr.curBlock = null;
                    it.remove();
                }
            } else if (useInstrs.get(value).contains(instr)) {
                for (Use use : instr.uses) {
                    Value curValue = curValues.get(value).peek();
                    use.user.setValue(use.pos, curValue); ///
                    use.value = curValue;
                    curValue.uses.add(use);
                }
                instr.curBlock = null;
                it.remove();
            }
        }

        for (BasicBlock nextBlock : block.idoms) {
            rename(value, nextBlock);
        }

        for (int i = 0; i < cnt; i++) {
            curValues.get(value).pop();
        }
    }


    public void removeUseless(Value value) {
        Iterator<Use> it = value.uses.iterator();
        while (it.hasNext()) {
            Use use = it.next();
            if (((Instruction) use.user).curBlock == null) {
                it.remove();
            }
        }
    }
}
