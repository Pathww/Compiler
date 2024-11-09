package LLVM.Instr;

import LLVM.Type.IntegerType;
import LLVM.Value;

public class StoreInst extends Instruction {
    public StoreInst(Value value, Value pointer) {
        super(InstrType.STORE, IntegerType.VOID);
        this.addValue(value, 0);
        this.addValue(pointer, 1);
        hasName = false;
    }

    @Override
    public String toString() {
        return String.format("store %s %s, %s %s\n", getValue(0).getType(), getValue(0).getName(), getValue(1).getType(), getValue(1).getName());
    }
}
