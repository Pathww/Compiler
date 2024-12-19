package MIPS;

import LLVM.Value;
import MIPS.Instr.Instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MipsBlock extends Operand {
    public ArrayList<Instruction> instrs = new ArrayList<>();
    public HashMap<Value, Integer> lastUse;

    public MipsBlock(String name, HashMap<Value, Integer> lastUse) {
        super(name);
        this.lastUse = lastUse;
    }

    public void addInstr(Instruction i) {
        instrs.add(i);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:\n", getName()));
        for (Instruction i : instrs) {
            sb.append("\t").append(i.toString());
        }
        sb.append("\n");
        return sb.toString();
    }
}
