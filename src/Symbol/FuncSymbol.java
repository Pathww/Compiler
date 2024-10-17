package Symbol;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<SymbolType> params = new ArrayList<>();

    public FuncSymbol(String ident, SymbolType type) {
        super(ident, type);
    }

    public void setParams(ArrayList<Symbol> symbols) {
        for (Symbol s : symbols) {
            params.add(s.getType());
        }
    }

    public ArrayList<SymbolType> getParams() {
        return params;
    }

    public ArrayList<Integer> getParaTypes() {
        /*
         *  void -1
         *  int/char 0
         *  int array 1
         *  char array 2
         * */
        ArrayList<Integer> types = new ArrayList<>();
        for (SymbolType s : params) {
            if (s == SymbolType.IntArray) {
                types.add(1);
            } else if (s == SymbolType.CharArray) {
                types.add(2);
            } else {
                types.add(0);
            }
        }
        return types;
    }
}
