package AST;

import Lexer.Token;

public class FuncDef {
    private FuncType funcType;
    private Token ident;
    private Token lparent;
    private FuncFParams funcFParams = null;
    private Token rparent;
    private Block block;

    public FuncDef(FuncType funcType, Token ident, Token lparent, Token rparent, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.lparent = lparent;
        this.rparent = rparent;
        this.block = block;
    }

    public FuncDef(FuncType funcType, Token ident, Token lparent, FuncFParams funcFParams, Token rparent, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.lparent = lparent;
        this.funcFParams = funcFParams;
        this.rparent = rparent;
        this.block = block;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcType.toString());
        sb.append(ident.toString());
        sb.append(lparent.toString());
        if (funcFParams != null) {
            sb.append(funcFParams);
        }
        sb.append(rparent.toString());
        sb.append(block.toString());
        sb.append("<FuncDef>\n");
        return sb.toString();
    }
}
