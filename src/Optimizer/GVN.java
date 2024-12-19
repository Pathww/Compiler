package Optimizer;

import LLVM.*;
import LLVM.Instr.BinaryInst;
import LLVM.Instr.CmpInst;
import LLVM.Instr.ConvertInst;
import LLVM.Instr.Instruction;
import LLVM.Module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class GVN {
    /// Global Variable Numbering
    private Module module = IRBuilder.module;
    private HashMap<String, Instruction> map;

    public GVN() {
        for (Function func : module.functions) {
            map = new HashMap<>();
            run(func.blocks.get(0));
        }
    }

    public void run(BasicBlock block) {
        HashSet<Instruction> set = new HashSet<>();
        Iterator<Instruction> it = block.instrs.iterator();
        while (it.hasNext()) {
            Instruction instr = it.next(); /// GEP CALL?
            if (instr instanceof BinaryInst || instr instanceof CmpInst || instr instanceof ConvertInst) {
                String key = instr.hash();
                if (key == null) {
                    continue;
                }
                if (map.containsKey(key)) {
                    for (int i = 0; i < instr.values.size(); i++) {
                        instr.values.get(i).uses.removeIf(use -> use.user.equals(instr));
                    }
                    Value value = map.get(key);
                    for (Use use : instr.uses) {
                        use.value = value;
                        value.addUse(use);
                        use.user.setValue(use.pos, value);
                    }
                    it.remove();
                } else {
                    set.add(instr);
                    map.put(key, instr);
                }
            }
        }
        for (BasicBlock nextBlock : block.idoms) {
            run(nextBlock);
        }
        for (Instruction instr : set) {
            map.remove(instr.hash());
        }
    }
}
