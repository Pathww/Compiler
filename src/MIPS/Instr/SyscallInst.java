package MIPS.Instr;

public class SyscallInst extends Instruction {
    private MipsInstrType type;
    private int no;

    public SyscallInst(int no) {
        type = MipsInstrType.SYSCALL;
        this.no = no;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("li $v0, %d\n", no));
        sb.append("\tsyscall\n");
        return sb.toString();
    }

    public MipsInstrType getInstrType() {
        return type;
    }
}
