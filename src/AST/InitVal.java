package AST;

import LLVM.ConstInteger;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.Token;
import Symbol.SymbolTable;

import java.util.ArrayList;

public class InitVal {
    private Exp exp = null;

    private Token lbrace = null;
    private ArrayList<Exp> exps = null;
    private ArrayList<Token> commas = null;
    private Token rbrace = null;

    private Token stringConst = null;

    public InitVal(Exp exp) {
        this.exp = exp;
    }

    public InitVal(Token lbrace, ArrayList<Exp> exps, ArrayList<Token> commas, Token rbrace) {
        this.lbrace = lbrace;
        this.exps = exps;
        this.commas = commas;
        this.rbrace = rbrace;
    }

    public InitVal(Token stringConst) {
        this.stringConst = stringConst;
    }

    public void toSymbol(SymbolTable table) {
        if (exp != null) {
            exp.toSymbol(table);
        } else if (lbrace != null) {
            for (Exp e : exps) {
                e.toSymbol(table);
            }
        }
    }

    public ArrayList<Value> buildIR() {
        ArrayList<Value> values = new ArrayList<>();
        if (exp != null) {
            values.add(exp.buildIR());
        } else if (exps != null) {
            for (Exp e : exps) {
                values.add(e.buildIR());
            }
        } else {
            String str = stringConst.getValue().substring(1, stringConst.getValue().length() - 1);
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch == '\\') {
                    i++;
                    ch = str.charAt(i);
                    switch (ch) {
                        case 'a':
                            ch = 7;
                            break;
                        case 'b':
                            ch = '\b';
                            break;
                        case 't':
                            ch = '\t';
                            break;
                        case 'n':
                            ch = '\n';
                            break;
                        case 'v':
                            ch = 11;
                            break;
                        case 'f':
                            ch = '\f';
                            break;
                        case '\"':
                            ch = '\"';
                            break;
                        case '\'':
                            ch = '\'';
                            break;
                        case '\\':
                            ch = '\\';
                            break;
                        case '0':
                            ch = '\0';
                            break;
                    }
                }
                values.add(new ConstInteger(ch, IntegerType.I8));
            }
        }
        return values;
    }

    public String getStringConst() {
        if (stringConst != null) {
            return stringConst.getValue().substring(1, stringConst.getValue().length() - 1);
        }
        StringBuilder sb = new StringBuilder();
        for (Exp e : exps) {
            char ch = (char) e.calVal();
            sb.append(ch);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (exp != null) {
            sb.append(exp);
        } else if (stringConst != null) {
            sb.append(stringConst);
        } else {
            sb.append(lbrace);
            if (!exps.isEmpty()) {
                sb.append(exps.get(0));
                for (int i = 0; i < commas.size(); i++) {
                    sb.append(commas.get(i));
                    sb.append(exps.get(i + 1));
                }
            }
            sb.append(rbrace);
        }

        sb.append("<InitVal>\n");
        return sb.toString();
    }

    public ArrayList<Integer> calVal() {
        ArrayList<Integer> list = new ArrayList<>();
        if (exp != null) {
            list.add(exp.calVal());
        } else if (exps != null) {
            for (Exp e : exps) {
                list.add(e.calVal());
            }
        } else {
            String str = stringConst.getValue().substring(1, stringConst.getValue().length() - 1);
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch == '\\') {
                    i++;
                    ch = str.charAt(i);
                    switch (ch) {
                        case 'a':
                            ch = 7;
                            break;
                        case 'b':
                            ch = '\b';
                            break;
                        case 't':
                            ch = '\t';
                            break;
                        case 'n':
                            ch = '\n';
                            break;
                        case 'v':
                            ch = 11;
                            break;
                        case 'f':
                            ch = '\f';
                            break;
                        case '\"':
                            ch = '\"';
                            break;
                        case '\'':
                            ch = '\'';
                            break;
                        case '\\':
                            ch = '\\';
                            break;
                        case '0':
                            ch = '\0';
                            break;
                    }
                }
                list.add((int) ch);
            }
        }
        return list;
    }
}
