package MIPS;

import LLVM.BasicBlock;
import LLVM.ConstInteger;
import LLVM.Instr.VarInst;
import LLVM.Type.ArrayType;
import LLVM.Type.IRType;
import LLVM.Value;
import MIPS.Instr.*;

import java.util.*;

public class MipsBuilder {
    public static MipsModule module = new MipsModule();
    public static MipsFunction curFunction;
    public static MipsBlock curBlock;

    private static HashMap<Value, Integer> offsets;
    private static HashMap<Value, Integer> allocValues;
    private static HashMap<Integer, Value> allocRegs;

    private static ArrayList<Integer> globals;
    private static HashSet<Integer> globalsUsed;
    private static ArrayList<Integer> temps;
    private static ArrayList<Integer> tempsUsed = new ArrayList<>();

    private static HashSet<Value> globalValues;
    private static HashMap<Value, Integer> globalRefs;

    private static int offset = 0;
    private static int globalOffset = 0;

    /// 保证offset一致性！！！
    private static void initRegs() {
        globals = new ArrayList<>();
        temps = new ArrayList<>();
        for (int i = 16; i <= 23; i++) {
            globals.add(i);
        }

        for (int i = 8; i <= 15; i++) {
            temps.add(i);
        }
        for (int i = 24; i <= 28; i++) {
            temps.add(i);
        }
        temps.add(30);
        temps.add(3);
    }

    public static boolean isGlobal(Value value) {
        if (globalValues.contains(value) && tempsUsed.contains(allocValues.get(value))) {
            return true;
        }
        return false;
    }

    public static void writeBack(Value value) {
        if (offsets.containsKey(value)) {
            addStoreInst(Register.get(allocValues.get(value)), new Immediate(offsets.get(value)), Register.sp);
        } else {
            offset -= 4;
            offsets.put(value, offset);
            addStoreInst(Register.get(allocValues.get(value)), new Immediate(offset), Register.sp);
        }
    }

    public static void allocGlobals(ArrayList<BasicBlock> blocks) {
        globalValues = new HashSet<>();
        globalRefs = new HashMap<>();

        for (BasicBlock block : blocks) {
            globalValues.addAll(block.liveIn); /// 需要out吗？？？
        }
        for (Value value : globalValues) {
            globalRefs.put(value, 0);
        }
        CountRefs(blocks);

        List<Map.Entry<Value, Integer>> list = new ArrayList<>(globalRefs.entrySet());
        list.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        for (Map.Entry<Value, Integer> entry : list) {
            Value value = entry.getKey();
            System.out.println(entry.getKey() + ": " + entry.getValue());
            if (value.isParam) {
                if (allocValues.containsKey(value)) {
                    offset -= 4;
                    offsets.put(value, offset);
                    globalsUsed.add(allocValues.get(value));
                } else {
                    for (int i : globals) { /// s0-s7
                        if (!allocRegs.containsKey(i)) {
                            allocValues.put(value, i);
                            allocRegs.put(i, value);
                            globalsUsed.add(i);
                            LoadInst loadInst = new LoadInst(Register.get(i), new Immediate(offsets.get(value)), Register.sp);
                            curFunction.addInitInstr(loadInst);
                            break;
                        }
                    }
                }
            } else {
                allocGlobal(value, value.getType());
            }
        }
        Iterator<Integer> it = globals.iterator();
        while (it.hasNext()) {
            int i = it.next();
            if (!allocRegs.containsKey(i)) {
                temps.add(i);
                it.remove();
            }
        }
        globalOffset = offset;
    }

    private static void CountRefs(ArrayList<BasicBlock> blocks) {
        for (BasicBlock block : blocks) {
            int loop = (int) Math.pow(5, block.loopDepth);
            for (LLVM.Instr.Instruction instr : block.instrs) {
                if (instr instanceof VarInst) {
                    continue;
                }
                if (globalValues.contains(instr)) {
                    globalRefs.put(instr, globalRefs.get(instr) + loop);
                }
                for (Value value : instr.values) {
                    if (globalValues.contains(value)) {
                        globalRefs.put(value, globalRefs.get(value) + loop);
                    }
                }
            }
        }
    }

