package LLVM.Instr;

import LLVM.Type.PointerType;
import LLVM.Value;

public class LoadInst extends Instruction {
    public LoadInst(Value pointer) {
        super(InstrType.LOAD, ((PointerType) pointer.getType()).getRefType());
        this.addValue(pointer, 0);
    }

    @Override
    public String toString() {
        return String.format("%s = load %s, %s %s\n", getName(), getType(), getValue(0).getType(), getValue(0).getName());
    }
}
