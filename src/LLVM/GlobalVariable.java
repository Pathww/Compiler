package LLVM;

import LLVM.Type.IRType;
import LLVM.Type.PointerType;
import MIPS.MipsBuilder;

import java.util.ArrayList;

public class GlobalVariable extends Value {
    public IRType eletype;
    public ArrayList<ConstInteger> values = null;
    private String string = null;
    public boolean isConst = false;

    /*
    int a
    char a
    int[10] a
    char[10] a
     */
    public GlobalVariable(String name, IRType eletype, ArrayList<ConstInteger> values) {
        super("@" + name, new PointerType(eletype));
        this.eletype = eletype;
        this.values = values;
    }

    public GlobalVariable(String name, IRType eletype, String string) {
        super("@" + name, new PointerType(eletype));
        this.eletype = eletype;
        this.string = string;
    }

    public boolean isString() {
        return string != null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s = global %s ", getName(), eletype));
        if (eletype.isArray()) {
            if (values != null) { // TODO: 部分赋值
                sb.append("[");
                for (int i = 0; i < values.size(); i++) {
                    sb.append(values.get(i));
                    if (i != values.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("]");
            } else if (string != null) {
                sb.append(String.format("c\"%s\"", string));
            } else {
                sb.append("zeroinitializer");
            }
        } else {
            if (values == null) {
                sb.append("0");
            } else {
                sb.append(values.get(0).getName());
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    public void buildMips() {
        if (string != null) {
            MipsBuilder.addGlobalData(getName().substring(2), eletype, string);
        } else {
            MipsBuilder.addGlobalData(getName().substring(1), eletype, values);
        }
    }
}
