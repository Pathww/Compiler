package AST;

import Lexer.*;
import Symbol.*;
import Error.*;

public class FuncDef {
    private FuncType funcType;
    private Token ident;
    private Token lparent;
    private FuncFParams funcFParams = null;
    private Token rparent;
    private Block block;

    public FuncDef(FuncType funcType, Token ident, Token lparent, Token rparent, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.lparent = lparent;
        this.rparent = rparent;
        this.block = block;
    }

    public FuncDef(FuncType funcType, Token ident, Token lparent, FuncFParams funcFParams, Token rparent, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.lparent = lparent;
        this.funcFParams = funcFParams;
        this.rparent = rparent;
        this.block = block;
    }

    public void toSymbol(SymbolTable table) {
        SymbolType type;
        SymbolTable nextTable = new SymbolTable(table);

        if (funcType.getType() == TokenType.VOIDTK) {
            type = SymbolType.VoidFunc;
            nextTable.setVoid();
        } else if (funcType.getType() == TokenType.INTTK) {
            type = SymbolType.IntFunc;
        } else {
            type = SymbolType.CharFunc;
        }

        FuncSymbol funcSymbol = new FuncSymbol(ident.getValue(), type);
        if (!table.addSymbol(funcSymbol)) {
            ErrorHandler.addError(ident.getLine(), ErrorType.b);
        }

        if (funcFParams != null) {
            funcFParams.toSymbol(nextTable);
            funcSymbol.setParams(nextTable.getSymbols());
        }
        block.toSymbol(nextTable);

        if (funcType.getType() != TokenType.VOIDTK) { //
            block.checkLastReturn();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcType.toString());
        sb.append(ident.toString());
        sb.append(lparent.toString());
        if (funcFParams != null) {
            sb.append(funcFParams);
        }
        sb.append(rparent.toString());
        sb.append(block.toString());
        sb.append("<FuncDef>\n");
        return sb.toString();
    }
}
