package LLVM.Instr;

import LLVM.Type.ArrayType;
import LLVM.Type.IRType;
import LLVM.Type.PointerType;
import LLVM.Value;

public class GetElementPtrInst extends Instruction {
    private IRType refType;

    public GetElementPtrInst(Value pointer, Value index) {
        super(InstrType.GEP, pointer.getType());
        refType = ((PointerType) pointer.getType()).getRefType();
        if (refType.isArray()) {
            setType(new PointerType(((ArrayType) refType).getElmType()));
        }
        this.addValue(pointer, 0);
        this.addValue(index, 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s = getelementptr inbounds %s, %s %s, ", getName(), ((PointerType) getValue(0).getType()).getRefType(),
                getValue(0).getType(), getValue(0).getName()));

        if (refType.isArray()) {
            sb.append("i32 0, ");
        }
        sb.append(String.format("%s %s\n", getValue(1).getType(), getValue(1).getName()));
        return sb.toString();
    }
}
