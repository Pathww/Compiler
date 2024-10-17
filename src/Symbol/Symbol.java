package Symbol;

public class Symbol {
    public String ident;
    public SymbolType type;

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

    @Override
    public String toString() {
        return " " + ident + " " + type + "\n";
    }

}