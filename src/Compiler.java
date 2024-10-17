import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import AST.CompUnit;
import Lexer.*;
import Error.*;
import Symbol.*;

public class Compiler {
    public static void main(String[] args) throws IOException {
        InputStream input = new FileInputStream("testfile.txt");
        Scanner scanner = new Scanner(input);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine()).append('\n');
        }
        input.close();

        Lexer lexer = new Lexer(sb.toString());
        ArrayList<Token> tokens = lexer.analyse();

        Parser parser = new Parser(tokens);
        CompUnit compUnit = parser.parseCompUnit();

        SymbolTable table = new SymbolTable(null);
        compUnit.toSymbol(table);

        FileWriter fw = new FileWriter("symbol.txt");
        fw.write(table.toString());
        fw.close();

        ErrorHandler errorHandler = new ErrorHandler();
        FileWriter error = new FileWriter("error.txt");
        error.write(errorHandler.toString());
        error.close();
    }
}