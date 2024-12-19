package LLVM.Instr;

import LLVM.BasicBlock;
import LLVM.Type.IRType;
import LLVM.Value;

import java.util.ArrayList;

public class PhiInst extends Instruction {
    public ArrayList<BasicBlock> blocks = new ArrayList<>();

    public VarInst varInst;

    public PhiInst(IRType irType) {
        super(InstrType.PHI, irType);
    }

    public void addValue(Value value, BasicBlock block) {
        super.addValue(value);
        blocks.add(block);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s = phi %s ", getName(), getType()));
        for (int i = 0; i < values.size(); i++) {
            sb.append(String.format("[ %s, %s ]", values.get(i).getName(), blocks.get(i).getName()));
            if (i < values.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("\n");
        return sb.toString();
    }
}
