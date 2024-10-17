package Symbol;

public class VarSymbol extends Symbol {
    // 变量的值：val，寄存器：reg
    public VarSymbol(String ident, SymbolType type) {
        super(ident, type);
    }
}