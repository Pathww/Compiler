package Optimizer;

import LLVM.*;
import LLVM.Instr.*;
import LLVM.Module;
import LLVM.Type.PointerType;

import java.util.*;

public class Mem2Reg {
    private Module module = IRBuilder.module;

    public Mem2Reg() {
        removeUnreachable();
        for (GlobalVariable gv : module.globalVariables) {
            removeUseless(gv);
        }
        for (Function func : module.functions) {
            removeUseless(func);
            if (func.blocks.get(0).allocas != null) {
                for (Instruction alloc : func.blocks.get(0).allocas) {
                    removeUseless(alloc);
                }
            }
            for (BasicBlock block : func.blocks) {
                removeUseless(block);
                for (Instruction instr : block.instrs) {
                    removeUseless(instr);
                }
            }
        }
        ControlFlowGraph();
        DominateSet();
        DominatorTree();
        DominanceFrontier();
        insertPhi();
        for (Function func : module.functions) {
            for (Instruction instr : func.blocks.get(0).vars) {
                rename(instr, func.blocks.get(0));
            }
        }
        for (GlobalVariable gv : module.globalVariables) {
            removeUseless(gv);
        }
        for (Function func : module.functions) {
            removeUseless(func);
            for (Instruction alloc : func.blocks.get(0).vars) {
                removeUseless(alloc);
            }
            for (BasicBlock block : func.blocks) {
                removeUseless(block);
                for (Instruction instr : block.instrs) {
                    removeUseless(instr);
                }
            }
        }
    }

    public void rebuild() {
        removeUnreachable();
        for (GlobalVariable gv : module.globalVariables) {
            removeUseless(gv);
        }
        for (Function func : module.functions) {
            removeUseless(func);
            if (func.blocks.get(0).allocas != null) {
                for (Instruction alloc : func.blocks.get(0).allocas) {
                    removeUseless(alloc);
                }
            }
            for (BasicBlock block : func.blocks) {
                removeUseless(block);
                for (Instruction instr : block.instrs) {
                    removeUseless(instr);
                }
            }
        }
        ControlFlowGraph();
        DominateSet();
        DominatorTree();
    }

    public void ControlFlowGraph() {
        for (Function func : module.functions) {
            for (BasicBlock block : func.blocks) {
                for (Use use : block.uses) {
                    Instruction user = (Instruction) use.user;
                    user.curBlock.addNextBlock(block);
                    block.addPrevBlock(user.curBlock);
                }
            }
        }
    }

    public void DominateSet() {
        for (Function func : module.functions) {
            for (BasicBlock block : func.blocks) {
                HashSet<BasicBlock> visited = new HashSet<>();
                Queue<BasicBlock> queue = new ArrayDeque<>();
                if (!block.equals(func.blocks.get(0))) {
                    queue.add(func.blocks.get(0));
                    visited.add(func.blocks.get(0));
                }
                while (!queue.isEmpty()) {
                    BasicBlock curBlock = queue.poll();
                    for (BasicBlock nextBlock : curBlock.nextBlocks) {
                        if (!visited.contains(nextBlock) && !nextBlock.equals(block)) {
                            visited.add(nextBlock);
                            queue.add(nextBlock);
                        }
                    }
                }

                for (BasicBlock basicBlock : func.blocks) {
                    if (!visited.contains(basicBlock)) {
                        block.domees.add(basicBlock);
                        basicBlock.domers.add(block);
                    }
                }
            }
        }
    }

    public boolean isImmediateDominator(BasicBlock prev, BasicBlock next) {
        if (prev.equals(next)) {
            return false;
        }
        for (BasicBlock block : prev.domees) {
            if (!block.equals(prev) && !block.equals(next) && block.domees.contains(next)) {
                return false;
            }
        }
        return true;
    }

    public void DominatorTree() {
        for (Function func : module.functions) {
            for (BasicBlock block : func.blocks) {
                for (BasicBlock domer : block.domers) {
                    if (isImmediateDominator(domer, block)) {
                        block.idom = domer;
                        domer.idoms.add(block);
                        break;
                    }
                }
            }
        }
    }

    public void DominanceFrontier() {
        for (Function func : module.functions) {
            for (BasicBlock b : func.blocks) {
                for (BasicBlock a : b.prevBlocks) {
                    BasicBlock x = a;
                    while (x != null && (!x.domees.contains(b) || x.equals(b))) {
                        x.frontiers.add(b);
                        x = x.idom;
                    }
                }
            }
        }
    }

