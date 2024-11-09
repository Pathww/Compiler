package LLVM.Instr;

import LLVM.Type.IRType;
import LLVM.Value;

public class ConvertInst extends Instruction {
    public ConvertInst(InstrType type, Value value, IRType irType) {
        super(type, irType);
        this.addValue(value, 0);
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s %s to %s\n", getName(), getInstrType().toString(), getValue(0).getType(), getValue(0).getName(), getType());
    }
}
