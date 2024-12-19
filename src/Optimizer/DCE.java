package Optimizer;

import LLVM.*;
import LLVM.Instr.*;
import LLVM.Module;

import java.util.*;

public class DCE {
    /// Dead Code Elimination
    private Module module = IRBuilder.module;

    public DCE() {
        for (Function func : module.functions) {
            HashSet<Value> visited = new HashSet<>();
            Queue<Value> queue = new ArrayDeque<>();
            for (BasicBlock block : func.blocks) {
                for (Instruction instr : block.instrs) {
                    if (instr instanceof CallInst || instr instanceof ReturnInst || instr instanceof BranchInst ||
                            isStoreGlobal(instr)) {
                        queue.add(instr);
                        visited.add(instr);
                    }
                }
            }

            while (!queue.isEmpty()) {
                Instruction instr = (Instruction) queue.poll();
                if (instr instanceof VarInst) {
                    for (Use use : instr.uses) {
                        Value user = use.user;
                        if (user instanceof MoveInst) {
                            if (!visited.contains(user)) {
                                visited.add(user);
                                queue.add(user);
                            }
                        }
                    }
                } else if (instr instanceof AllocaInst) {
                    for (Use use : instr.uses) {
                        Value gep = use.user;
                        for (Use gepUse : gep.uses) {
                            Value store = gepUse.user;
                            if (store instanceof StoreInst) {
                                if (!visited.contains(store)) {
                                    visited.add(store);
                                    queue.add(store);
                                }
                            }
                        }
                    }
                } else {
                    for (Value value : instr.values) {
                        if (value.isParam || value instanceof ConstInteger || value instanceof GlobalVariable || value instanceof BasicBlock || value instanceof Function) {
                            continue;
                        }
                        if (!visited.contains(value)) {
                            visited.add(value);
                            queue.add(value);
                        }
                    }
                }
            }

            for (BasicBlock block : func.blocks) {
                Iterator<Instruction> it = block.instrs.iterator();
                while (it.hasNext()) {
                    Instruction instr = it.next();
                    if (!visited.contains(instr)) {
                        for (Value value : instr.values) {
                            Iterator<Use> useIt = value.uses.iterator();
                            while (useIt.hasNext()) {
                                Use use = useIt.next();
                                if (use.user.equals(instr)) {
                                    useIt.remove();
                                }
                            }
                        }
                        it.remove();
                    }
                }
            }

            if (func.blocks.get(0).allocas != null) {
                Iterator<Instruction> it = func.blocks.get(0).allocas.iterator();
                while (it.hasNext()) {
                    Instruction alloca = it.next();
                    if (!visited.contains(alloca)) {
                        it.remove();
                    }
                }
            }
        }
    }

    public boolean isStoreGlobal(Instruction instr) {
        if (instr instanceof StoreInst) {
            if (instr.getValue(1) instanceof GlobalVariable) {
                return true;
            } else if (instr.getValue(1) instanceof GetElementPtrInst) {
                Value pointer = ((GetElementPtrInst) instr.getValue(1)).getPointer();
                if (pointer.isParam || pointer instanceof GlobalVariable) {
                    return true;
                }
            }
        }
        return false;
    }
}