    private HashMap<Instruction, HashSet<Instruction>> useInstrs = new HashMap<>();
    private HashMap<Instruction, HashSet<Instruction>> defInstrs = new HashMap<>();
    private HashMap<Instruction, Stack<Value>> curValues = new HashMap<>();

    public void insertPhi() {
        for (Function func : module.functions) {
            func.blocks.get(0).allocVar();
            for (Instruction instr : func.blocks.get(0).vars) {
                defInstrs.put(instr, new HashSet<>());
                useInstrs.put(instr, new HashSet<>());
                Stack<Value> stack = new Stack<>();
                stack.push(new ConstInteger(0, ((PointerType) instr.getType()).getRefType()));
                curValues.put(instr, stack);

                HashSet<BasicBlock> defBlocks = new HashSet<>();
                for (Use use : instr.uses) {
                    Instruction user = (Instruction) use.user;
                    if (user instanceof StoreInst) {
                        defInstrs.get(instr).add(user);
                        defBlocks.add(user.curBlock);
                    } else { /// LoadInst
                        useInstrs.get(instr).add(user);
                    }
                }

                HashSet<BasicBlock> F = new HashSet<>();
                Stack<BasicBlock> W = new Stack<>();
                for (BasicBlock block : defBlocks) {
                    W.push(block);
                }
                while (!W.isEmpty()) {
                    BasicBlock X = W.pop();
                    for (BasicBlock Y : X.frontiers) {
                        if (!F.contains(Y)) {
                            Instruction phi = IRBuilder.addPhiInst(Y, ((PointerType) instr.getType()).getRefType());
                            useInstrs.get(instr).add(phi);
                            defInstrs.get(instr).add(phi);
                            F.add(Y);
                            if (!defBlocks.contains(Y)) {
                                W.push(Y);
                            }
                        }
                    }
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
                } else if (instr.getInstrType() == InstrType.PHI) {
                    curValues.get(value).push(instr);
                }
            } else if (useInstrs.get(value).contains(instr) && instr.getInstrType() != InstrType.PHI) {
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

        for (BasicBlock nextBlock : block.nextBlocks) {
            for (Instruction phi : nextBlock.instrs) {
                if (phi.getInstrType() == InstrType.PHI) {
                    if (defInstrs.get(value).contains(phi)) {
                        ((PhiInst) phi).addValue(curValues.get(value).peek(), block);
                    }
                } else {
                    break;
                }
            }
        }

        for (BasicBlock nextBlock : block.idoms) {
            rename(value, nextBlock);
        }

        for (int i = 0; i < cnt; i++) {
            curValues.get(value).pop();
        }
    }

    public void removeUnreachable() {
        for (Function func : module.functions) {
            HashSet<BasicBlock> visited = new HashSet<>();
            Queue<BasicBlock> queue = new ArrayDeque<>();
            queue.add(func.blocks.get(0));
            visited.add(func.blocks.get(0));
            while (!queue.isEmpty()) {
                BasicBlock block = queue.poll();
                Instruction instr = block.instrs.get(block.instrs.size() - 1);
                if (instr.getInstrType() == InstrType.BR) {
                    if (((BranchInst) instr).isCond) {
                        BasicBlock trueBlock = (BasicBlock) instr.getValue(1);
                        BasicBlock falseBlock = (BasicBlock) instr.getValue(2);
                        if (!visited.contains(trueBlock)) {
                            visited.add(trueBlock);
                            queue.add(trueBlock);
                        }
                        if (!visited.contains(falseBlock)) {
                            visited.add(falseBlock);
                            queue.add(falseBlock);
                        }
                    } else {
                        BasicBlock nextBlock = (BasicBlock) instr.getValue(0);
                        if (!visited.contains(nextBlock)) {
                            visited.add(nextBlock);
                            queue.add(nextBlock);
                        }
                    }
                }
            }
            Iterator<BasicBlock> it = func.blocks.iterator();
            while (it.hasNext()) {
                BasicBlock block = it.next();
                if (!visited.contains(block)) {
                    for (Instruction instr : block.instrs) {
                        instr.curBlock = null;
                    }
                    it.remove();
                }
            }
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
