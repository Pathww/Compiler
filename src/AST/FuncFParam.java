package AST;

import LLVM.IRBuilder;
import LLVM.Type.IRType;
import LLVM.Type.IntegerType;
import LLVM.Type.PointerType;
import LLVM.Value;
import Lexer.*;
import Symbol.*;
import Error.*;

public class FuncFParam {
    private BType bType;
    private Token ident;

    private Token lbrack = null;
    private Token rbrack = null;
    private VarSymbol symbol;

    public FuncFParam(BType bType, Token ident) {
        this.bType = bType;
        this.ident = ident;
    }

    public FuncFParam(BType bType, Token ident, Token lbrack, Token rbrack) {
        this.bType = bType;
        this.ident = ident;
        this.lbrack = lbrack;
        this.rbrack = rbrack;
    }

    public void toSymbol(SymbolTable table) {
        SymbolType type;
        if (lbrack != null) {
            if (bType.getType() == TokenType.INTTK) {
                type = SymbolType.IntArray;
            } else {
                type = SymbolType.CharArray;
            }
        } else {
            if (bType.getType() == TokenType.INTTK) {
                type = SymbolType.Int;
            } else {
                type = SymbolType.Char;
            }
        }
        symbol = new VarSymbol(ident.getValue(), type);
        if (!table.addSymbol(symbol)) {
            ErrorHandler.addError(ident.getLine(), ErrorType.b);
        }
    }

    public void buildIR() {
        IRType type;
        type = (symbol.getType() == SymbolType.Int || symbol.getType() == SymbolType.IntArray) ? IntegerType.I32 : IntegerType.I8;
        if (lbrack != null) {
            type = new PointerType(type);
        }
        Value param = IRBuilder.addParam(type);
//        symbol.setValue(param);
        Value alloc = IRBuilder.addAllocaInst(type);
        IRBuilder.addStoreInst(param, alloc);

        if (lbrack != null) {
            alloc = IRBuilder.addLoadInst(alloc); // todo 优化时解决掉！
        }
        symbol.setValue(alloc);
        // 数组指针不用alloc+store'？？？
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bType.toString());
        sb.append(ident.toString());
        if (lbrack != null) {
            sb.append(lbrack);
            sb.append(rbrack);
        }
        sb.append("<FuncFParam>\n");
        return sb.toString();
    }
}
