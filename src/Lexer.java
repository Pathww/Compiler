import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    private String input;
    private int length;
    private int pos = 0;
    private int line = 1;
    private HashMap<String, TokenType> keywords = new HashMap<>();
    private ArrayList<Error> errors;

    public Lexer(String input, ArrayList<Error> errors) {
        initKeywords();
        this.input = input;
        this.length = input.length() - 1; // \n
        this.errors = errors;
    }

    public Token next() {
        for (; pos < length; pos++) {
            char ch = input.charAt(pos);
            if (isAlpha(ch) || ch == '_') {
                StringBuilder sb = new StringBuilder();
                while (isAlpha(ch) || isDigit(ch) || ch == '_') {
                    sb.append(ch);
                    pos++;
                    if (pos >= length) {
                        break;
                    }
                    ch = input.charAt(pos);
                }
                return new Token(keywords.getOrDefault(sb.toString(), TokenType.IDENFR), sb.toString(), line);
            } else if (isDigit(ch)) {
                StringBuilder sb = new StringBuilder();
                while (isDigit(ch)) {
                    sb.append(ch);
                    pos++;
                    if (pos >= input.length()) {
                        break;
                    }
                    ch = input.charAt(pos);
                }
                return new Token(TokenType.INTCON, sb.toString(), line);
            } else if (ch == '\"') {
                StringBuilder sb = new StringBuilder(); // null ""
                sb.append('\"');
                pos++;
                if (pos >= length) {
                    break;
                }
                ch = input.charAt(pos); // report error ?
                while (isChar(ch)) {
                    sb.append(ch);
                    pos++;
                    if (ch == '\"') {
                        break;
                    }
                    if (pos >= input.length()) {
                        break;
                    }
                    ch = input.charAt(pos);
                }
                return new Token(TokenType.STRCON, sb.toString(), line);
            } else if (ch == '\'') {
                StringBuilder sb = new StringBuilder();
                sb.append('\'');
                pos++;
                if (pos >= length) {
                    break;
                }
                ch = input.charAt(pos);
                sb.append(ch);
                if (ch == '\\') {
                    pos++;
                    if (pos >= length) {
                        break;
                    }
                    ch = input.charAt(pos);
                    sb.append(ch);
                }
                pos += 2; // report error ?
                sb.append('\'');
                return new Token(TokenType.CHRCON, sb.toString(), line);
            } else if (ch == '!') {
                pos++;
                if (pos < length && input.charAt(pos) == '=') {
                    pos++;
                    return new Token(TokenType.NEQ, "!=", line);
                } else {
                    return new Token(TokenType.NOT, "!", line);
                }
            } else if (ch == '<') {
                pos++;
                if (pos < length && input.charAt(pos) == '=') {
                    pos++;
                    return new Token(TokenType.LEQ, "<=", line);
                } else {
                    return new Token(TokenType.LSS, "<", line);
                }
            } else if (ch == '>') {
                pos++;
                if (pos < length && input.charAt(pos) == '=') {
                    pos++;
                    return new Token(TokenType.GEQ, ">=", line);
                } else {
                    return new Token(TokenType.GRE, ">", line);
                }
            } else if (ch == '=') {
                pos++;
                if (pos < length && input.charAt(pos) == '=') {
                    pos++;
                    return new Token(TokenType.EQL, "==", line);
                } else {
                    return new Token(TokenType.ASSIGN, "=", line);
                }
            } else if (ch == '&') {
                pos++;
                if (pos < length && input.charAt(pos) == '&') {
                    pos++;
                    return new Token(TokenType.AND, "&&", line);
                } else {
                    errors.add(new Error(line, "a"));
                    return new Token(TokenType.AND, "&", line);
                }
            } else if (ch == '|') {
                pos++;
                if (pos < length && input.charAt(pos) == '|') {
                    pos++;
                    return new Token(TokenType.OR, "||", line);
                } else {
                    errors.add(new Error(line, "a"));
                    return new Token(TokenType.OR, "|", line);
                }
            } else if (ch == '/') {
                pos++;
                if (pos < length && input.charAt(pos) == '/') {
                    while (pos < length && input.charAt(pos) != '\n') {
                        pos++;
                    }
                } else if (pos < length && input.charAt(pos) == '*') {
                    pos++;
                    for (; pos + 1 < length; pos++) {
                        if (input.charAt(pos) == '\n') {
                            line++;
                        }
                        if (input.charAt(pos) == '*' && input.charAt(pos + 1) == '/') {
                            pos++;
                            break;
                        }
                    }
                } else {
                    return new Token(TokenType.DIV, "/", line);
                }
            } else if (ch == '+') {
                pos++;
                return new Token(TokenType.PLUS, "+", line);
            } else if (ch == '-') {
                pos++;
                return new Token(TokenType.MINU, "-", line);
            } else if (ch == '*') {
                pos++;
                return new Token(TokenType.MULT, "*", line);
            } else if (ch == '%') {
                pos++;
                return new Token(TokenType.MOD, "%", line);
            } else if (ch == ';') {
                pos++;
                return new Token(TokenType.SEMICN, ";", line);
            } else if (ch == ',') {
                pos++;
                return new Token(TokenType.COMMA, ",", line);
            } else if (ch == '(') {
                pos++;
                return new Token(TokenType.LPARENT, "(", line);
            } else if (ch == ')') {
                pos++;
                return new Token(TokenType.RPARENT, ")", line);
            } else if (ch == '[') {
                pos++;
                return new Token(TokenType.LBRACK, "[", line);
            } else if (ch == ']') {
                pos++;
                return new Token(TokenType.RBRACK, "]", line);
            } else if (ch == '{') {
                pos++;
                return new Token(TokenType.LBRACE, "{", line);
            } else if (ch == '}') {
                pos++;
                return new Token(TokenType.RBRACE, "}", line);
            } else if (ch == '\n') {
                line++;
            }
        }
        return new Token(TokenType.EOFTK, "", line);
    }

    private void initKeywords() {
        keywords.put("main", TokenType.MAINTK);
        keywords.put("const", TokenType.CONSTTK);
        keywords.put("int", TokenType.INTTK);
        keywords.put("char", TokenType.CHARTK);
        keywords.put("break", TokenType.BREAKTK);
        keywords.put("continue", TokenType.CONTINUETK);
        keywords.put("if", TokenType.IFTK);
        keywords.put("else", TokenType.ELSETK);
        keywords.put("for", TokenType.FORTK);
        keywords.put("getint", TokenType.GETINTTK);
        keywords.put("getchar", TokenType.GETCHARTK);
        keywords.put("printf", TokenType.PRINTFTK);
        keywords.put("return", TokenType.RETURNTK);
        keywords.put("void", TokenType.VOIDTK);
    }

    private boolean isAlpha(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    private boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9');
    }

    private boolean isChar(char ch) {
        return ch == 0 || (ch >= 7 && ch <= 12) || (ch >= 32 && ch <= 126);
    }
}
