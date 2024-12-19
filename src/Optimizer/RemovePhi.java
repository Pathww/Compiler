package Optimizer;

import LLVM.*;
import LLVM.Instr.*;
import LLVM.Module;

import java.util.ArrayList;
import java.util.Iterator;

public class RemovePhi {
    private Module module = IRBuilder.module;

    public RemovePhi() {
        SplitCriticalEdge();
        Pcopy2Move();
    }


    public void modifyValue(Value usee, int pos, User user) {
        Iterator<Use> it = user.getValue(pos).uses.iterator();
        while (it.hasNext()) {
            Use use = it.next();
            if (use.user.equals(user)) {
                it.remove();
                break;
            }
        }
        user.setValue(pos, usee);
        usee.addUse(new Use(usee, pos, user));
    }

    public void SplitCriticalEdge() {
        for (Function func : module.functions) {
            ArrayList<BasicBlock> blocks = new ArrayList<>(func.blocks);
            for (BasicBlock block : blocks) {
                ArrayList<BasicBlock> prevBlocks = new ArrayList<>(block.prevBlocks);
                for (BasicBlock prevBlock : prevBlocks) {
                    if (prevBlock.nextBlocks.size() > 1 && block.instrs.get(0).getInstrType() == InstrType.PHI) {
                        BasicBlock basicBlock = new BasicBlock();
                        basicBlock.prevBlocks.add(prevBlock);
                        basicBlock.nextBlocks.add(block);
                        func.addBlock(func.blocks.size() - 1, basicBlock);

                        BranchInst tmp = new BranchInst(block);
                        tmp.setBasicBlock(basicBlock);
                        basicBlock.instrs.add(tmp);

                        prevBlock.nextBlocks.remove(block);
                        prevBlock.nextBlocks.add(basicBlock);
                        block.prevBlocks.remove(prevBlock);
                        block.prevBlocks.add(basicBlock);

                        BranchInst br = (BranchInst) prevBlock.instrs.get(prevBlock.instrs.size() - 1);
                        if (br.isCond) {
                            if (br.getValue(1).equals(block)) {
                                modifyValue(basicBlock, 1, br);
                            } else {
                                modifyValue(basicBlock, 2, br);
                            }
                        } else {
                            modifyValue(basicBlock, 0, br);
                        }

                        for (int i = 0; i < block.instrs.size(); i++) {
                            Instruction phi = block.instrs.get(i);
                            if (phi.getInstrType() == InstrType.PHI) {
                                for (int j = 0; j < ((PhiInst) phi).blocks.size(); j++) {
                                    if (((PhiInst) phi).blocks.get(j).equals(prevBlock)) {
                                        ((PhiInst) phi).blocks.set(j, basicBlock);
                                        break;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }

                for (int i = 0; i < block.instrs.size(); i++) {
                    Instruction phi = block.instrs.get(i);
                    if (phi.getInstrType() == InstrType.PHI) {
                        VarInst inst = new VarInst(phi.getType());
                        inst.setBasicBlock(block);
                        ((PhiInst) phi).varInst = inst;

                        for (Use use : phi.uses) {
                            use.value = inst;
                            use.user.setValue(use.pos, inst);
                            inst.addUse(use);
                        }
                    } else {
                        break;
                    }
                }
            }

            for (BasicBlock block : blocks) {
                for (int i = 0; i < block.instrs.size(); i++) {
                    Instruction phi = block.instrs.get(i);
                    if (phi.getInstrType() == InstrType.PHI) {
                        VarInst inst = ((PhiInst) phi).varInst;
                        for (int j = 0; j < phi.values.size(); j++) {
                            Value value = phi.getValue(j);
                            if (!inst.equals(value)) { /// ensure not exists a=b
                                BasicBlock prevBlock = ((PhiInst) phi).blocks.get(j);
                                prevBlock.pcopys.add(new PcopyInst(inst, value));
                            }
                            Iterator<Use> it = value.uses.iterator();
                            while (it.hasNext()) {
                                Use use = it.next();
                                if (use.user.equals(phi)) {
                                    it.remove();
                                }
                            }
                        }
                        block.instrs.set(i, inst);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public void Pcopy2Move() {
        for (Function func : module.functions) {
            for (BasicBlock block : func.blocks) {
                while (!block.pcopys.isEmpty()) { /// ensure not exists a=b
                    PcopyInst pcopy = getIndependentInstr(block.pcopys);
                    if (pcopy != null) {
                        MoveInst move = new MoveInst(pcopy.left, pcopy.right);
                        block.addLastInstr(move);
                        block.pcopys.remove(pcopy);
                    } else {
                        pcopy = block.pcopys.get(0);
                        VarInst inst = new VarInst(pcopy.right.getType());
                        block.addLastInstr(inst);

                        MoveInst move = new MoveInst(inst, pcopy.right);
                        block.addLastInstr(move);
                        pcopy.right = inst;
                    }
                }
            }
        }
    }

    private PcopyInst getIndependentInstr(ArrayList<PcopyInst> pcopys) {
        for (int i = 0; i < pcopys.size(); i++) {
            PcopyInst pcopy = pcopys.get(i);
            boolean flag = true;
            for (int j = 0; j < pcopys.size(); j++) {
                if (i != j && pcopy.left.equals(pcopys.get(j).right)) { /// be careful !!!
                    flag = false;
                    break;
                }
            }
            if (flag) {
                return pcopy;
            }
        }
        return null;
    }
}
