package MIPS;

import LLVM.ConstInteger;
import LLVM.Type.ArrayType;
import LLVM.Type.IRType;

import java.util.ArrayList;

public class MipsGlobalData extends Operand {
    private IRType type;
    private ArrayList<ConstInteger> values = null;
    private String string = null;

    /*
    int a
    char a
    int[10] a
    char[10] a
     */
    public MipsGlobalData(String name, IRType type, ArrayList<ConstInteger> values) {
        super(name);
        this.type = type;
        this.values = values;
    }

    public MipsGlobalData(String name, IRType type, String string) {
        super(name);
        this.type = type;
        this.string = string;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s: ", getName()));
        if (type.isArray()) {
            if (values != null) {
                sb.append(".word ");
                for (int i = 0; i < values.size(); i++) {
                    sb.append(values.get(i).getName());
                    if (i != values.size() - 1) {
                        sb.append(", ");
                    }
                }
            } else if (string != null) {
                sb.append(String.format(".asciiz \"%s\"", string));
            } else {
                sb.append(String.format(".space %d", 4 * ((ArrayType) type).getLength()));
            }
        } else {
            sb.append(".word ");
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
