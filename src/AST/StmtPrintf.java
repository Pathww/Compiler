package AST;

import LLVM.ConstInteger;
import LLVM.IRBuilder;
import LLVM.Instr.InstrType;
import LLVM.Type.ArrayType;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.Token;
import Symbol.SymbolTable;
import Error.*;

import java.util.ArrayList;

public class StmtPrintf implements Stmt {
    private Token printfTk;
    private Token lparent;
    private Token stringConst;
    private ArrayList<Token> commas;
    private ArrayList<Exp> exps;
    private Token rparent;
    private Token semicn;

    private static int cnt = 0;

    private ArrayList<String> strs = new ArrayList<>();

    public StmtPrintf(Token printfTk, Token lparent, Token stringConst, ArrayList<Token> commas, ArrayList<Exp> exps, Token rparent, Token semicn) {
        this.printfTk = printfTk;
        this.lparent = lparent;
        this.stringConst = stringConst;
        this.commas = commas;
        this.exps = exps;
        this.rparent = rparent;
        this.semicn = semicn;
    }

    public void toSymbol(SymbolTable table) {
        String str = stringConst.getValue();
        str = str.substring(1, str.length() - 1).replace("\\n", "\n");
        int cnt = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '%' && i + 1 < str.length() && (str.charAt(i + 1) == 'd' || str.charAt(i + 1) == 'c')) {
                if (!sb.isEmpty()) {
                    strs.add(sb.toString());
                    sb = new StringBuilder();
                }
                cnt++;
                i++;
                if (str.charAt(i) == 'd') {
                    strs.add("%d");
                } else {
                    strs.add("%c");
                }
            } else {
                sb.append(str.charAt(i));
            }
        }
        if (!sb.isEmpty()) {
            strs.add(sb.toString());
        }

        if (cnt != exps.size()) {
            ErrorHandler.addError(printfTk.getLine(), ErrorType.l);
        }

        for (Exp exp : exps) {
            exp.toSymbol(table);
        }
    }

    public void buildIR() {
        int index = 0;
        for (String str : strs) {
            if (str.equals("%d") || str.equals("%c")) {
                Value value = exps.get(index).buildIR();
                if (value.getType() != IntegerType.I32) {
                    value = IRBuilder.addConvertInst(InstrType.ZEXT, value, IntegerType.I32);
                }
                ArrayList<Value> tmp = new ArrayList<>();
                tmp.add(value);
                if (str.equals("%d")) {
                    IRBuilder.addCallInst(IRBuilder.putint, tmp);
                } else {
                    IRBuilder.addCallInst(IRBuilder.putch, tmp);
                }
                index++;
            } else {
                int len = str.length() + 1;
//                String res = str.replace("\n", "\\0A");
//                res += "\\00";
                cnt++;
                Value gv = IRBuilder.addGlobalVariable(".str." + cnt, new ArrayType(len, IntegerType.I8), str);
                Value pointer = IRBuilder.addGetElementPtrInst(gv, new ConstInteger(0, IntegerType.I32));
                // TODO可不可以是I32？
                ArrayList<Value> tmp = new ArrayList<>();
                tmp.add(pointer);
                IRBuilder.addCallInst(IRBuilder.putstr, tmp);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(printfTk.toString());
        sb.append(lparent.toString());
        sb.append(stringConst.toString());
        for (int i = 0; i < commas.size(); i++) {
            sb.append(commas.get(i).toString());
            sb.append(exps.get(i).toString());
        }
        sb.append(rparent.toString());
        sb.append(semicn.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
