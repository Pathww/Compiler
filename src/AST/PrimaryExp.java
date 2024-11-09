package AST;

import LLVM.IRBuilder;
import LLVM.Value;
import Lexer.Token;
import Symbol.SymbolTable;

public class PrimaryExp {
    private Token lparent = null;
    private Exp exp = null;
    private Token rparent = null;

    private LVal lVal = null;

    private Number number = null;

    private Character character = null;

    public PrimaryExp(Token lparent, Exp exp, Token rparent) {
        this.lparent = lparent;
        this.exp = exp;
        this.rparent = rparent;
    }

    public PrimaryExp(LVal lVal) {
        this.lVal = lVal;
    }

    public PrimaryExp(Number number) {
        this.number = number;
    }

    public PrimaryExp(Character character) {
        this.character = character;
    }

    public LVal toLVal() {
        return lVal;
    }

    public void toSymbol(SymbolTable table) {
        if (lVal != null) {
            lVal.toSymbol(table);
        } else if (lparent != null) {
            exp.toSymbol(table);
        }
    }

    public Value buildIR() {
        if (lVal != null) {
            Value value = lVal.buildIR();
            if (lVal.isArrayPara()) {
                return value;
            }
            return IRBuilder.addLoadInst(value); // LOAD 勿忘
        } else if (number != null) {
            return number.buildIR();
        } else if (character != null) {
            return character.buildIR();
        } else {
            return exp.buildIR();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lVal != null) {
            sb.append(lVal);
        } else if (number != null) {
            sb.append(number);
        } else if (character != null) {
            sb.append(character);
        } else {
            sb.append(lparent);
            sb.append(exp);
            sb.append(rparent);
        }
        sb.append("<PrimaryExp>\n");
        return sb.toString();
    }

    public int getParaType(SymbolTable table) {
        if (exp != null) {
            return exp.getParaType(table);
        } else if (lVal != null) {
            return lVal.getParaType(table);
        } else {
            return 0;
        }
    }

    public int calVal() {
        if (exp != null) {
            return exp.calVal();
        } else if (lVal != null) {
            return lVal.calVal();
        } else if (number != null) {
            return number.calVal();
        } else {
            return character.calVal();
        }
    }
}

