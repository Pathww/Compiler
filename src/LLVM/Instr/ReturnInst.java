package LLVM.Instr;

import LLVM.Type.IntegerType;
import LLVM.Value;

public class ReturnInst extends Instruction {
    public ReturnInst() {
        super(InstrType.RET, IntegerType.VOID);
        hasName = false;
    }

    public ReturnInst(Value value) {
        super(InstrType.RET, value.getType());
        this.addValue(value, 0);
        hasName = false;
    }

    @Override
    public String toString() {
        if (getType() == IntegerType.VOID) {
            return "ret void\n";
        } else {
            return String.format("ret %s %s\n", getType().toString(), getValue(0).getName());
        }
    }
}
