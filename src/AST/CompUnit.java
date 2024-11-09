package AST;

import Symbol.SymbolTable;

import java.util.ArrayList;

public class CompUnit {
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;

    public CompUnit(ArrayList<Decl> decls, ArrayList<FuncDef> funcDefs, MainFuncDef mainFuncDef) {
        this.decls = decls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
    }

    public void toSymbol(SymbolTable table) {
        for (Decl d : decls) {
            d.toSymbol(table);
        }
        for (FuncDef f : funcDefs) {
            f.toSymbol(table);
        }
        mainFuncDef.toSymbol(table);
    }

    public void buildIR() {
        for (Decl d : decls) {
            d.buildIR();
        }
        for (FuncDef f : funcDefs) {
            f.buildIR();
        }
        mainFuncDef.buildIR();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Decl d : decls) {
            sb.append(d.toString());
        }
        for (FuncDef f : funcDefs) {
            sb.append(f.toString());
        }
        sb.append(mainFuncDef.toString());
        sb.append("<CompUnit>\n");
        return sb.toString();
    }
}
