package MIPS;

import MIPS.Instr.BranchInst;
import MIPS.Instr.Instruction;
import MIPS.Instr.LoadInst;
import MIPS.Instr.MipsInstrType;

import java.util.ArrayList;

public class MipsFunction extends Operand {
    public ArrayList<MipsBlock> blocks = new ArrayList<>();
    private ArrayList<Instruction> initInstrs;

    public MipsFunction(String name) {
        super(name);
    }

    public void addBlock(MipsBlock block) {
        blocks.add(block);
    }

    public String toString() {
        for (int i = 0; i < blocks.size() - 1; i++) { /// 消除相邻块的Jump
            int size = blocks.get(i).instrs.size();
            Instruction instr = blocks.get(i).instrs.get(size - 1);
            if (instr.getInstrType() == MipsInstrType.J) {
                if (blocks.get(i + 1).getName().equals(((BranchInst) instr).label.getName())) {
                    blocks.get(i).instrs.remove(size - 1);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:\n", getName()));
        if (initInstrs != null) {
            for (Instruction instr : initInstrs) {
                sb.append("\t").append(instr.toString());
            }
        }
        for (MipsBlock b : blocks) {
            sb.append(b.toString());
        }
        return sb.toString();
    }

    public void addInitInstr(Instruction instr) {
        if (initInstrs == null) {
            initInstrs = new ArrayList<>();
        }
        initInstrs.add(instr);
    }
}
