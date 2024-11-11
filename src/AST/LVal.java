package AST;

import LLVM.ConstInteger;
import LLVM.IRBuilder;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.*;
import Symbol.*;
import Error.*;

public class LVal {
    private Token ident;

    private Token lbrack = null;
    private Exp exp = null;
    private Token rbrack = null;

    private VarSymbol symbol;

    public LVal(Token ident) {
        this.ident = ident;
    }

    public LVal(Token ident, Token lbrack, Exp exp, Token rbrack) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.exp = exp;
        this.rbrack = rbrack;
    }

    public void toSymbol(SymbolTable table) {
        Symbol res = table.getSymbol(ident.getValue());
        if (res == null) {
            ErrorHandler.addError(ident.getLine(), ErrorType.c);
        } else {
            symbol = (VarSymbol) res;
        }
        if (exp != null) {
            exp.toSymbol(table);
        }
    }

    private boolean isArrayPara = false;

    public Value buildIR() { // pointer
        if (symbol.getDim() == 0) {
            return symbol.getValue();
        } else if (lbrack != null) { // 数组元素
            Value val = symbol.getValue();
//            if (((PointerType) symbol.getValue().getType()).getRefType() instanceof PointerType) {
//                val = IRBuilder.addLoadInst(symbol.getValue());
//            }
            Value index = exp.buildIR();
            return IRBuilder.addGetElementPtrInst(val, index);
        } else { // 数组指针
            isArrayPara = true;
            return IRBuilder.addGetElementPtrInst(symbol.getValue(), new ConstInteger(0, IntegerType.I32));
        }
    }

    public boolean isArrayPara() {
        return isArrayPara;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        if (exp != null) {
            sb.append(lbrack);
            sb.append(exp);
            sb.append(rbrack);
        }
        sb.append("<LVal>\n");
        return sb.toString();
    }

    public Token getIdent() {
        return ident;
    }

    public int getParaType(SymbolTable table) {
        if (symbol.isIntArray() && lbrack == null) {
            return 1;
        } else if (symbol.isCharArray() && lbrack == null) {
            return 2;
        } else {
            return 0;
        }
    }

    public int calVal() {
        if (symbol.isInt() || symbol.isChar()) { // TODO: 勿忘const！！！
            return symbol.getInitVal(0);
        } else {
            int index = exp.calVal();
            return symbol.getInitVal(index);
        }
    }
}
