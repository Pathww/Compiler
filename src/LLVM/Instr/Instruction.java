package LLVM.Instr;

import LLVM.*;
import LLVM.Type.IRType;

import java.util.ArrayList;

public class Instruction extends User {

    private InstrType type;
    public BasicBlock curBlock;
    public int index;

    public Instruction(InstrType instrType, IRType irType) {
        super(irType);
        this.type = instrType;
    }

    public void buildMips() {
    }

    public InstrType getInstrType() {
        return type;
    }

    public void setBasicBlock(BasicBlock basicBlock) {
        this.curBlock = basicBlock;
    }

    public Value getDef() {
        return null;
    }

    public ArrayList<Value> getUses() {
        return new ArrayList<>();
    }

    public boolean isLiveVar(Value value) {
        if (value instanceof ConstInteger || value instanceof GlobalVariable || value instanceof AllocaInst) {
            return false;
        }
        return true;
    }

    public void setInstrType(InstrType type) {
        this.type = type;
    }

    public String hash() {
        return null;
    }
}
