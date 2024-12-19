package Optimizer;

import LLVM.*;
import LLVM.Module;

public class Optimizer {
    private Module module = IRBuilder.module;

    public Optimizer() {
        ConstSpread constSpread = new ConstSpread();
        Mem2Reg mem2Reg = new Mem2Reg();
//        FuncInline funcInline = new FuncInline();
        for (Function func : module.functions) {
            for (BasicBlock block : func.blocks) {
                block.resetDominate();
            }
        }
        mem2Reg.rebuild();

//        Localizer localizer = new Localizer();
        RemovePhi removePhi = new RemovePhi();
        constSpread = new ConstSpread();

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
