import AST.*;
import AST.Character;
import AST.Number;
import Lexer.Token;
import Lexer.TokenType;
import Error.*;

import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokens;
    private int size;
    private int pos = -1;
    private Token curToken;
    private Token eofTk = new Token(TokenType.EOFTK, "", 0);
    private ErrorHandler errorHandler;

    public Parser(ArrayList<Token> tokens, ErrorHandler errorHandler) {
        this.tokens = tokens;
        size = tokens.size();
        next();
        this.errorHandler = errorHandler;
    }

    public void next() {
        pos++;
        if (pos < size) {
            curToken = tokens.get(pos);
        } else {
            curToken = eofTk;
            /*
             *
             * QQQQ?
             * */
        }
    }

    public Token preRead(int offset) {
        if (pos + offset < size) {
            return tokens.get(pos + offset);
        } else {
            return eofTk;
        }
    }


    //检查报错的情况！！！别忘了next！！！
    public CompUnit parseCompUnit() {
        ArrayList<Decl> decls = new ArrayList<>();
        ArrayList<FuncDef> funcDefs = new ArrayList<>();

        while (curToken.getType() == TokenType.CONSTTK ||
                (preRead(2).getType() != TokenType.LPARENT)) {
            decls.add(parseDecl());
        }
        while (preRead(1).getType() == TokenType.IDENFR) {
            funcDefs.add(parseFuncDef());
        }

        MainFuncDef mainFuncDef = parseMainFuncDef();
        return new CompUnit(decls, funcDefs, mainFuncDef);
    }


    private Decl parseDecl() {
        if (curToken.getType() == TokenType.CONSTTK) {
            return new Decl(parseConstDecl());
        } else {
            return new Decl(parseVarDecl());
        }
    }


    private ConstDecl parseConstDecl() {
        Token constTk = curToken;
        next();
        BType bType = parseBType();
        ArrayList<ConstDef> constDefs = new ArrayList<>();
        ArrayList<Token> commas = new ArrayList<>();
        constDefs.add(parseConstDef());
        while (curToken.getType() == TokenType.COMMA) {
            commas.add(curToken);
            next();
            constDefs.add(parseConstDef());
        }
        Token semicn = curToken;
        next();
        return new ConstDecl(constTk, bType, constDefs, commas, semicn);
    }

    private BType parseBType() {
        Token type = curToken;
        next();
        return new BType(type);
    }

    private ConstDef parseConstDef() {
        Token ident = curToken;
        next();
        Token lbr = null, rbr = null;
        ConstExp constExp = null;
        if (curToken.getType() == TokenType.LBRACK) {
            lbr = curToken;
            next();
            constExp = parseConstExp();
            rbr = curToken;
            next();
        }
        Token assign = curToken;
        next();
        ConstInitVal constInitVal = parseConstInitVal();
        return new ConstDef(ident, lbr, constExp, rbr, assign, constInitVal);

    }

    private ConstInitVal parseConstInitVal() {
        if (curToken.getType() == TokenType.STRCON) {
            Token strCon = curToken;
            next();
            return new ConstInitVal(strCon);
        } else if (curToken.getType() == TokenType.LBRACE) {
            Token lbr = curToken;
            next();
            ArrayList<ConstExp> constExps = new ArrayList<>();
            ArrayList<Token> commas = new ArrayList<>();
            if (curToken.getType() != TokenType.RBRACE) {
                constExps.add(parseConstExp());
                while (curToken.getType() == TokenType.COMMA) {
                    commas.add(curToken);
                    next();
                    constExps.add(parseConstExp());
                }
            }
            Token rbr = curToken;
            next();
            return new ConstInitVal(lbr, constExps, commas, rbr);
        } else {
            return new ConstInitVal(parseConstExp());
        }
    }

    private VarDecl parseVarDecl() {
        BType bType = parseBType();
        ArrayList<VarDef> varDefs = new ArrayList<>();
        ArrayList<Token> commas = new ArrayList<>();
        varDefs.add(parseVarDef());
        while (curToken.getType() == TokenType.COMMA) {
            commas.add(curToken);
            next();
            varDefs.add(parseVarDef());
        }
        Token semicn = curToken;
        next();
        return new VarDecl(bType, varDefs, commas, semicn);
    }

    private VarDef parseVarDef() {
        Token ident = curToken;
        next();
        Token lbr = null, rbr = null, assign = null;
        ConstExp constExp = null;
        InitVal initVal = null;
        if (curToken.getType() == TokenType.LBRACK) {
            lbr = curToken;
            next();
            constExp = parseConstExp();
            rbr = curToken;
            next();
        }
        if (curToken.getType() == TokenType.ASSIGN) {
            assign = curToken;
            next();
            initVal = parseInitVal();
        }
        return new VarDef(ident, lbr, constExp, rbr, assign, initVal);
    }

    private InitVal parseInitVal() {
        if (curToken.getType() == TokenType.STRCON) {
            Token strCon = curToken;
            next();
            return new InitVal(strCon);
        } else if (curToken.getType() == TokenType.LBRACE) {
            Token lbr = curToken;
            next();
            ArrayList<Exp> exps = new ArrayList<>();
            ArrayList<Token> commas = new ArrayList<>();
            if (curToken.getType() != TokenType.RBRACE) {
                exps.add(parseExp());
                while (curToken.getType() == TokenType.COMMA) {
                    commas.add(curToken);
                    next();
                    exps.add(parseExp());
                }
            }
            Token rbr = curToken;
            next();
            return new InitVal(lbr, exps, commas, rbr);
        } else {
            return new InitVal(parseExp()); //TODO:可以复合吗？？？
        }

    }

    private FuncDef parseFuncDef() {
        FuncType funcType = parseFuncType();
        Token ident = curToken;
        next();
        Token lbr = curToken;
        next();
        FuncFParams funcFParams = null;
        if (curToken.getType() != TokenType.RPARENT) {
            funcFParams = parseFuncFParams();
        }
        Token rbr = curToken;
        next();
        Block block = parseBlock();
        return new FuncDef(funcType, ident, lbr, funcFParams, rbr, block);

    }

    private MainFuncDef parseMainFuncDef() {
        Token intTk = curToken;
        next();
        Token mainTk = curToken;
        next();
        Token lparent = curToken;
        next();
        Token rparent = curToken;
        next();
        Block block = parseBlock();
        return new MainFuncDef(intTk, mainTk, lparent, rparent, block);

    }

    private FuncType parseFuncType() {
        Token type = curToken;
        next();
        return new FuncType(type);
    }

    private FuncFParams parseFuncFParams() {
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        ArrayList<Token> commas = new ArrayList<>();
        funcFParams.add(parseFuncFParam());
        while (curToken.getType() == TokenType.COMMA) {
            commas.add(curToken);
            next();
            funcFParams.add(parseFuncFParam());
        }
        return new FuncFParams(funcFParams, commas);
    }

    private FuncFParam parseFuncFParam() {
        BType bType = parseBType();
        Token ident = curToken;
        next();
        if (curToken.getType() == TokenType.LBRACK) {
            Token lbr = curToken;
            next();
            Token rbr = curToken;
            next();
            return new FuncFParam(bType, ident, lbr, rbr);
        }
        return new FuncFParam(bType, ident);
    }

    private Block parseBlock() {
        Token lbrace = curToken;
        next();
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        while (curToken.getType() != TokenType.RBRACE) {
            blockItems.add(parseBlockItem());
        }
        Token rbrace = curToken;
        next();
        return new Block(lbrace, blockItems, rbrace);
    }

    private BlockItem parseBlockItem() {
        if (curToken.getType() == TokenType.CONSTTK || curToken.getType() == TokenType.INTTK || curToken.getType() == TokenType.CHARTK) {
            return new BlockItem(parseDecl());
        } else {
            return new BlockItem(parseStmt());
        }
    }

    private Stmt parseStmt() {
        if (curToken.getType() == TokenType.LBRACE) {
            return new StmtBlock(parseBlock());
        } else if (curToken.getType() == TokenType.IFTK) {
            Token ifTk = curToken;
            next();
            Token lbr = curToken;
            next();
            Cond cond = parseCond();
            Token rbr = curToken;
            next();
            Stmt ifStmt = parseStmt();
            Token elseTk = null;
            Stmt elseStmt = null;
            if (curToken.getType() == TokenType.ELSETK) {
                elseTk = curToken;
                next();
                elseStmt = parseStmt();
            }
            return new StmtIf(ifTk, lbr, cond, rbr, ifStmt, elseTk, elseStmt);
        } else if (curToken.getType() == TokenType.FORTK) {
            Token forTk = curToken;
            next();
            Token lbr = curToken;
            next();
            ForStmt forStmt1 = null, forStmt2 = null;
            Cond cond = null;
            if (curToken.getType() != TokenType.SEMICN) {
                forStmt1 = parseForStmt();
            }
            Token semicn1 = curToken;
            next();
            if (curToken.getType() != TokenType.SEMICN) {
                cond = parseCond();
            }
            Token semicn2 = curToken;
            next();
            if (curToken.getType() != TokenType.RPARENT) {
                forStmt2 = parseForStmt();
            }
            Token rbr = curToken;
            next();
            Stmt stmt = parseStmt();
            return new StmtFor(forTk, lbr, forStmt1, semicn1, cond, semicn2, forStmt2, rbr, stmt);
        } else if (curToken.getType() == TokenType.BREAKTK) {
            Token breakTk = curToken;
            next();
            Token semicn = curToken;
            next();
            return new StmtBreak(breakTk, semicn);
        } else if (curToken.getType() == TokenType.CONTINUETK) {
            Token continueTk = curToken;
            next();
            Token semicn = curToken;
            next();
            return new StmtContinue(continueTk, semicn);
        } else if (curToken.getType() == TokenType.RETURNTK) {
            Token returnTk = curToken;
            next();
            Exp exp = null;
            if (curToken.getType() != TokenType.SEMICN) {
                exp = parseExp();
            }
            Token semicn = curToken;
            next();
            return new StmtReturn(returnTk, exp, semicn);
        } else if (curToken.getType() == TokenType.PRINTFTK) {
            Token printfTk = curToken;
            next();
            Token lbr = curToken;
            next();
            Token strCon = curToken;
            next();
            ArrayList<Token> commas = new ArrayList<>();
            ArrayList<Exp> exps = new ArrayList<>();

            while (curToken.getType() == TokenType.COMMA) {
                commas.add(curToken);
                next();
                exps.add(parseExp());
            }
            Token rbr = curToken;
            next();
            Token semicn = curToken;
            next();
            return new StmtPrintf(printfTk, lbr, strCon, commas, exps, rbr, semicn);
        } else {
            for (int i = 0; i < size; i++) {
                TokenType type = preRead(i).getType();
                if (type == TokenType.ASSIGN) {
                    LVal lVal = parseLVal();
                    Token assign = curToken;
                    next();
                    if (curToken.getType() == TokenType.GETINTTK || curToken.getType() == TokenType.GETCHARTK) {
                        Token getTk = curToken;
                        next();
                        Token lparent = curToken;
                        next();
                        Token rparent = curToken;
                        next();
                        Token semicn = curToken;
                        next();
                        return new StmtGet(lVal, assign, getTk, lparent, rparent, semicn);
                    } else {
                        Exp exp = parseExp();
                        Token semicn = curToken;
                        next();
                        return new StmtAssign(lVal, assign, exp, semicn);
                    }

                } else if (type == TokenType.SEMICN) {
                    Exp exp = null;
                    if (curToken.getType() != TokenType.SEMICN) {
                        exp = parseExp();
                    }
                    Token semicn = curToken;
                    next();
                    return new StmtExp(exp, semicn);
                }
            }
        }
        return null;
    }


    private ForStmt parseForStmt() {
        LVal lVal = parseLVal();
        Token assign = curToken;
        next();
        Exp exp = parseExp();
        return new ForStmt(lVal, assign, exp);
    }

    private Exp parseExp() {
        return new Exp(parseAddExp());
    }

    private Cond parseCond() {
        return new Cond(parseLOrExp());
    }

    private LVal parseLVal() {
        Token ident = curToken;
        next();
        if (curToken.getType() == TokenType.LBRACK) {
            Token lbr = curToken;
            next();
            Exp exp = parseExp();
            Token rbr = curToken;
            next();
            return new LVal(ident, lbr, exp, rbr);
        }
        return new LVal(ident);
    }

    private PrimaryExp parsePrimaryExp() {
        if (curToken.getType() == TokenType.LPARENT) {
            Token lparent = curToken;
            next();
            Exp exp = parseExp();
            Token rparent = curToken;
            next();
            return new PrimaryExp(lparent, exp, rparent);
        } else if (curToken.getType() == TokenType.INTCON) {
            return new PrimaryExp(parseNumber());
        } else if (curToken.getType() == TokenType.CHRCON) {
            return new PrimaryExp(parseCharacter());
        } else {
            return new PrimaryExp(parseLVal());
        }
    }

    private Number parseNumber() {
        Token intConst = curToken;
        next();
        return new Number(intConst);
    }

    private Character parseCharacter() {
        Token charConst = curToken;
        next();
        return new Character(charConst);
    }

    private UnaryExp parseUnaryExp() {
        if (curToken.getType() == TokenType.PLUS || curToken.getType() == TokenType.MINU || curToken.getType() == TokenType.NOT) {
            UnaryOp op = parseUnaryOp();
            UnaryExp exp = parseUnaryExp();
            return new UnaryExp(op, exp);
        } else if (curToken.getType() == TokenType.IDENFR && preRead(1).getType() == TokenType.LPARENT) {
            Token ident = curToken;
            next();
            Token lparent = curToken;
            next();
            FuncRParams funcRParams = null;
            if (curToken.getType() != TokenType.RPARENT) {
                funcRParams = parseFuncRParams();
            }
            Token rparent = curToken;
            next();
            return new UnaryExp(ident, lparent, funcRParams, rparent);
        } else {
            return new UnaryExp(parsePrimaryExp());
        }
    }

    private UnaryOp parseUnaryOp() {
        Token op = curToken;
        next();
        return new UnaryOp(op);
    }

    private FuncRParams parseFuncRParams() {
        ArrayList<Exp> exps = new ArrayList<>();
        ArrayList<Token> commas = new ArrayList<>();
        exps.add(parseExp());
        while (curToken.getType() == TokenType.COMMA) {
            commas.add(curToken);
            next();
            exps.add(parseExp());
        }
        return new FuncRParams(exps, commas);
    }

    private MulExp parseMulExp() {
        UnaryExp unaryExp = parseUnaryExp();
        MulExp mulExp = new MulExp(unaryExp);
        while (curToken.getType() == TokenType.MULT || curToken.getType() == TokenType.DIV || curToken.getType() == TokenType.MOD) {
            Token op = curToken;
            next();
            unaryExp = parseUnaryExp();
            mulExp = new MulExp(mulExp, op, unaryExp); //对吗？？？？？
        }
        return mulExp;
    }

    private AddExp parseAddExp() {
        MulExp mulExp = parseMulExp();
        AddExp addExp = new AddExp(mulExp);
        while (curToken.getType() == TokenType.PLUS || curToken.getType() == TokenType.MINU) {
            Token op = curToken;
            next();
            mulExp = parseMulExp();
            addExp = new AddExp(addExp, op, mulExp);
        }
        return addExp;
    }

    private RelExp parseRelExp() {
        AddExp addExp = parseAddExp();
        RelExp relExp = new RelExp(addExp);
        while (curToken.getType() == TokenType.LSS || curToken.getType() == TokenType.GRE || curToken.getType() == TokenType.LEQ || curToken.getType() == TokenType.GEQ) {
            Token op = curToken;
            next();
            addExp = parseAddExp();
            relExp = new RelExp(relExp, op, addExp);
        }
        return relExp;
    }

    private EqExp parseEqExp() {
        RelExp relExp = parseRelExp();
        EqExp eqExp = new EqExp(relExp);
        while (curToken.getType() == TokenType.EQL || curToken.getType() == TokenType.NEQ) {
            Token op = curToken;
            next();
            relExp = parseRelExp();
            eqExp = new EqExp(eqExp, op, relExp);
        }
        return eqExp;
    }

    private LAndExp parseLAndExp() {
        EqExp eqExp = parseEqExp();
        LAndExp lAndExp = new LAndExp(eqExp);
        while (curToken.getType() == TokenType.AND) {
            Token op = curToken;
            next();
            eqExp = parseEqExp();
            lAndExp = new LAndExp(lAndExp, op, eqExp);
        }
        return lAndExp;
    }

    private LOrExp parseLOrExp() {
        LAndExp lAndExp = parseLAndExp();
        LOrExp lOrExp = new LOrExp(lAndExp);
        while (curToken.getType() == TokenType.OR) {
            Token op = curToken;
            next();
            lAndExp = parseLAndExp();
            lOrExp = new LOrExp(lOrExp, op, lAndExp);
        }
        return lOrExp;
    }

    private ConstExp parseConstExp() {
        return new ConstExp(parseAddExp());
    }
}
