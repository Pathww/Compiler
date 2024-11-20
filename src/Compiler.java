import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import AST.*;
import LLVM.*;
import Lexer.*;
import Error.*;
import MIPS.*;
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

//        try {
        compUnit.buildIR();
//        } catch (Exception e) {
//            StackTraceElement stackTraceElement = e.getStackTrace()[0];
//            System.out.println(stackTraceElement.getClassName());
//            if (stackTraceElement.getClassName().equals("Symbol.Symbol")) {
//
//            } else {
//                e.printStackTrace();
//            }
//        }

        IRBuilder.module.allocName();
        FileWriter fw = new FileWriter("llvm_ir.txt");
        fw.write(IRBuilder.module.toString());
//        fw.write("define i32 @main() {\n" +
//                "\tret i32 0\n"
//                "}");
        fw.close();

//        try {
        IRBuilder.module.buildMips();
//        } catch (Exception e) {
//            /// getAllocedReg
//            StackTraceElement stackTraceElement = e.getStackTrace()[2];
//            System.out.println(stackTraceElement.getMethodName());
//            System.out.println(stackTraceElement.getClassName());
//            if (stackTraceElement.getClassName().equals("LLVM.Instr.CallInst")) {
//                System.out.println("store load");
//                e.printStackTrace();
//            } else {
//                e.printStackTrace();
//            }

//            if (e instanceof NullPointerException ) {
//                System.out.println("null pointer");
//            }else {
//                e.printStackTrace();
//            }
//        }

        fw = new FileWriter("mips.txt");
        fw.write(MipsBuilder.module.toString());
        fw.close();
    }
}