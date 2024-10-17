package AST;

import Symbol.SymbolTable;

public class StmtBlock implements Stmt {
    private Block block;

    public StmtBlock(Block block) {
        this.block = block;
    }

    public void toSymbol(SymbolTable table) {
        SymbolTable nextTable = new SymbolTable(table);
        block.toSymbol(nextTable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(block);
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
