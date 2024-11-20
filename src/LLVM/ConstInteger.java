package LLVM;

import LLVM.Type.IRType;

public class ConstInteger extends Value {
    private int value;

    public ConstInteger(int value, IRType type) {
        super(String.valueOf(value), type);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return getType().toString() + " " + getName();
    }
}
