package AST;

import Lexer.Token;

public class MulExp {
    private UnaryExp unaryExp = null;

    private Token op = null;
    private MulExp mulExp = null;


    public MulExp(UnaryExp unaryExp) {
        this.unaryExp = unaryExp;
    }

    public MulExp(MulExp mulExp, Token op, UnaryExp unaryExp) {
        this.mulExp = mulExp;
        this.op = op;
        this.unaryExp = unaryExp;
    }

    public LVal toLVal() {
        return unaryExp.toLVal();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (mulExp != null) {
            sb.append(mulExp.toString());
            sb.append(op.toString());
        }
        sb.append(unaryExp.toString());
        sb.append("<MulExp>\n");
        return sb.toString();
    }
}
