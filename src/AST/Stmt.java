package AST;

import Symbol.SymbolTable;

public interface Stmt {
    String toString();

    void toSymbol(SymbolTable table);
}
