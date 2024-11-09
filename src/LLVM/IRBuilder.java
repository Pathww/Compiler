package LLVM;

import LLVM.Instr.*;
import LLVM.Type.ArrayType;
import LLVM.Type.IRType;
import LLVM.Type.IntegerType;

import java.util.ArrayList;
import java.util.Stack;

public class IRBuilder {
    public static Module module = new Module();
    private static Function curFunction;
    private static BasicBlock curBlock;

    private static Stack<BasicBlock> forBlocks = new Stack<>();
    private static Stack<BasicBlock> lastBlocks = new Stack<>();

    public static Function getint = new Function("getint", IntegerType.I32);
    public static Function getchar = new Function("getchar", IntegerType.I32);
    public static Function putint = new Function("putint", IntegerType.VOID);
    public static Function putch = new Function("putch", IntegerType.VOID);
    public static Function putstr = new Function("putstr", IntegerType.VOID);

    private static boolean hasBr = false;

    public static void addInstr(Instruction instr) {
        if (hasBr) {
            return;
        }
        if (instr instanceof AllocaInst) {
            curFunction.addAlloca(instr);
        } else {
            curBlock.addInstr(instr);
        }
    }

    public static Value addFunction(String name, IRType type) {
        Function f = new Function(name, type);
        module.addFunction(f);
        curFunction = f;
        addBasicBlock(new BasicBlock());
        return f;
    }

    public static void addBasicBlock(BasicBlock b) {
        hasBr = false;
        curFunction.addBlock(b);
        curBlock = b;
    }

    public static Value addGlobalVariable(String name, IRType type, ArrayList<Integer> integers) {
        ArrayList<ConstInteger> values = null;
        if (integers != null) {
            values = new ArrayList<>();
            IRType constType = type;
            int len = 1;
            if (type.isArray()) {
                constType = ((ArrayType) type).getElmType();
                len = ((ArrayType) type).getLength();
            }
            for (Integer i : integers) {
                values.add(new ConstInteger(i, constType));
            }
            for (int i = integers.size(); i < len; i++) {
                values.add(new ConstInteger(0, constType));
            }
        }
        GlobalVariable gv = new GlobalVariable(name, type, values);
        module.addGlobalVariable(gv);
        return gv;
    }

    public static Value addGlobalVariable(String name, IRType type, String string) {
        /// Only For Printf.
        String res = string;
        if (string != null) {
            int len = string.length();
            res = string.replace("\n", "\\0A");
            res += "\\00";
        }
        GlobalVariable gv = new GlobalVariable(name, type, res);
        module.addGlobalVariable(gv);
        return gv;
    }

    public static Value addAllocaInst(IRType type) {
        AllocaInst allocaInst = new AllocaInst(type);
        addInstr(allocaInst);
        return allocaInst;
    }

    public static Value addBinaryInst(InstrType instrType, Value left, Value right) {
        BinaryInst binaryInst = new BinaryInst(instrType, left, right);
        addInstr(binaryInst);
        return binaryInst;
    }

    public static Value addBranchInst(Value cond, BasicBlock iftrue, BasicBlock iffalse) {
        BranchInst branchInst = new BranchInst(cond, iftrue, iffalse);
        addInstr(branchInst);
        hasBr = true;
        return branchInst;
    }

    public static Value addBranchInst(BasicBlock dest) {
        BranchInst branchInst = new BranchInst(dest);
        addInstr(branchInst);
        hasBr = true;
        return branchInst;
    }

    public static Value addCallInst(Function func, ArrayList<Value> values) {
        CallInst callInst = new CallInst(func, values);
        addInstr(callInst);
        return callInst;
    }

    public static Value addCmpInst(InstrType cond, Value left, Value right) {
        CmpInst cmpInst = new CmpInst(cond, left, right);
        addInstr(cmpInst);
        return cmpInst;
    }

    public static Value addConvertInst(InstrType type, Value value, IRType irType) {
        ConvertInst convertInst = new ConvertInst(type, value, irType);
        addInstr(convertInst);
        return convertInst;

    }

    public static Value addGetElementPtrInst(Value pointer, Value index) {
        GetElementPtrInst getElementPtrInst = new GetElementPtrInst(pointer, index);
        addInstr(getElementPtrInst);
        return getElementPtrInst;
    }

    public static Value addLoadInst(Value pointer) {
        LoadInst loadInst = new LoadInst(pointer);
        addInstr(loadInst);
        return loadInst;
    }

    public static Value addReturnInst() {
        ReturnInst returnInst = new ReturnInst();
        addInstr(returnInst);
        hasBr = true; //todo???
        return returnInst;
    }

    public static Value addReturnInst(Value value) {
        ReturnInst returnInst = new ReturnInst(value);
        addInstr(returnInst);
        hasBr = true;
        return returnInst;
    }

    public static Value addStoreInst(Value value, Value pointer) {
        StoreInst storeInst = new StoreInst(value, pointer);
        addInstr(storeInst);
        return storeInst;
    }

    public static Value addParam(IRType type) {
        Value param = new Value(type);
        curFunction.addParam(param);
        return param;
    }

    public static void enterLoop(BasicBlock forBlock, BasicBlock lastBlock) {
        forBlocks.push(forBlock);
        lastBlocks.push(lastBlock);
    }

    public static void leaveLoop() {
        forBlocks.pop();
        lastBlocks.pop();
    }

    public static BasicBlock getForBlock() {
        return forBlocks.peek();
    }

    public static BasicBlock getLastBlock() {
        return lastBlocks.peek();
    }

    public static Function getCurFunction() {
        return curFunction;
    }
}
