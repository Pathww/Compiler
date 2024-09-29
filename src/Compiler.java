import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import AST.CompUnit;
import Lexer.*;
import Error.*;

public class Compiler {
    public static void main(String[] args) throws IOException {
        InputStream input = new FileInputStream("testfile.txt");
        Scanner scanner = new Scanner(input);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine()).append('\n');
        }
        input.close();

        ErrorHandler errorHandler = new ErrorHandler();

        Lexer lexer = new Lexer(sb.toString(), errorHandler);
        ArrayList<Token> tokens = lexer.analyse();

        Parser parser = new Parser(tokens, errorHandler);
        CompUnit compUnit = parser.parseCompUnit();

        FileWriter fw = new FileWriter("parser.txt");
        fw.write(compUnit.toString());
        fw.close();

        FileWriter error = new FileWriter("error.txt");
        error.write(errorHandler.toString());
        error.close();
    }
}