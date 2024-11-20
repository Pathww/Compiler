package MIPS;

import MIPS.Instr.Instruction;

import java.util.ArrayList;

public class MipsBlock extends Operand {
    public ArrayList<Instruction> instrs = new ArrayList<>();

    public MipsBlock(String name) {
        super(name);
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
