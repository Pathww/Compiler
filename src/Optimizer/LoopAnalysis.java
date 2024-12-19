package Optimizer;

import LLVM.*;
import LLVM.Module;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;

public class LoopAnalysis {
    private Module module = IRBuilder.module;

    public LoopAnalysis() {
        for (Function func : module.functions) {
            HashSet<BasicBlock> loopHeaders = new HashSet<>();
            for (BasicBlock block : func.blocks) {
                for (BasicBlock nextBlock : block.nextBlocks) {
                    if (nextBlock.domees.contains(block)) {
                        loopHeaders.add(nextBlock);
                        HashSet<BasicBlock> forwards = forward(nextBlock);
                        HashSet<BasicBlock> backwards = backward(block);
                        forwards.retainAll(backwards);
                        nextBlock.loopBlocks.addAll(forwards);
                    }
                }
            }
            for (BasicBlock block : loopHeaders) {
                for (BasicBlock loopBlock : block.loopBlocks) {
                    loopBlock.loopDepth++;
                }
            }
        }
    }


    private HashSet<BasicBlock> forward(BasicBlock block) {
        HashSet<BasicBlock> visited = new HashSet<>();
        Queue<BasicBlock> queue = new ArrayDeque<>();
        visited.add(block);
        queue.add(block);
        while (!queue.isEmpty()) {
            BasicBlock curBlock = queue.poll();
            for (BasicBlock nextBlock : curBlock.nextBlocks) {
                if (!visited.contains(nextBlock)) {
                    visited.add(nextBlock);
                    queue.add(nextBlock);
                }
            }
        }
        return visited;
    }

    private HashSet<BasicBlock> backward(BasicBlock block) {
        HashSet<BasicBlock> visited = new HashSet<>();
        Queue<BasicBlock> queue = new ArrayDeque<>();
        visited.add(block);
        queue.add(block);
        while (!queue.isEmpty()) {
            BasicBlock curBlock = queue.poll();
            for (BasicBlock prevBlock : curBlock.prevBlocks) {
                if (!visited.contains(prevBlock)) {
                    visited.add(prevBlock);
                    queue.add(prevBlock);
                }
            }
        }
        return visited;
    }
}
