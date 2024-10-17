package AST;

import Lexer.Token;
import Symbol.SymbolTable;
import Error.*;

import java.util.ArrayList;

public class Block {
    private Token lbrace;
    private ArrayList<BlockItem> blockItems;
    private Token rbrace;

    public Block(Token lbrace, Token rbrace) {
        this.lbrace = lbrace;
        this.rbrace = rbrace;
    }

    public Block(Token lbrace, ArrayList<BlockItem> blockItems, Token rbrace) {
        this.lbrace = lbrace;
        this.blockItems = blockItems;
        this.rbrace = rbrace;
    }

    public void toSymbol(SymbolTable table) {
        for (BlockItem blockItem : blockItems) {
            blockItem.toSymbol(table);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lbrace.toString());
        for (BlockItem item : blockItems) {
            sb.append(item.toString());
        }
        sb.append(rbrace.toString());
        sb.append("<Block>\n");
        return sb.toString();
    }

    public void checkLastReturn() {
        if (blockItems.isEmpty()) {
            ErrorHandler.addError(rbrace.getLine(), ErrorType.g);
        } else {
            if (!blockItems.get(blockItems.size() - 1).isReturn()) {
                ErrorHandler.addError(rbrace.getLine(), ErrorType.g);
            }
        }
    }
}
