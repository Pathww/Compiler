package LLVM;

import LLVM.Type.IRType;

import java.util.ArrayList;

public class User extends Value {
    public ArrayList<Value> values = new ArrayList<>(); //use-def
    private int pos = 0;

    public User(IRType type) {
        super(type);
    }

    public void addValue(Value value) {
        values.add(value);
        value.addUse(new Use(value, pos, this));
        pos++;
    }

    public void setValue(int i, Value value) {
        values.set(i, value);
    }

    public Value getValue(int i) {
        return values.get(i);
    }

    public void setValues(Value left, Value right) {
        values = new ArrayList<>();
        pos = 0;
        addValue(left);
        addValue(right);
    }

    public void setValues(Value value) {
        values = new ArrayList<>();
        pos = 0;
        addValue(value);
    }
}
