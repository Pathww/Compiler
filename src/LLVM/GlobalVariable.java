package LLVM;

import LLVM.Type.IRType;
import LLVM.Type.PointerType;

import java.util.ArrayList;

public class GlobalVariable extends Value {
    IRType type;
    ArrayList<ConstInteger> values = null;
    String string = null;

    /*
    int a
    char a
    int[10] a
    char[10] a
     */
    public GlobalVariable(String name, IRType type, ArrayList<ConstInteger> values) {
        super("@" + name, new PointerType(type));
        this.type = type;
        this.values = values;
    }

    public GlobalVariable(String name, IRType type, String string) {
        super("@" + name, new PointerType(type));
        this.type = type;
        this.string = string;
    }

    public String toString() { // TODO: zeroinit...
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s = global %s ", getName(), type));
        if (type.isArray()) {
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
}
