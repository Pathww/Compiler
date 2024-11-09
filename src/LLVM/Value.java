package LLVM;

import LLVM.Type.IRType;

import java.util.ArrayList;

public class Value {
    private String name = null;
    private IRType type;
    private ArrayList<Use> uses = new ArrayList<>();
    //def-use

    //    public boolean needName = true;
    public Value(String name, IRType type) {
        this.name = name;
        this.type = type;
    }

    public Value(IRType type) {
        this.type = type;
    }

    public IRType getType() {
        return type;
    }

    public void addUse(Use use) {
        uses.add(use);
    }

    protected boolean hasName = true;

    public String getName() {
//        if (name == null) {
//            this.name = "%" + SlotTracker.alloc();
//        }
        return name;
    }

    public void setName() {
        if (name == null && hasName) {
            this.name = "%" + SlotTracker.alloc();
        }
    }

    public String toString() {
        return getType().toString() + " " + getName();
    }

    public void setType(IRType type) {
        this.type = type;
    }
}
