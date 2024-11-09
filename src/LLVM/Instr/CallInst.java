package LLVM.Instr;

import LLVM.Function;
import LLVM.Type.IntegerType;
import LLVM.Value;

import java.util.ArrayList;

public class CallInst extends Instruction {
    public CallInst(Function func, ArrayList<Value> values) {
        super(InstrType.CALL, func.getType());
        this.addValue(func, 0);
        for (int i = 0; i < values.size(); i++) {
            this.addValue(values.get(i), i + 1);
        }
        if (func.getType() == IntegerType.VOID) {
            hasName = false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getType() != IntegerType.VOID) {
            sb.append(String.format("%s = ", getName()));
        }
        sb.append(String.format("call %s %s(", values.get(0).getType(), values.get(0).getName()));
        for (int i = 1; i < values.size(); i++) {
            sb.append(String.format("%s %s", values.get(i).getType(), values.get(i).getName()));
            if (i < values.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")\n");
        return sb.toString();
    }
}
