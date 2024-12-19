package LLVM.Instr;

import LLVM.Type.ArrayType;
import LLVM.Type.IRType;
import LLVM.Type.PointerType;
import LLVM.Value;

import java.util.ArrayList;

public class GetElementPtrInst extends Instruction {
    private IRType refType;

    public GetElementPtrInst(Value pointer, Value index) {
        super(InstrType.GEP, pointer.getType());
        refType = ((PointerType) pointer.getType()).getRefType();
        if (refType.isArray()) {
            setType(new PointerType(((ArrayType) refType).getElmType()));
        }
        this.addValue(pointer);
        this.addValue(index);
    }


    public Value getPointer() {
        return getValue(0);
    }

    public Value getIndex() {
        return getValue(1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s = getelementptr inbounds %s, %s %s, ", getName(), ((PointerType) getValue(0).getType()).getRefType(),
                getValue(0).getType(), getValue(0).getName()));

        refType = ((PointerType) getPointer().getType()).getRefType();
        if (refType.isArray()) {
            sb.append("i32 0, ");
        }
        sb.append(String.format("%s %s\n", getValue(1).getType(), getValue(1).getName()));
        return sb.toString();
    }

    public Value getDef() {
        return this;
    }

    public ArrayList<Value> getUses() {
        ArrayList<Value> uses = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            if (isLiveVar(values.get(i))) {
                uses.add(values.get(i));
            }
        }
        return uses;
    }
}
