package AST;

import Lexer.Token;

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
}
