package AST;

import Lexer.Token;
import Error.*;
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
            FuncSymbol funcSymbol = (FuncSymbol) table.getSymbol(ident.getValue());
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
            Symbol funcSymbol = table.getSymbol(ident.getValue());
            if (funcSymbol.getType() == SymbolType.VoidFunc) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
