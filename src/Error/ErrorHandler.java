package Error;

import java.util.ArrayList;

public class ErrorHandler {
    ArrayList<Error> errors;
    public ErrorHandler(){
        errors=new ArrayList<>();
    }
    public void addError(int line, ErrorType type){
        errors.add(new Error(line, type));
    }
    public ArrayList<Error> getErrors(){
        return errors;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Error e:errors){
            sb.append(e.toString());
        }
        return sb.toString();
    }
}
