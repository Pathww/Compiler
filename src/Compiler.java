import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Compiler {
    public static void main(String[] args) throws IOException {
        InputStream input = new FileInputStream("testfile.txt");
        Scanner scanner = new Scanner(input);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine()).append('\n');
        }
        input.close();
        ArrayList<Error> errors = new ArrayList<>();
        Lexer lexer = new Lexer(sb.toString(), errors);
//        Parser parser = new Parser(lexer);
        FileWriter fw = new FileWriter("lexer.txt");
        while (true) {
            Token token = lexer.next();
            if (token.getType() == TokenType.EOFTK) {
                break;
            }
            fw.write(token.toString() + '\n');
        }
        fw.close();
        FileWriter error = new FileWriter("error.txt");
        for (Error e : errors) {
            error.write(e.toString() + '\n');
        }
        error.close();
    }
}