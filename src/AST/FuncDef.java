package AST;

import LLVM.IRBuilder;
import LLVM.Type.IRType;
import LLVM.Type.IntegerType;
import LLVM.Value;
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
    private FuncSymbol funcSymbol;

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

        funcSymbol = new FuncSymbol(ident.getValue(), type);
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

    public void buildIR() {
        IRType retType;
        if (funcType.getType() == TokenType.VOIDTK) {
            retType = IntegerType.VOID;
        } else if (funcType.getType() == TokenType.INTTK) {
            retType = IntegerType.I32;
        } else {
            retType = IntegerType.I8;
        }

        Value funcValue = IRBuilder.addFunction(ident.getValue(), retType);
        funcSymbol.setValue(funcValue);

        if (funcFParams != null) {
            funcFParams.buildIR();
        }
        block.buildIR();

        if (!block.checkLastReturn()) {
            IRBuilder.addReturnInst();
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
