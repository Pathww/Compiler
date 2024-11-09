package Symbol;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private static int num = 0;
    private int id;
    private SymbolTable preTable;
    private ArrayList<SymbolTable> tables = new ArrayList<>();

    private ArrayList<Symbol> symbols = new ArrayList<>();

    private HashMap<String, Symbol> idents = new HashMap<>();

    private boolean isVoid = false;

    private boolean isGlobal = true;

    public static int loop = 0;

    public SymbolTable(SymbolTable preTable) {
        num++;
        this.id = num;
        if (preTable != null) {
            preTable.addTable(this);
            this.isVoid = preTable.isVoid();
            this.isGlobal = false;
        }
        this.preTable = preTable;
    }

    public boolean addSymbol(Symbol symbol) {
        if (!idents.containsKey(symbol.getIdent())) {
            symbols.add(symbol);
            idents.put(symbol.getIdent(), symbol);
            return true;
        }
        return false;
    }

    public Symbol getSymbol(String ident) {
        if (idents.containsKey(ident)) {
            return idents.get(ident);
        } else if (preTable != null) {
            return preTable.getSymbol(ident);
        } else {
            return null;
        }
    }

    public void addTable(SymbolTable table) {
        tables.add(table);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Symbol s : symbols) {
            sb.append(id);
            sb.append(s.toString());
        }
        for (SymbolTable t : tables) {
            sb.append(t.toString());
        }
        return sb.toString();
    }

    public boolean isConst(String ident) {
        Symbol s = getSymbol(ident);
        return s != null &&
                (s.getType() == SymbolType.ConstCharArray || s.getType() == SymbolType.ConstIntArray || s.getType() == SymbolType.ConstChar || s.getType() == SymbolType.ConstInt);
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }

    public boolean isVoid() {
        return isVoid;
    }

    public void setVoid() {
        this.isVoid = true;
    }
}