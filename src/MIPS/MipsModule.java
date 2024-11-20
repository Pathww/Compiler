package MIPS;

import java.util.ArrayList;

public class MipsModule {
    ArrayList<MipsGlobalData> globalDatas = new ArrayList<>();
    ArrayList<MipsFunction> functions = new ArrayList<>();

    public void addGlobalData(MipsGlobalData gd) {
        globalDatas.add(gd);
    }

    public void addFunction(MipsFunction f) {
        functions.add(f);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!globalDatas.isEmpty()) {
            sb.append(".data\n");
            for (MipsGlobalData gd : globalDatas) {
                sb.append(gd.toString());
            }
            sb.append("\n.text\n");
        }
        for (MipsFunction f : functions) {
            sb.append(f.toString());
        }
        return sb.toString();
    }
}
