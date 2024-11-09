package LLVM.Instr;

public enum InstrType {
    ADD("add"),
    SUB("sub"),
    MUL("mul"),
    SDIV("sdiv"),
    SREM("srem"),
    ICMP("icmp"),
    AND("and"),
    OR("or"),
    CALL("call"),
    ALLOCA("alloca"),
    LOAD("load"),
    STORE("store"),
    GEP("getelementptr"),
    PHI("phi"),
    ZEXT("zext"),
    TRUNC("trunc"),
    BR("br"),
    RET("ret"),

    EQ("icmp eq"), NE("icmp ne"),
    SLT("icmp slt"), SGT("icmp sgt"),
    SLE("icmp sle"), SGE("icmp sge");

    private String value;

    InstrType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
