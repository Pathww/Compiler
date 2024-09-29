package AST;

import Lexer.Token;

public class MainFuncDef {
    private Token intTk;
    private Token mainTk;
    private Token lparent;
    private Token rparent;
    private Block block;

    public MainFuncDef(Token intTk, Token mainTk, Token lparent, Token rparent, Block block) {
        this.intTk = intTk;
        this.mainTk = mainTk;
        this.lparent = lparent;
        this.rparent = rparent;
        this.block = block;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(intTk.toString());
        sb.append(mainTk.toString());
        sb.append(lparent.toString());
        sb.append(rparent.toString());
        sb.append(block.toString());
        sb.append("<MainFuncDef>\n");
        return sb.toString();
    }

}
