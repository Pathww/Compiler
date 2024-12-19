package MIPS.Instr;

public enum MipsInstrType {
    ADD("add"),
    ADDU("addu"),
    ADDIU("addiu"),
    SUBU("subu"),
    MULT("mult"),
    DIV("div"),
    REM("div"),
    AND("and"),
    OR("or"),
    XORI("xori"),
    SLL("sll"),
    SRA("sra"),
    SRL("srl"),
    MULSH("mulsh"),

    LI("li"),
    LA("la"),
    LW("lw"),
    SW("sw"),

    MOVE("move"),

    SYSCALL("syscall"),

    BEQ("beq"),
    J("j"),
    JR("jr"),
    JAL("jal"),

    SLTI("slti"),
    SEQ("seq"), SNE("sne"),
    SLT("slt"), SGT("sgt"),
    SLE("sle"), SGE("sge");

    private String value;

    MipsInstrType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
