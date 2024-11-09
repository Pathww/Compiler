package AST;

import Lexer.Token;
import Symbol.SymbolTable;

import java.util.ArrayList;

public class ConstInitVal {
    private ConstExp constExp = null;

    private Token lbrace = null;
    private ArrayList<ConstExp> constExps = null;
    private ArrayList<Token> commas = null;
    private Token rbrace = null;

    private Token stringConst = null;

    public ConstInitVal(ConstExp constExp) {
        this.constExp = constExp;
    }

    public ConstInitVal(Token lbrace, ArrayList<ConstExp> constExps, ArrayList<Token> commas, Token rbrace) {
        this.lbrace = lbrace;
        this.constExps = constExps;
        this.commas = commas;
        this.rbrace = rbrace;
    }

    public ConstInitVal(Token stringConst) {
        this.stringConst = stringConst;
    }

    public void toSymbol(SymbolTable table) {
        if (constExp != null) {
            constExp.toSymbol(table);
        } else if (lbrace != null) {
            for (ConstExp c : constExps) {
                c.toSymbol(table);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (constExp != null) {
            sb.append(constExp);
        } else if (stringConst != null) {
            sb.append(stringConst);
        } else {
            sb.append(lbrace);
            if (!constExps.isEmpty()) {
                sb.append(constExps.get(0));
                for (int i = 0; i < commas.size(); i++) {
                    sb.append(commas.get(i));
                    sb.append(constExps.get(i + 1));
                }
            }
            sb.append(rbrace);
        }

        sb.append("<ConstInitVal>\n");
        return sb.toString();
    }

    public String getStringConst() {
        if (stringConst != null) {
            return stringConst.getValue().substring(1, stringConst.getValue().length() - 1);
        }
        StringBuilder sb = new StringBuilder();
        for (ConstExp c : constExps) {
            char ch = (char) c.calVal();
            sb.append(ch);
        }
        return sb.toString();
    }

    public ArrayList<Integer> calVal() {
        ArrayList<Integer> list = new ArrayList<>();
        if (constExp != null) {
            list.add(constExp.calVal());
        } else if (constExps != null) {
            for (ConstExp e : constExps) {
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

