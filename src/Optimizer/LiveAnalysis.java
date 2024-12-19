package Optimizer;

import LLVM.*;
import LLVM.Instr.InstrType;
import LLVM.Instr.Instruction;
import LLVM.Module;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;

public class LiveAnalysis {
    private Module module = IRBuilder.module;

    public LiveAnalysis() {
        DefUseChain();
        LiveInOut();
        LastUse();
    }

    public void DefUseChain() {
        for (Function func : module.functions) {
            for (BasicBlock block : func.blocks) {
                for (Instruction instr : block.instrs) {
                    if (instr.getDef() != null && !block.useVars.contains(instr.getDef())) {
                        block.defVars.add(instr.getDef());
                    }
                    for (Value value : instr.getUses()) {
                        if (!block.defVars.contains(value)) {
                            block.useVars.add(value);
                        }
                    }
                }
            }
        }
    }

    public void LiveInOut() {
        for (Function func : module.functions) {
            boolean flag = true;
            while (flag) {
                flag = false;
                for (int i = func.blocks.size() - 1; i >= 0; i--) {
                    BasicBlock curBlock = func.blocks.get(i);
                    HashSet<Value> out = new HashSet<>();
                    for (BasicBlock nextBlock : curBlock.nextBlocks) {
                        out.addAll(nextBlock.liveIn);
                    }
                    HashSet<Value> in = new HashSet<>(out);
                    in.removeAll(curBlock.defVars);
                    in.addAll(curBlock.useVars);
                    if (in.size() != curBlock.liveIn.size()) { /// 不能调换顺序，细心！！！
                        flag = true;
                    }
                    curBlock.liveIn = in;
                    curBlock.liveOut = out;
                }
            }
        }
    }

    public void LastUse() {
        for (Function func : module.functions) {
            for (BasicBlock block : func.blocks) {
                int index = 0;
                for (Instruction instr : block.instrs) {
                    instr.index = index;
                    for (Value value : instr.values) {
                        if (value instanceof GlobalVariable || value instanceof BasicBlock || value instanceof Function) {
                            continue;
                        }
                        block.lastUse.put(value, index);
                    }
                    index++;
                }
            }
        }
    }
}
