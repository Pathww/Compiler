package LLVM;

import LLVM.Type.IRType;

public class ConstInteger extends Value {
    public ConstInteger(int value, IRType type) {
        super(String.valueOf(value), type);
    }

    public String toString() {
        return getType().toString() + " " + getName();
    }
}