    public static Operand addFunction(String name) {
        MipsFunction f = new MipsFunction(name);
        module.addFunction(f);
        curFunction = f;
        offset = 0;
        globalOffset = 0; /// 勿忘！！！
        offsets = new HashMap<>();
        allocValues = new HashMap<>();
        allocRegs = new HashMap<>();
        globalsUsed = new HashSet<>();
        initRegs();
        return f;
    }

    public static Operand addBlock(String name, HashMap<Value, Integer> lastUse) {
        offset = globalOffset;
        clearTemps();
        tempsUsed = new ArrayList<>(); /// 放到这里对吗？？？
        MipsBlock b = new MipsBlock(curFunction.getName() + "_" + name, lastUse);
        curFunction.addBlock(b);
        curBlock = b;
        return b;
    }

    public static Operand addGlobalData(String name, IRType type, ArrayList<ConstInteger> values) {
        MipsGlobalData gd = new MipsGlobalData(name, type, values);
        module.addGlobalData(gd);
        return gd;
    }

    public static Operand addGlobalData(String name, IRType type, String string) {
        /// Only For Printf.
        String res = string.replace("\\0A", "\\n");
        res = res.substring(0, res.length() - 3); /// delete \00
        MipsGlobalData gd = new MipsGlobalData(name, type, res);
        module.addGlobalData(gd);
        return gd;
    }

    public static void addParams(ArrayList<Value> params) {
        ///todo: 没有使用 a0
        for (int i = 0; i < params.size(); i++) {
            if (i < 3) {
                allocValues.put(params.get(i), 5 + i);
                allocRegs.put(5 + i, params.get(i));
            } else {
                offsets.put(params.get(i), 4 * (i - 3)); // 使用a0后需要改
            }
        }
    }

    public static void addBinaryInst(MipsInstrType instrType, Operand result, Operand left, Operand right) {
        BinaryInst binaryInst = new BinaryInst(instrType, result, left, right);
        curBlock.addInstr(binaryInst);
    }

    public static void addBranchInst(MipsInstrType type, Operand rs, Operand rt, Value value) {
        Operand label = new Operand(curFunction.getName() + "_" + value.getName().substring(1));
        BranchInst branchInst = new BranchInst(type, rs, rt, label);
        curBlock.addInstr(branchInst);
    }

    public static void addBranchInst(MipsInstrType type, Value value) {
        Operand label = new Operand(curFunction.getName() + "_" + value.getName().substring(1));
        BranchInst branchInst = new BranchInst(type, label);
        curBlock.addInstr(branchInst);
    }

    public static void addBranchInst(MipsInstrType type, Operand operand) {
        BranchInst branchInst = new BranchInst(type, operand);
        curBlock.addInstr(branchInst);
    }

    public static void addCmpInst(MipsInstrType instrType, Operand result, Operand left, Operand right) {
        CmpInst cmpInst = new CmpInst(instrType, result, left, right);
        curBlock.addInstr(cmpInst);
    }

    public static void addLoadInst(Operand rt, Operand offset, Operand base) {
        LoadInst loadInst = new LoadInst(rt, offset, base);
        curBlock.addInstr(loadInst);
    }

    public static void addLoadInst(MipsInstrType type, Operand rt, Operand base) {
        LoadInst loadInst = new LoadInst(type, rt, base);
        curBlock.addInstr(loadInst);
    }

    public static void addStoreInst(Operand rt, Operand offset, Operand base) {
        StoreInst storeInst = new StoreInst(rt, offset, base);
        curBlock.addInstr(storeInst);
    }

    public static void addMoveInst(Operand rd, Operand rs) {
        MoveInst moveInst = new MoveInst(rd, rs);
        curBlock.addInstr(moveInst);
    }

