package AST;

import LLVM.ConstInteger;
import LLVM.IRBuilder;
import LLVM.Type.ArrayType;
import LLVM.Type.IRType;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.*;
import Symbol.*;
import Error.*;

import java.util.ArrayList;

public class ConstDef {
    private Token ident;

    private Token lbrack = null;
    private ConstExp constExp = null;
    private Token rbrack = null;

    private Token assign;
    private ConstInitVal constInitVal;
    private SymbolTable symbolTable;
    private VarSymbol symbol;

    public ConstDef(Token ident, Token assign, ConstInitVal constInitVal) {
        this.ident = ident;
        this.assign = assign;
        this.constInitVal = constInitVal;
    }

    public ConstDef(Token ident, Token lbrack, ConstExp constExp, Token rbrack, Token assign, ConstInitVal constInitVal) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assign = assign;
        this.constInitVal = constInitVal;
    }

    public void toSymbol(SymbolTable table, BType bType) {
        this.symbolTable = table;
        SymbolType type;
        if (lbrack != null) {
            if (bType.getType() == TokenType.INTTK) {
                type = SymbolType.ConstIntArray;
            } else {
                type = SymbolType.ConstCharArray;
            }
            constExp.toSymbol(table);
        } else {
            if (bType.getType() == TokenType.INTTK) {
                type = SymbolType.ConstInt;
            } else {
                type = SymbolType.ConstChar;
            }
        }

        symbol = new VarSymbol(ident.getValue(), type);
        if (!table.addSymbol(symbol)) {
            ErrorHandler.addError(ident.getLine(), ErrorType.b);
        }
        constInitVal.toSymbol(table);
    }

    public void buildIR() { // 别忘加const！
        if (symbol.isArray()) {
            symbol.setLength(constExp.calVal());
        }

        if (constInitVal != null) {
            if (symbol.isChar()) {
                ArrayList<Integer> tmp = constInitVal.calVal();
                int res = tmp.get(0) & 0xFF;
                tmp.set(0, res);
                symbol.setInitVals(tmp);
            } else {
                symbol.setInitVals(constInitVal.calVal());
            }
        }

        Value symbolValue;
        if (symbolTable.isGlobal()) { //"\n \0A"
            IRType type = (symbol.isInt() || symbol.isIntArray()) ? IntegerType.I32 : IntegerType.I8;
            if (symbol.isInt() || symbol.isChar()) {
                symbolValue = IRBuilder.addGlobalVariable(symbol.getIdent(), type, symbol.getInitVals());
            } else {
                symbolValue = IRBuilder.addGlobalVariable(symbol.getIdent(), new ArrayType(symbol.getLength(), type), symbol.getInitVals());
            }
        } else {
            if (symbol.isInt() || symbol.isChar()) {
                IRType type = (symbol.isInt()) ? IntegerType.I32 : IntegerType.I8;
                symbolValue = IRBuilder.addAllocaInst(type);
                if (constInitVal != null) {
                    IRBuilder.addStoreInst(new ConstInteger(symbol.getInitVal(0), type), symbolValue);
                }
            } else if (symbol.isIntArray()) {
                symbolValue = IRBuilder.addAllocaInst(new ArrayType(symbol.getLength(), IntegerType.I32));
                if (constInitVal != null) {
                    for (int i = 0; i < symbol.getInitVals().size(); i++) {
                        Value gep = IRBuilder.addGetElementPtrInst(symbolValue, new ConstInteger(i, IntegerType.I32));
                        IRBuilder.addStoreInst(new ConstInteger(symbol.getInitVal(i), IntegerType.I32), gep);
                    }
                }
            } else {
                symbolValue = IRBuilder.addAllocaInst(new ArrayType(symbol.getLength(), IntegerType.I8));
                if (constInitVal != null) {
                    ArrayList<Integer> str = symbol.getInitVals();
                    int i = 0;
                    for (; i < str.size(); i++) {
                        Value gep = IRBuilder.addGetElementPtrInst(symbolValue, new ConstInteger(i, IntegerType.I32));
                        IRBuilder.addStoreInst(new ConstInteger(str.get(i), IntegerType.I8), gep);
                    }
                    i++;
                    if (i < symbol.getLength()) {
                        Value gep = IRBuilder.addGetElementPtrInst(symbolValue, new ConstInteger(i, IntegerType.I32));
                        IRBuilder.addStoreInst(new ConstInteger(0, IntegerType.I8), gep);
                    }
                }
            }
        }
        symbol.setValue(symbolValue);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        if (lbrack != null) {
            sb.append(lbrack);
            sb.append(constExp);
            sb.append(rbrack);
        }
        sb.append(assign);
        sb.append(constInitVal);

        sb.append("<ConstDef>\n");
        return sb.toString();
    }
}
