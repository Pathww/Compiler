import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import AST.*;
import LLVM.*;
import Lexer.*;
import Error.*;
import MIPS.*;
import Optimizer.*;
import Parser.*;
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

        ErrorHandler errorHandler = new ErrorHandler();
        if (!errorHandler.isEmpty()) {
            FileWriter error = new FileWriter("error.txt");
            error.write(errorHandler.toString());
            error.close();
            return;
        }

        compUnit.buildIR();
        Optimizer optimizer = new Optimizer();

//        IRBuilder.module.allocName();
        FileWriter fw = new FileWriter("llvm_ir.txt");
        fw.write(IRBuilder.module.toString());
        fw.close();

        IRBuilder.module.buildMips();

        fw = new FileWriter("mips.txt");
        fw.write(MipsBuilder.module.toString());
        fw.close();
    }
}