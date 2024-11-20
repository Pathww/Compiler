package LLVM.Instr;

import LLVM.BasicBlock;
import LLVM.Type.IRType;
import LLVM.User;

public class Instruction extends User {

    private InstrType type;
    private BasicBlock basicBlock;

    public Instruction(InstrType instrType, IRType irType) {
        super(irType);
        this.type = instrType;
    }

    public void buildMips() {
    }

    public InstrType getInstrType() {
        return type;
    }
}
