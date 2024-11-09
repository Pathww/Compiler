package LLVM;

import LLVM.Instr.Instruction;
import LLVM.Type.IntegerType;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private ArrayList<Instruction> instrs = new ArrayList<>();
    private ArrayList<Instruction> allocas = null;

    public BasicBlock() {
        super(IntegerType.VOID);
    }

    public void addInstr(Instruction i) {
        instrs.add(i);
    }

    public void addAlloca(Instruction i) {
        if (allocas == null) {
            allocas = new ArrayList<>();
        }
        allocas.add(i);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:\n", getName().substring(1)));
        if (allocas != null) {
            for (Instruction i : allocas) {
                sb.append("\t").append(i.toString());
            }
        }
        for (Instruction i : instrs) {
            sb.append("\t").append(i.toString());
        }
        sb.append("\n");
        return sb.toString();
    }

    public void allocName() {
        if (allocas != null) {
            for (Instruction i : allocas) {
                i.setName();
            }
        }
        for (Instruction i : instrs) {
            i.setName();
        }
    }
}
