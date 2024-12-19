package LLVM.Instr;

import LLVM.Value;

public class PcopyInst {
    public Value left;
    public Value right;

    public PcopyInst(Value left, Value right) {
        this.left = left;
        this.right = right;
    }
}
