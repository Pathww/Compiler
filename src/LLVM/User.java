package LLVM;

import LLVM.Type.IRType;

import java.util.ArrayList;

public class User extends Value {
    protected ArrayList<Value> values = new ArrayList<>(); //use-def

    public User(IRType type) {
        super(type);
    }

    public void addValue(Value value, int pos) {
        values.add(value);
        value.addUse(new Use(value, pos, this));
    }

    public Value getValue(int i) {
        return values.get(i);
    }
}
