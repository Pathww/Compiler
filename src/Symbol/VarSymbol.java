package Symbol;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    // 变量的值：val，寄存器：reg
    private int dim;
    private int length = 1; // TODO
    private ArrayList<Integer> initVals = null;
//    private String initString = null;


    public VarSymbol(String ident, SymbolType type) {
        super(ident, type);
        if (type == SymbolType.Int || type == SymbolType.Char || type == SymbolType.ConstInt || type == SymbolType.ConstChar) {
            dim = 0;
        } else {
            dim = 1;
        }
    }

    public int getDim() {
        return dim;
    }

    public int getInitVal(int index) {
        if (index >= length) {
            return 0;
        }
        return initVals.get(index);
    }

    public void setInitVals(ArrayList<Integer> init) {
        initVals = init;
    }

    public ArrayList<Integer> getInitVals() {
        return initVals;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

//
//    public String getInitString() {
//        return initString;
//    }
//    public void toConstString() {
//        int len = initString.length();
//        initString = initString.replace("\n", "\\0A");
//        StringBuilder sb = new StringBuilder(initString);
//        for (int i = len; i < length; i++) {
//            sb.append("\\00");
//        }
//        initString = sb.toString();
//    }
//    public void setInitString(String stringConst) {
//        initString = stringConst.replace("\\n", "\n");
//    }
//    public int getInitChar(int index) {
//        if (index >= length) {
//            return 0;
//        }
//        return initString.charAt(index);
//    }
}