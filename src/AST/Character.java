package AST;

import LLVM.ConstInteger;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.Token;

public class Character {
    private Token charConst;

    private char ch;

    public Character(Token charConst) {
        this.charConst = charConst;
        ch = toChar(charConst.getValue());
    }

    public char toChar(String str) {
        str = str.substring(1, str.length() - 1);
        switch (str) { // todo LLVM \0A 等还没处理
            case "\\a":
                return 7;
            case "\\b":
                return '\b';
            case "\\t":
                return '\t';
            case "\\n":
                return '\n';
            case "\\v":
                return 11;
            case "\\f":
                return '\f';
            case "\\":
                return '\"';
            case "\\'":
                return '\'';
            case "\\\\":
                return '\\';
            case "\\0":
                return '\0';
            default:
                return str.charAt(0);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(charConst.toString());
        sb.append("<Character>\n");
        return sb.toString();
    }

    public int calVal() {
        //TODO 其他转义字符？
        return ch;
    }

    public Value buildIR() {
        return new ConstInteger(ch, IntegerType.I8); // TODO: I8????
    }
}
