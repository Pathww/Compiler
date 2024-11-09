package LLVM.Instr;

import LLVM.Value;

public class BinaryInst extends Instruction {

    public BinaryInst(InstrType instrType, Value left, Value right) {
        super(instrType, left.getType());
        this.addValue(left, 0);
        this.addValue(right, 1);
    }

    @Override
    public String toString() {
        return
                String.format("%s = %s %s %s, %s\n", getName(),
                        getInstrType().toString(),
                        getValue(0).getType(),
                        getValue(0).getName(),
                        getValue(1).getName());

    }
}