    public static void addSyscallInst(int no) {
        SyscallInst syscallInst = new SyscallInst(no);
        curBlock.addInstr(syscallInst);
    }


    public static void addComment(String comment) {
        curBlock.addInstr(new Instruction(comment));
    }


    public static void allocGlobal(Value value, IRType type) {
        // 数组不参与全局寄存器分配
        if (type.isArray()) {
            offset -= ((ArrayType) type).getLength() * 4;
        } else {
            offset -= 4;
            for (int i : globals) { /// s0-s7
                if (!allocRegs.containsKey(i)) {
                    allocValues.put(value, i);
                    allocRegs.put(i, value);
//                    globalsUsed.add(i);
                    break;
                }
            }
        }
        offsets.put(value, offset);
    }

    static public int pos = 0;

    public static Register allocTemp(Value value) {
        for (int i : temps) {
            if (!allocRegs.containsKey(i)) {
                allocValues.put(value, i);
                allocRegs.put(i, value);
                tempsUsed.add(i);
                return Register.get(i);
            }
        }

        int reg = tempsUsed.remove(0);
        Value save = allocRegs.get(reg);
        if (!globalValues.contains(save)) {
            if (curBlock.lastUse.containsKey(save) && curBlock.lastUse.get(save) > pos) {
                if (offsets.containsKey(save)) {
                    addStoreInst(Register.get(reg), new Immediate(offsets.get(save)), Register.sp);
                } else {
                    offset -= 4;
                    offsets.put(save, offset);
                    addStoreInst(Register.get(reg), new Immediate(offset), Register.sp);
                }
            }
        }

        allocValues.remove(save);

        allocRegs.put(reg, value);
        allocValues.put(value, reg);
        tempsUsed.add(reg); /// 细心，代码错误要改全！！！
        return Register.get(reg);
    }

    public static void saveGlobals() {
        for (int i : globalsUsed) {
            Value value = allocRegs.get(i);
            addStoreInst(Register.get(i), new Immediate(offsets.get(value)), Register.sp);
        }
    }

    public static void restoreGlobals() {
        for (int i : globalsUsed) {
            Value value = allocRegs.get(i);
            addLoadInst(Register.get(i), new Immediate(offsets.get(value)), Register.sp);
        }
    }

    public static void saveTemps() {
        for (int i : tempsUsed) {
            if (globalValues.contains(allocRegs.get(i))) {
                continue;
            }
            Value save = allocRegs.get(i);
            if (curBlock.lastUse.containsKey(save) && curBlock.lastUse.get(save) > pos) {
                offset -= 4;
                offsets.put(allocRegs.get(i), offset);
                addStoreInst(Register.get(i), new Immediate(offset), Register.sp);
            }
        }
    }

    public static void clearTemps() {
        for (int i : tempsUsed) {
            Value value = allocRegs.remove(i);
            allocValues.remove(value);
        }
        tempsUsed = new ArrayList<>();
    }

    public static boolean hasAlloc(Value value) {
        return allocValues.containsKey(value);
    }

    public static Register getAllocReg(Value value) {
        if (allocValues.containsKey(value)) {
            int reg = allocValues.get(value);
            if (globals.contains(reg)) { /// 不能有a0-a3？？？
                globalsUsed.add(reg);
            }
            if (tempsUsed.remove((Integer) reg)) { /// 勿忘维护！！！
                tempsUsed.add(reg);
            }
            return Register.get(reg);
        } else if (offsets.containsKey(value)) {
            Register reg = allocTemp(value);
            addLoadInst(reg, new Immediate(getOffset(value)), Register.sp);
            return reg;
        } else {
            return allocTemp(value);
        }
    }

    public static int addOffset(int off) {
        offset += off;
        return offset;
    }

    public static int getOffset() {
        return offset;
    }

    public static int getOffset(Value value) {
        return offsets.get(value);
    }

    public static boolean hasOffset(Value value) {
        return offsets.containsKey(value);
    }

    public static void fixGlobalStack() {
        globalOffset = offset;
    }
}
