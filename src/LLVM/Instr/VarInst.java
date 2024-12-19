package LLVM.Instr;

import LLVM.Type.IRType;

public class VarInst extends Instruction {
    public VarInst(IRType type) {
        super(InstrType.VAR, type);
    }

    @Override
    public String toString() {
        return getName();
    }
}
