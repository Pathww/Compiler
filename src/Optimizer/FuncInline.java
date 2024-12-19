package Optimizer;

import LLVM.*;
import LLVM.Instr.*;
import LLVM.Module;
import LLVM.Type.IntegerType;
import LLVM.Type.PointerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class FuncInline {
    private Module module = IRBuilder.module;

    public FuncInline() {
        Iterator<Function> it = module.functions.iterator();
        while (it.hasNext()) {
            Function func = it.next();
            if (isInlinable(func)) {
                boolean flag = false;
                for (Use use : func.uses) {
                    flag = true;
                    Instruction callInst = (Instruction) use.user;
                    BasicBlock curBlock = callInst.curBlock;
                    Function newFunc = deepCopy(func);
                    for (int i = 1; i < callInst.values.size(); i++) {
                        Value arg = callInst.getValue(i);
                        if (arg instanceof GetElementPtrInst) { /// care
                            arg = ((GetElementPtrInst) arg).getPointer();
                        }
                        Value param = newFunc.params.get(i - 1);
                        for (Use paramUse : param.uses) {
                            paramUse.value = arg;
                            arg.addUse(paramUse);
                            paramUse.user.setValue(paramUse.pos, arg);
                        }
                        arg.uses.removeIf(paramUse -> paramUse.user.equals(callInst));
                    }

                    int index = curBlock.instrs.indexOf(callInst);
                    curBlock.instrs.remove(index);
                    if (newFunc.blocks.size() == 1) {
                        BasicBlock block = newFunc.blocks.get(0);
                        for (int i = 0; i < block.instrs.size(); i++) {
                            Instruction instr = block.instrs.get(i);
                            if (instr instanceof ReturnInst) {
                                if (func.getType() != IntegerType.VOID) {
                                    for (Use retUse : callInst.uses) {
                                        retUse.value = instr.getValue(0);
                                        instr.getValue(0).addUse(retUse);
                                        retUse.user.setValue(retUse.pos, instr.getValue(0));
                                    }
                                    instr.getValue(0).uses.removeIf(retUse -> retUse.user.equals(instr));
                                }
                            } else {
                                curBlock.addInstr(index + i, instr);
                            }
                        }
                    } else {
                        BasicBlock block = newFunc.blocks.get(0);
                        for (BasicBlock prevBlock : curBlock.prevBlocks) {
                            block.prevBlocks.add(prevBlock);
                            prevBlock.nextBlocks.remove(curBlock);
                            prevBlock.nextBlocks.add(block);
                        }

                        for (int i = 0; i < index; i++) {
                            Instruction instr = curBlock.instrs.remove(0);
                            block.addInstr(i, instr);
                        }

                        Iterator<Use> brIt = curBlock.uses.iterator();
                        while (brIt.hasNext()) {
                            Use brUse = brIt.next();
                            brUse.value = block;
                            brUse.user.setValue(brUse.pos, block);
                            block.addUse(brUse);
                            brIt.remove();
                        }

                        index = curBlock.curFunc.blocks.indexOf(curBlock);
                        ArrayList<Value> retValues = new ArrayList<>();
                        ArrayList<BasicBlock> retBlocks = new ArrayList<>();

                        for (int i = 0; i < newFunc.blocks.size(); i++) {
                            BasicBlock newBlock = newFunc.blocks.get(i);
                            curBlock.curFunc.addBlock(index + i, newBlock);

                            Instruction instr = newBlock.instrs.get(newBlock.instrs.size() - 1);
                            if (instr instanceof ReturnInst) {
                                if (func.getType() != IntegerType.VOID) {
                                    retValues.add(instr.getValue(0));
                                    instr.getValue(0).uses.removeIf(retUse -> retUse.user.equals(instr));
                                }
                                newBlock.instrs.remove(newBlock.instrs.size() - 1);
                                newBlock.addInstr(newBlock.instrs.size(), new BranchInst(curBlock));
                                retBlocks.add(newBlock);
                                newBlock.nextBlocks.add(curBlock);
                            }
                        }

                        curBlock.prevBlocks = retBlocks;

                        if (func.getType() != IntegerType.VOID) {
                            Value retValue;
                            if (retValues.size() == 1) {
                                retValue = retValues.get(0);
                            } else {
                                PhiInst phi = new PhiInst(func.getType());
                                for (int i = 0; i < retValues.size(); i++) {
                                    phi.addValue(retValues.get(i), retBlocks.get(i));
                                }
                                retValue = phi;
                                curBlock.addInstr(0, phi);
                            }
                            for (Use retUse : callInst.uses) {
                                retUse.value = retValue;
                                retValue.addUse(retUse);
                                retUse.user.setValue(retUse.pos, retValue);
                            }
                        }
                    }

                    if (newFunc.blocks.get(0).allocas != null && !curBlock.curFunc.blocks.get(0).equals(newFunc.blocks.get(0))) {
                        for (Instruction alloca : newFunc.blocks.get(0).allocas) {
                            alloca.setBasicBlock(curBlock.curFunc.blocks.get(0));
                            curBlock.curFunc.addAlloca(alloca);
                        }
                        newFunc.blocks.get(0).allocas = null;
                    }
                    if (curBlock.allocas != null && !curBlock.curFunc.blocks.get(0).equals(curBlock)) {
                        for (Instruction alloca : curBlock.allocas) {
                            alloca.setBasicBlock(curBlock.curFunc.blocks.get(0));
                            curBlock.curFunc.addAlloca(alloca);
                        }
                        curBlock.allocas = null;
                    }
                }
                if (flag) {
                    for (GlobalVariable gv : module.globalVariables) {
                        gv.uses.removeIf(use -> ((Instruction) use.user).curBlock.curFunc.equals(func));
                    }
                    it.remove();
                }
            }
        }
    }

    private boolean isInlinable(Function func) {
        for (BasicBlock block : func.blocks) {
            for (Instruction instr : block.instrs) {
                if (instr instanceof CallInst) {
                    if (!instr.getValue(0).getName().equals("@getint") && !instr.getValue(0).getName().equals("@getchar") &&
                            !instr.getValue(0).getName().equals("@putint") && !instr.getValue(0).getName().equals("@putch") &&
                            !instr.getValue(0).getName().equals("@putstr")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    private HashMap<Value, Value> map;
    private HashSet<BasicBlock> visited;
    private HashSet<PhiInst> phis;

    private Function deepCopy(Function func) {
        map = new HashMap<>();
        visited = new HashSet<>();
        phis = new HashSet<>();
        Function newFunc = new Function("inline", func.getType());
        for (Value value : func.params) {
            Value param = new Value(value.getType());
            param.isParam = true;
            newFunc.params.add(param);
            map.put(value, param);
        }
        for (BasicBlock block : func.blocks) {
            BasicBlock newBlock = new BasicBlock();
            newFunc.addBlock(newBlock);
            map.put(block, newBlock);
        }
        if (func.blocks.get(0).allocas != null) {
            newFunc.blocks.get(0).allocas = new ArrayList<>();
            for (Instruction alloca : func.blocks.get(0).allocas) {
                Instruction newAlloca = deepCopy(alloca);
                newAlloca.setBasicBlock(newFunc.blocks.get(0));
                newFunc.addAlloca(newAlloca);
            }
        }

        visited.add(func.blocks.get(0));
        deepCopy(func.blocks.get(0));

        for (PhiInst phi : phis) {
            PhiInst newPhi = (PhiInst) map.get(phi);
            for (int i = 0; i < phi.values.size(); i++) {
                newPhi.addValue(getValue(phi.getValue(i)), (BasicBlock) getValue(phi.blocks.get(i)));
            }
        }
        return newFunc;
    }

    private void deepCopy(BasicBlock block) {
        BasicBlock newBlock = (BasicBlock) map.get(block);
        for (Instruction instr : block.instrs) {
            Instruction newInstr = deepCopy(instr);
            newBlock.addInstr(newInstr);
        }
        for (BasicBlock prevBlock : block.prevBlocks) {
            newBlock.prevBlocks.add((BasicBlock) map.get(prevBlock));
        }
        for (BasicBlock nextBlock : block.nextBlocks) {
            newBlock.nextBlocks.add((BasicBlock) map.get(nextBlock));
            if (!visited.contains(nextBlock)) {
                visited.add(nextBlock);
                deepCopy(nextBlock);
            }
        }
    }

    private Instruction deepCopy(Instruction instr) {
        Instruction newInstr;
        if (instr instanceof AllocaInst) {
            newInstr = new AllocaInst(((PointerType) instr.getType()).getRefType()); /// care
        } else if (instr instanceof BinaryInst) {
            newInstr = new BinaryInst(instr.getInstrType(), getValue(instr.getValue(0)), getValue(instr.getValue(1)));
        } else if (instr instanceof BranchInst) {
            if (((BranchInst) instr).isCond) {
                newInstr = new BranchInst(getValue(instr.getValue(0)), (BasicBlock) getValue(instr.getValue(1)), (BasicBlock) getValue(instr.getValue(2)));
            } else {
                newInstr = new BranchInst((BasicBlock) getValue(instr.getValue(0)));
            }
        } else if (instr instanceof CallInst) {
            ArrayList<Value> values = new ArrayList<>();
            for (int i = 1; i < instr.values.size(); i++) {
                values.add(getValue(instr.getValue(i)));
            }
            newInstr = new CallInst((Function) getValue(instr.getValue(0)), values);
        } else if (instr instanceof CmpInst) {
            newInstr = new CmpInst(((CmpInst) instr).cond, getValue(instr.getValue(0)), getValue(instr.getValue(1)));
        } else if (instr instanceof ConvertInst) {
            newInstr = new ConvertInst(instr.getInstrType(), getValue(instr.getValue(0)), instr.getType());
        } else if (instr instanceof GetElementPtrInst) {
            newInstr = new GetElementPtrInst(getValue(instr.getValue(0)), getValue(instr.getValue(1)));
        } else if (instr instanceof LoadInst) {
            newInstr = new LoadInst(getValue(instr.getValue(0)));
        } else if (instr instanceof PhiInst) {
            newInstr = new PhiInst(instr.getType());
            phis.add((PhiInst) instr);
        } else if (instr instanceof ReturnInst) {
            if (!instr.values.isEmpty()) {
                newInstr = new ReturnInst(getValue(instr.getValue(0)));
            } else {
                newInstr = new ReturnInst();
            }
        } else if (instr instanceof StoreInst) {
            newInstr = new StoreInst(getValue(instr.getValue(0)), getValue(instr.getValue(1)));
        } else {
            throw new RuntimeException("unknown instruction type");
        }
        map.put(instr, newInstr);
        return newInstr;
    }


    private Value getValue(Value value) {
        if (value instanceof GlobalVariable || value instanceof ConstInteger || value instanceof Function) {
            return value;
        }
        return map.get(value);
    }
}
