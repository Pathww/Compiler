package Optimizer;

import LLVM.*;
import LLVM.Instr.Instruction;
import LLVM.Instr.MoveInst;
import LLVM.Module;

import java.util.Iterator;

public class Optimizer {
    private Module module = IRBuilder.module;

    public Optimizer() {
        ConstSpread constSpread = new ConstSpread();
        Mem2Reg mem2Reg = new Mem2Reg();
        FuncInline funcInline = new FuncInline();
        for (Function func : module.functions) {
            for (BasicBlock block : func.blocks) {
                block.resetDominate();
            }
        }
        mem2Reg.rebuild();

        Localizer localizer = new Localizer();
        RemovePhi removePhi = new RemovePhi();
        constSpread = new ConstSpread();
        for (Function func : module.functions) {
            Iterator<Instruction> it = func.blocks.get(0).instrs.iterator();
            while (it.hasNext()) {
                Instruction instr = it.next();
                if (instr instanceof MoveInst && instr.values.get(1) instanceof ConstInteger && ((ConstInteger) instr.values.get(1)).getValue() == 0) {
                    it.remove();
                }
            }
        }
        IRBuilder.module.allocName();

        for (Function func : module.functions) {
            for (BasicBlock block : func.blocks) {
                block.resetDominate();
            }
        }
        mem2Reg.rebuild();

        GVN gvn = new GVN();
        DCE dce = new DCE();
        LiveAnalysis liveAnalysis = new LiveAnalysis();
        LoopAnalysis loopAnalysis = new LoopAnalysis();
    }
}
