package AST;

import LLVM.ConstInteger;
import LLVM.IRBuilder;
import LLVM.Instr.InstrType;
import LLVM.Type.ArrayType;
import LLVM.Type.IRType;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.*;
import Symbol.*;
import Error.*;

import java.util.ArrayList;

public class VarDef {
    private Token ident;
    private Token lbrack = null;
    private ConstExp constExp = null;
    private Token rbrack = null;
    private Token assign = null;
    private InitVal initVal = null;

    private SymbolTable symbolTable;
    private VarSymbol symbol;

    public VarDef(Token ident) {
        this.ident = ident;
    }

    public VarDef(Token ident, Token lbrack, ConstExp constExp, Token rbrack) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
    }

    public VarDef(Token ident, Token assign, InitVal initVal) {
        this.ident = ident;
        this.assign = assign;
        this.initVal = initVal;
    }

    public VarDef(Token ident, Token lbrack, ConstExp constExp, Token rbrack, Token assign, InitVal initVal) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assign = assign;
        this.initVal = initVal;
    }

    public void toSymbol(SymbolTable table, BType bType) {
        this.symbolTable = table;
        SymbolType type;
        if (lbrack != null) {
            if (bType.getType() == TokenType.INTTK) {
                type = SymbolType.IntArray;
            } else {
                type = SymbolType.CharArray;
            }
            constExp.toSymbol(table);
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
        if (initVal != null) {
            initVal.toSymbol(table);
        }
    }

    public void buildIR() {
        if (symbol.isArray()) {
            symbol.setLength(constExp.calVal());
        }
        if (initVal != null && symbolTable.isGlobal()) {
            if (symbol.isChar()) {
                ArrayList<Integer> tmp = initVal.calVal();
                int res = tmp.get(0) & 0xFF;
                tmp.set(0, res);
                symbol.setInitVals(tmp);
            } else {
                symbol.setInitVals(initVal.calVal());
            }
        }

        Value symbolValue;
        if (symbolTable.isGlobal()) {
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
                if (initVal != null) {
                    Value init = initVal.buildIR().get(0);
                    if (type != init.getType()) {
                        if (type == IntegerType.I8) {
                            if (init instanceof ConstInteger) {
                                init.setType(IntegerType.I8);
                            } else {
                                init = IRBuilder.addConvertInst(InstrType.TRUNC, init, IntegerType.I8);
                            }
                        }
                    }
                    IRBuilder.addStoreInst(init, symbolValue);
                }
            } else if (symbol.isIntArray()) {
                symbolValue = IRBuilder.addAllocaInst(new ArrayType(symbol.getLength(), IntegerType.I32));
                if (initVal != null) {
                    ArrayList<Value> initVals = initVal.buildIR(); // TODO对吗？
                    for (int i = 0; i < initVals.size(); i++) {
                        Value gep = IRBuilder.addGetElementPtrInst(symbolValue, new ConstInteger(i, IntegerType.I32));
                        IRBuilder.addStoreInst(initVals.get(i), gep);
                    }
                }
            } else {
                symbolValue = IRBuilder.addAllocaInst(new ArrayType(symbol.getLength(), IntegerType.I8));
                if (initVal != null) { // todo 不存在变量赋值？？？
                    ArrayList<Value> initVals = initVal.buildIR(); // TODO对吗？
                    int i = 0;
                    for (; i < initVals.size(); i++) {
                        Value value = initVals.get(i);
                        if (value.getType() != IntegerType.I8) {
                            if (value instanceof ConstInteger) {
                                value.setType(IntegerType.I8);
                            } else {
                                value = IRBuilder.addConvertInst(InstrType.TRUNC, value, IntegerType.I8);
                            }
                        }
                        Value gep = IRBuilder.addGetElementPtrInst(symbolValue, new ConstInteger(i, IntegerType.I32));
                        IRBuilder.addStoreInst(value, gep);
                    }
                    i++;
                    if (i < symbol.getLength()) {
                        Value gep = IRBuilder.addGetElementPtrInst(symbolValue, new ConstInteger(i, IntegerType.I32));
                        IRBuilder.addStoreInst(new ConstInteger(0, IntegerType.I8), gep);
                    }
//                    symbol.setInitVals(initVal.calVal());
//                    ArrayList<Integer> str = symbol.getInitVals();
//                    int i = 0;
//                    for (; i < str.size(); i++) {
//                        Value gep = IRBuilder.addGetElementPtrInst(symbolValue, new ConstInteger(i, IntegerType.I32));
//                        IRBuilder.addStoreInst(new ConstInteger(str.get(i), IntegerType.I8), gep);
//                    }
//                    i++;
//                    if (i < symbol.getLength()) {
//                        Value gep = IRBuilder.addGetElementPtrInst(symbolValue, new ConstInteger(i, IntegerType.I32));
//                        IRBuilder.addStoreInst(new ConstInteger(0, IntegerType.I8), gep);
//                    }
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
        if (initVal != null) {
            sb.append(assign);
            sb.append(initVal);
        }
        sb.append("<VarDef>\n");
        return sb.toString();
    }
}
