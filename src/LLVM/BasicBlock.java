package LLVM;

import LLVM.Instr.InstrType;
import LLVM.Instr.Instruction;
import LLVM.Instr.PcopyInst;
import LLVM.Type.IntegerType;
import LLVM.Type.PointerType;
import MIPS.MipsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BasicBlock extends Value {
    public ArrayList<Instruction> allocas = null;
    public ArrayList<Instruction> instrs = new ArrayList<>();
    public Function curFunc = null;

    /// ControlFlowGraph
    public ArrayList<BasicBlock> prevBlocks = new ArrayList<>();
    public ArrayList<BasicBlock> nextBlocks = new ArrayList<>();

    /// DominateSet
    public HashSet<BasicBlock> domers = new HashSet<>();
    public HashSet<BasicBlock> domees = new HashSet<>();

    /// DominatorTree
    public BasicBlock idom = null;
    public HashSet<BasicBlock> idoms = new HashSet<>();

    /// DominanceFrontier
    public HashSet<BasicBlock> frontiers = new HashSet<>();
    public ArrayList<Instruction> vars = null;

    public ArrayList<PcopyInst> pcopys = new ArrayList<>();

    /// Live Analysis
    public HashSet<Value> defVars = new HashSet<>();
    public HashSet<Value> useVars = new HashSet<>();
    public HashSet<Value> liveIn = new HashSet<>();
    public HashSet<Value> liveOut = new HashSet<>();

    /// Loop Analysis
    public int loopDepth = 0;
    public HashSet<BasicBlock> loopBlocks = new HashSet<>();

    public HashMap<Value, Integer> lastUse = new HashMap<>();


    public BasicBlock() {
        super(IntegerType.VOID);
    }

    public void addInstr(Instruction i) {
        instrs.add(i);
        i.curBlock = this;
    }

    public void addInstr(int index, Instruction i) {
        instrs.add(index, i);
        i.curBlock = this;
    }

    public void addAlloca(Instruction i) {
        if (allocas == null) {
            allocas = new ArrayList<>();
        }
        allocas.add(i);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:\n", getName().substring(1)));
        if (allocas != null) {
            for (Instruction i : allocas) {
                sb.append("\t").append(i.toString());
            }
        }
        for (Instruction i : instrs) {
            if (i.getInstrType() != InstrType.VAR) {
                sb.append("\t").append(i);
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    public void allocName() {
        if (allocas != null) {
            for (Instruction i : allocas) {
                i.setName();
            }
        }
        for (Instruction i : instrs) {
            i.setName();
        }
    }

    public void buildMips() {
        MipsBuilder.addBlock(getName().substring(1), lastUse);
        if (allocas != null) {
            for (Instruction i : allocas) {
                i.buildMips();
            }
            MipsBuilder.fixGlobalStack();
        }
        MipsBuilder.pos = 0;
        for (Instruction i : instrs) {
            System.out.println(i);
            MipsBuilder.addComment("# " + i.toString());
            i.buildMips();
            MipsBuilder.pos++;
        }
    }

    public void removeLastReturn() {
        instrs.remove(instrs.size() - 1);
    }

    public void addPrevBlock(BasicBlock block) {
        prevBlocks.add(block);
    }

    public void addNextBlock(BasicBlock block) {
        nextBlocks.add(block);
    }

    public void allocVar() {
        vars = new ArrayList<>();
        if (allocas == null) {
            return;
        }
        for (int i = allocas.size() - 1; i >= 0; i--) {
            Instruction instr = allocas.get(i);
            if (!((PointerType) instr.getType()).getRefType().isArray()) {
                vars.add(instr);
                allocas.remove(i);
            }
        }
    }

    public void addLastInstr(Instruction instr) {
        instr.setBasicBlock(this);
        instrs.add(instrs.size() - 1, instr);
    }

    public void resetDominate() {
        prevBlocks = new ArrayList<>();
        nextBlocks = new ArrayList<>();

        domers = new HashSet<>();
        domees = new HashSet<>();

        idom = null;
        idoms = new HashSet<>();
    }
}
