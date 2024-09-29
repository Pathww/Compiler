package AST;

import Lexer.Token;

public class AddExp {
    private MulExp mulExp = null;
    private AddExp addExp = null;
    private Token op = null;

    public AddExp(MulExp mulExp) {
        this.mulExp = mulExp;
    }

    public AddExp(AddExp addExp, Token op, MulExp mulExp) {
        this.addExp = addExp;
        this.op = op;
        this.mulExp = mulExp;
    }

    public LVal toLVal() {
        return mulExp.toLVal();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (addExp != null) {
            sb.append(addExp);
            sb.append(op.toString());
        }
        sb.append(mulExp.toString());
        sb.append("<AddExp>\n");
        return sb.toString();
    }


}
