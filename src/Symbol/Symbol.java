package Symbol;

import LLVM.Value;

public class Symbol {
    public String ident;
    public SymbolType type;
    private Value value;

    public Symbol(String ident, SymbolType type) {
        this.ident = ident;
        this.type = type;
    }

    public String getIdent() {
        return ident;
    }

    public SymbolType getType() {
        return type;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public boolean isArray() {
        return type == SymbolType.IntArray || type == SymbolType.CharArray || type == SymbolType.ConstIntArray || type == SymbolType.ConstCharArray;
    }

    public boolean isInt() {
        return type == SymbolType.Int || type == SymbolType.ConstInt;
    }

    public boolean isChar() {
        return type == SymbolType.Char || type == SymbolType.ConstChar;
    }

    public boolean isIntArray() {
        return type == SymbolType.IntArray || type == SymbolType.ConstIntArray;
    }

    public boolean isCharArray() {
        return type == SymbolType.CharArray || type == SymbolType.ConstCharArray;
    }

    @Override
    public String toString() {
        return " " + ident + " " + type + "\n";
    }

}