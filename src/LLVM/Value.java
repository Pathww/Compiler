package LLVM;

import LLVM.Type.IRType;

import java.util.ArrayList;

public class Value {
    private String name = null;
    private IRType type;
    public ArrayList<Use> uses = new ArrayList<>();
    public boolean isParam = false;
    protected boolean hasName = true;

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

    public String getName() {
        return name;
    }

    public void setName() {
        if (this instanceof GlobalVariable || this instanceof Function || this instanceof ConstInteger || !hasName) {
            return;
        }
        this.name = "%" + SlotTracker.alloc();
    }

    public String toString() {
        return getType().toString() + " " + getName();
    }

    public void setType(IRType type) {
        this.type = type;
    }
}
