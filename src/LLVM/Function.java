package LLVM;

import LLVM.Instr.Instruction;
import LLVM.Type.IRType;
import MIPS.MipsBuilder;

import java.util.ArrayList;

public class Function extends Value {
    private ArrayList<Value> params = new ArrayList<>();
    private ArrayList<BasicBlock> blocks = new ArrayList<>();

    public Function(String name, IRType type) {
        super("@" + name, type);
    }

    public void addParam(Value v) {
        params.add(v);
    }

    public ArrayList<Value> getParams() {
        return params;
    }

    public void addBlock(BasicBlock block) {
        blocks.add(block);
    }

    public void addAlloca(Instruction i) {
        blocks.get(0).addAlloca(i);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("define %s %s(", getType(), getName()));
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).toString());
            if (i < params.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(") {\n");
        for (BasicBlock b : blocks) {
            sb.append(b.toString());
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}\n\n");
        return sb.toString();
    }

    public void allocName() {
        for (Value v : params) {
            v.setName();
        }
        for (BasicBlock b : blocks) {
            b.setName();
            b.allocName();
        }
    }

    public void buildMips() {
        MipsBuilder.addFunction(getName().substring(1));
        MipsBuilder.addParams(params);

        if (getName().equals("@main")) {
            blocks.get(blocks.size() - 1).removeLastReturn();
        }
        for (BasicBlock b : blocks) {
            b.buildMips();
        }
        if (getName().equals("@main")) {
            MipsBuilder.addSyscallInst(10);
        }
    }
}
