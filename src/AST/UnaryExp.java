package AST;

import LLVM.ConstInteger;
import LLVM.Function;
import LLVM.IRBuilder;
import LLVM.Instr.InstrType;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.Token;
import Error.*;
import Lexer.TokenType;
import Symbol.*;

import java.util.ArrayList;

public class UnaryExp {
    private PrimaryExp primaryExp = null;

    private Token ident = null;
    private Token lparent = null;
    private FuncRParams funcRParams = null;
    private Token rparent = null;

    private UnaryOp unaryOp = null;
    private UnaryExp unaryExp = null;

    private FuncSymbol funcSymbol;

    public UnaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }

    public UnaryExp(Token ident, Token lparent, Token rparent) {
        this.ident = ident;
        this.lparent = lparent;
        this.rparent = rparent;
    }

    public UnaryExp(Token ident, Token lparent, FuncRParams funcRParams, Token rparent) {
        this.ident = ident;
        this.lparent = lparent;
        this.funcRParams = funcRParams;
        this.rparent = rparent;
    }

    public UnaryExp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }

    public LVal toLVal() {
        return primaryExp.toLVal();
    }

    public void toSymbol(SymbolTable table) {
        if (primaryExp != null) {
            primaryExp.toSymbol(table);
        } else if (unaryExp != null) {
            unaryExp.toSymbol(table);
        } else {
            funcSymbol = (FuncSymbol) table.getSymbol(ident.getValue());
            if (funcSymbol == null) {
                ErrorHandler.addError(ident.getLine(), ErrorType.c);
                return;
            }
            ArrayList<Integer> fparams = funcSymbol.getParaTypes();
            if (funcRParams != null) {
                funcRParams.toSymbol(table);
                ArrayList<Exp> rparams = funcRParams.getExps();
                if (rparams.size() != fparams.size()) {
                    ErrorHandler.addError(ident.getLine(), ErrorType.d);
                    return;
                }
                for (int i = 0; i < fparams.size(); i++) {
                    if (rparams.get(i).getParaType(table) != fparams.get(i)) {
                        ErrorHandler.addError(ident.getLine(), ErrorType.e);
                    }
                }
            } else if (!fparams.isEmpty()) {
                ErrorHandler.addError(ident.getLine(), ErrorType.d);
            }
        }
    }

    public Value buildIR() {
        if (primaryExp != null) {
            return primaryExp.buildIR();
        } else if (unaryExp != null) {
            Value value = unaryExp.buildIR();
            if (value.getType() != IntegerType.I32) { // SEXT???
                value = IRBuilder.addConvertInst(InstrType.ZEXT, value, IntegerType.I32);
            }
            if (unaryOp.getType() == TokenType.PLUS) {
                return value;
            } else if (unaryOp.getType() == TokenType.MINU) {
                return IRBuilder.addBinaryInst(InstrType.SUB, new ConstInteger(0, IntegerType.I32), value);
            } else {
                Value cmp = IRBuilder.addCmpInst(InstrType.EQ, new ConstInteger(0, IntegerType.I32), value);
                return IRBuilder.addConvertInst(InstrType.ZEXT, cmp, IntegerType.I32);
            }
        } else {
            Function function = (Function) funcSymbol.getValue();
            if (funcRParams != null) {
                ArrayList<Value> values = funcRParams.buildIR();
                ArrayList<Value> params = function.getParams();
                for (int i = 0; i < values.size(); i++) {
                    Value value = values.get(i);
                    Value param = params.get(i);
                    if (value.getType() != param.getType() && param.getType() instanceof IntegerType) {
                        if (value.getType() == IntegerType.I32) {
                            if (value instanceof ConstInteger) {
                                value.setType(IntegerType.I8);
                            }else {
                                value = IRBuilder.addConvertInst(InstrType.TRUNC, value, IntegerType.I8);
                                values.set(i, value);
                            }
                        }else {
                            if (value instanceof ConstInteger) {
                                value.setType(IntegerType.I32);
                            }else {
                                value = IRBuilder.addConvertInst(InstrType.ZEXT, value, IntegerType.I32);
                                values.set(i, value);
                            }
                        }
                    }
                }
                return IRBuilder.addCallInst(function, values);
            } else {
                return IRBuilder.addCallInst(function, new ArrayList<>());
                // zext???
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (primaryExp != null) {
            sb.append(primaryExp);
        } else if (unaryExp != null) {
            sb.append(unaryOp);
            sb.append(unaryExp);
        } else {
            sb.append(ident);
            sb.append(lparent);
            if (funcRParams != null) {
                sb.append(funcRParams);
            }
            sb.append(rparent);
        }

        sb.append("<UnaryExp>\n");
        return sb.toString();
    }

    public int getParaType(SymbolTable table) {
        if (primaryExp != null) {
            return primaryExp.getParaType(table);
        } else if (unaryExp != null) {
            return 0;
        } else {
            funcSymbol = (FuncSymbol) table.getSymbol(ident.getValue());
            if (funcSymbol.getType() == SymbolType.VoidFunc) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public int calVal() {
        if (primaryExp != null) {
            return primaryExp.calVal();
        } else { // 常量计算无函数调用
            if (unaryOp.getType() == TokenType.PLUS) {
                return unaryExp.calVal();
            } else {
                return -unaryExp.calVal();
            }
        }
    }
}
