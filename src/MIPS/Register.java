package MIPS;

import java.util.HashMap;

public class Register extends Operand {
    public static HashMap<Integer, Register> regs = new HashMap<>();
    public int no;

    public static Register zero = new Register("$0", 0);
    public static Register at = new Register("$at", 1);
    public static Register v0 = new Register("$v0", 2);
    public static Register v1 = new Register("$v1", 3);
    public static Register a0 = new Register("$a0", 4);
    public static Register a1 = new Register("$a1", 5);
    public static Register a2 = new Register("$a2", 6);
    public static Register a3 = new Register("$a3", 7);
    public static Register t0 = new Register("$t0", 8);
    public static Register t1 = new Register("$t1", 9);
    public static Register t2 = new Register("$t2", 10);
    public static Register t3 = new Register("$t3", 11);
    public static Register t4 = new Register("$t4", 12);
    public static Register t5 = new Register("$t5", 13);
    public static Register t6 = new Register("$t6", 14);
    public static Register t7 = new Register("$t7", 15);
    public static Register s0 = new Register("$s0", 16);
    public static Register s1 = new Register("$s1", 17);
    public static Register s2 = new Register("$s2", 18);
    public static Register s3 = new Register("$s3", 19);
    public static Register s4 = new Register("$s4", 20);
    public static Register s5 = new Register("$s5", 21);
    public static Register s6 = new Register("$s6", 22);
    public static Register s7 = new Register("$s7", 23);
    public static Register t8 = new Register("$t8", 24);
    public static Register t9 = new Register("$t9", 25);
    public static Register k0 = new Register("$k0", 26);
    public static Register k1 = new Register("$k1", 27);
    public static Register gp = new Register("$gp", 28);
    public static Register sp = new Register("$sp", 29);
    public static Register fp = new Register("$fp", 30);
    public static Register ra = new Register("$ra", 31);

    private Register(String name, int no) {
        super(name);
        this.no = no;
    }

    static {
        regs.put(0, zero);
        regs.put(1, at);
        regs.put(2, v0);
        regs.put(3, v1);
        regs.put(4, a0);
        regs.put(5, a1);
        regs.put(6, a2);
        regs.put(7, a3);
        regs.put(8, t0);
        regs.put(9, t1);
        regs.put(10, t2);
        regs.put(11, t3);
        regs.put(12, t4);
        regs.put(13, t5);
        regs.put(14, t6);
        regs.put(15, t7);
        regs.put(16, s0);
        regs.put(17, s1);
        regs.put(18, s2);
        regs.put(19, s3);
        regs.put(20, s4);
        regs.put(21, s5);
        regs.put(22, s6);
        regs.put(23, s7);
        regs.put(24, t8);
        regs.put(25, t9);
        regs.put(26, k0);
        regs.put(27, k1);
        regs.put(28, gp);
        regs.put(29, sp);
        regs.put(30, fp);
        regs.put(31, ra);
    }

    public static Register get(int index) {
        return regs.get(index);
    }
}
