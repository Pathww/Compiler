package LLVM.Instr;

import LLVM.Type.IRType;
import LLVM.Type.PointerType;
import MIPS.MipsBlock;
import MIPS.MipsBuilder;

public class AllocaInst extends Instruction {
    private IRType type;

    public AllocaInst(IRType type) {
        super(InstrType.ALLOCA, new PointerType(type));
        this.type = type;
    }

    @Override
    public String toString() {
        return getName() + " = alloca " + type.toString() + "\n";
    }

    public void buildMips() {
        MipsBuilder.allocGlobal(this, type);
    }
}
