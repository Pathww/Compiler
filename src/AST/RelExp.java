package AST;

import Lexer.Token;

public class RelExp {
    private AddExp addExp = null;
    private RelExp relExp = null;
    private Token op = null;


    public RelExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public RelExp(RelExp relExp, Token op, AddExp addExp) {
        this.relExp = relExp;
        this.op = op;
        this.addExp = addExp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (relExp != null) {
            sb.append(relExp);
            sb.append(op.toString());
        }
        sb.append(addExp.toString());
        sb.append("<RelExp>\n");
        return sb.toString();
    }
}
