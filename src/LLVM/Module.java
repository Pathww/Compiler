package LLVM;

import java.util.ArrayList;

public class Module {
    public ArrayList<GlobalVariable> globalVariables = new ArrayList<>();
    public ArrayList<Function> functions = new ArrayList<>();

    public void addGlobalVariable(GlobalVariable gv) {
        globalVariables.add(gv);
    }

    public void addFunction(Function f) {
        functions.add(f);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("declare i32 @getint()\n" +
                "declare i32 @getchar()\n" +
                "declare void @putint(i32)\n" +
                "declare void @putch(i32)\n" +
                "declare void @putstr(i8*)\n\n");
        for (GlobalVariable gv : globalVariables) {
            sb.append(gv.toString());
        }
        sb.append("\n");
        for (Function f : functions) {
            sb.append(f.toString());
        }
        return sb.toString();
    }

    public void allocName() {
        for (Function f : functions) {
            f.allocName();
            SlotTracker.reset();
        }
    }

    public void buildMips() {
        for (GlobalVariable gv : globalVariables) {
            gv.buildMips();
        }
        for (int i = functions.size() - 1; i >= 0; i--) {
            functions.get(i).buildMips();
        }
    }
}
