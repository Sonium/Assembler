import java.io.IOException;
import java.util.HashMap;
import java.lang.Math;

public class SixLixAssembler extends Assembler {

  public HashMap<String, Long> labelMap;
  public HashMap<String, String> regMap;

  public SixLixAssembler(String[] args) throws IOException {
    super(args);
    labelMap = new HashMap<String, Long>();
    regMap = new HashMap<String, String>();
  }

  @Override
  void processLabel(String sourceCode) {
    labelMap.put(sourceCode, programCounter);
  }

  private void printOpErr(Instruction instr) {
    System.err.println("Error: Invalid operand at pc " + programCounter);
    instr.print();
    System.out.flush();
    System.exit(1);
  }

  private void printRangeErr(Instruction instr, int reqSize, int actSize) {
    System.err.println("Error: Immedate out of range at pc " + programCounter);
    System.err.println("Immediate should fit in " + reqSize
        + " bits. Is actually " + actSize);
    instr.print();
    System.out.flush();
    System.exit(1);
  }

  private String numToStr(Instruction instr, Long num, int size) {
    String numStr = Long.toBinaryString(Math.abs(num));
    if (numStr.length() > size) {
      printRangeErr(instr, size, numStr.length());
    } else if (numStr.length() < size) {
      if (num < 0) {
        numStr = Long.toBinaryString(num);
        int len = numStr.length();
        numStr = numStr.substring(len - size, len);
      } else {
        int len = numStr.length();
        for (int i = 0; i < size - len; i++) {
          numStr = "0" + numStr;
        }
      }
    }
    return numStr;
  }

  private String getTemp(Instruction instr, int num) {
    if (num == 0)
      return "00";
    if (num == 1)
      return "01";
    if (num == 2)
      return "10";
    if (num == 3)
      return "11";
    System.err.println("Error: Temp or offset out of range at pc "
        + programCounter);
    instr.print();
    System.out.flush();
    System.exit(1);
    return "uh.... god, whut? fucking errors EVERYWHERWER";
  }

  @Override
  String generateCode(Instruction instruction) {
    // TODO
    // ADD COMMENTS
    String code = null;
    String op = instruction.operator;
    Operand[] ops = instruction.operands;
    if (op.equals("addi")) {
      if (!getOperandType(ops[0].name).equals("register")/*
                                                          * ||
                                                          * !getOperandType(ops[
                                                          * 1].
                                                          * name).equals("immediate"
                                                          * )
                                                          */) {
        printOpErr(instruction);
      }
      Long imm = Long.decode(ops[1].name);
      String immStr = numToStr(instruction, imm, 6);
      code = "1110" + getReg(instruction, ops[0].name) + immStr;
    } else if (op.equals("bne")) {
      if (!getOperandType(ops[0].name).equals("register")) {
        printOpErr(instruction);
      }
      Long imm = Long.decode(ops[1].name);
      String immStr = numToStr(instruction, imm, 6);
      code = "1110" + getReg(instruction, ops[0].name) + immStr;
    } else if (op.equals("blt")) {
      if (!getOperandType(ops[0].name).equals("register")) {
        printOpErr(instruction);
      }
      Long imm = Long.decode(ops[1].name);
      String immStr = numToStr(instruction, imm, 6);
      code = "1110" + getReg(instruction, ops[0].name) + immStr;
    } else if (op.equals("sra")) {
      if (!getOperandType(ops[0].name).equals("register")) {
        printOpErr(instruction);
      }
      Long imm = Long.decode(ops[1].name);
      String immStr = numToStr(instruction, imm, 4);
      code = "1001" + getReg(instruction, ops[0].name) + immStr + "01";
    } else if (op.equals("jor")) {
      if (!getOperandType(ops[0].name).equals("register")) {
        printOpErr(instruction);
      }
      Long imm = Long.decode(ops[1].name);
      String immStr = numToStr(instruction, imm, 4);
      code = "1001" + getReg(instruction, ops[0].name) + immStr + "11";
    } else if (op.equals("jr")) {
      if (!getOperandType(ops[0].name).equals("register")) {
        printOpErr(instruction);
      }
      code = "1011" + getReg(instruction, ops[0].name) + "000000";
    } else if (op.equals("lw")) {
      if (!getOperandType(ops[0].name).equals("register")
          || !getOperandType(ops[1].name).equals("register")) {
        printOpErr(instruction);
      }
      int num = ops[1].offset;
      String tempReg = getTemp(instruction, num);
      code = "0110" + getReg(instruction, ops[0].name) + getReg(instruction, ops[1].name)
          + tempReg;
    } else if (op.equals("sw")) {
      if (!getOperandType(ops[0].name).equals("register")
          || !getOperandType(ops[1].name).equals("register")) {
        printOpErr(instruction);
      }
      int num = ops[1].offset;
      String tempReg = getTemp(instruction, num);
      code = "0111" + getReg(instruction, ops[0].name) + getReg(instruction, ops[1].name)
          + tempReg;
    } else if (op.equals("lix")) {
      if (!getOperandType(ops[0].name).equals("register")
          || !getOperandType(ops[1].name).equals("register")
          || !getOperandType(ops[2].name).equals("label")) {
        printOpErr(instruction);
      }
      int num = Integer.valueOf(ops[2].name);
      String tempReg = getTemp(instruction, num);
      code = "0100" + getReg(instruction, ops[0].name) + getReg(instruction, ops[1].name)
          + tempReg;
    } else if (op.equals("six")) {
      if (!getOperandType(ops[0].name).equals("register")
          || !getOperandType(ops[1].name).equals("register")
          || !getOperandType(ops[2].name).equals("label")) {
        printOpErr(instruction);
      }
      int num = Integer.valueOf(ops[2].name);
      String tempReg = getTemp(instruction, num);
      code = "0101" + getReg(instruction, ops[0].name) + getReg(instruction, ops[1].name)
          + tempReg;
    } else if (op.equals("add")) {
      if (!getOperandType(ops[0].name).equals("register")
          || !getOperandType(ops[1].name).equals("register")) {
        printOpErr(instruction);
      }
      code = "0000" + getReg(instruction, ops[0].name) + getReg(instruction, ops[1].name) + "00";
    } else if (op.equals("sub")) {
      if (!getOperandType(ops[0].name).equals("register")
          || !getOperandType(ops[1].name).equals("register")) {
        printOpErr(instruction);
      }
      code = "0000" + getReg(instruction, ops[0].name) + getReg(instruction, ops[1].name) + "01";
    } else if (op.equals("nor")) {
      if (!getOperandType(ops[0].name).equals("register")
          || !getOperandType(ops[1].name).equals("register")) {
        printOpErr(instruction);
      }
      code = "0000" + getReg(instruction, ops[0].name) + getReg(instruction, ops[1].name) + "10";
    } else if (op.equals("mv")) {
      if (!getOperandType(ops[0].name).equals("register")
          || !getOperandType(ops[1].name).equals("register")) {
        printOpErr(instruction);
      }
      code = "0000" + getReg(instruction, ops[0].name) + getReg(instruction, ops[1].name) + "11";
    } else if (op.equals("in")) {
      if (!getOperandType(ops[0].name).equals("register")
          || !getOperandType(ops[1].name).equals("register")) {
        printOpErr(instruction);
      }
      code = "0001" + getReg(instruction, ops[0].name) + getReg(instruction, ops[1].name) + "00";
    } else if (op.equals("out")) {
      if (!getOperandType(ops[0].name).equals("register")
          || !getOperandType(ops[1].name).equals("register")) {
        printOpErr(instruction);
      }
      code = "0001" + getReg(instruction, ops[0].name) + getReg(instruction, ops[1].name) + "01";
    } else if (op.equals("j")) {
      Long imm = Long.decode(ops[0].name);
      String immStr = numToStr(instruction, imm, 9);
      code = "1000" + immStr + "0";
    } else if (op.equals("jal")) {
      Long imm = Long.decode(ops[0].name);
      String immStr = numToStr(instruction, imm, 9);
      code = "1000" + immStr + "1";
    } else if (op.equals("sloi")) {
      Long imm = Long.decode(ops[0].name);
      String immStr = numToStr(instruction, imm, 10);
      code = "1010" + immStr;
    } else if (op.equals("halt")) {
      code = "1111" + "0000000000";
    } else if (op.equals("li")) {
      // long num = Long.parseLong(instruction.operands[0].name, 16);
      int reqCodeLen = 0;
      int numInst = 0;
      long num = Long.decode(instruction.operands[0].name);
      String numBits = numToStr(instruction, num, 34);
      if (num < 1024) {
        reqCodeLen = 14 + 14 + 2;
        code = "0000" + getReg(instruction, "$v0") + getReg(instruction, "$0") + "11" + ",\n";
        code += "1010" + numBits.substring(24, 34);
        numInst = 2;
      } else if (num < 1024 * 1024) {
        reqCodeLen = 14 + 14 + 14 + 2 + 2;
        code = "0000" + getReg(instruction, "$v0") + getReg(instruction, "$0") + "11" + ",\n";
        code += "1010" + numBits.substring(14, 24) + ",\n";
        code += "1010" + numBits.substring(24, 34);
        numInst = 3;
      } else {
        reqCodeLen = 14 + 14 + 14 + 14 + 2 + 2 + 2; // Check for correct
                                                    // size, convert to
                                                    // bitfield
        code = "1010" + "000000" + numBits.substring(0, 4) + ",\n";
        code += "1010" + numBits.substring(4, 14) + ",\n";
        code += "1010" + numBits.substring(14, 24) + ",\n";
        code += "1010" + numBits.substring(24, 34);
        numInst = 4;
      }
      if (code.length() != reqCodeLen) {
        System.err.println("Error processing instruction at pc "
            + programCounter);
        System.err.println("Machine code is " + code.length()
            + " bits but should be " + reqCodeLen + " bits");
        instruction.print();
        System.out.flush();
        System.exit(1);
      }
      programCounter += numInst;
      return code;
    } else if (op.equals("la")) {
      Long num = Long.decode(ops[0].name);
      String numBits = numToStr(instruction, num, 34); // Check for correct
                                                       // size, convert to
                                                       // bitfield
      code = "1010" + "000000" + numBits.substring(0, 4) + ",\n";
      /* numToStr(instruction, (num>>30)%1024, 10) */
      code += "1010" + numBits.substring(4, 14) + ",\n";
      /* numToStr(instruction, (num>>20)%1024, 10) */
      code += "1010" + numBits.substring(14, 24) + ",\n";
      /* numToStr(instruction, (num>>10)%1024, 10) */
      code += "1010" + numBits.substring(24, 34);
      /* numToStr(instruction, num%1024, 10) */
      if (code.length() != 14 * 4 + 2 * 3) {
        System.err.println("Error processing instruction at pc "
            + programCounter);
        System.err.println("Machine code is " + code.length()
            + " bits but should be 34 bits");
        instruction.print();
        System.out.flush();
        System.exit(1);
      }
      programCounter += 4;
      return code;
    } else {
      System.err.println("Error unrecognized instruction at pc "
          + programCounter);
      instruction.print();
      System.out.flush();
      System.exit(1);
    }
    if (!isBinary(code)) {
      System.err
          .println("Error processing instruction at pc " + programCounter);
      System.err.println("Machine code is not binary. Machine code is: " + code);
      instruction.print();
      System.out.flush();
      System.exit(1);
    }
    if (code.length() != 14) {
      System.err
          .println("Error processing instruction at pc " + programCounter);
      System.err.println("Machine code is " + code.length()
          + " bits but should be 14 bits");
      instruction.print();
      System.out.flush();
      System.exit(1);
    }
    programCounter += 1;
    return code;
  }

  @Override
  void updateProgramCounter(Instruction instruction) {
    // la takes 4 sloi instructions
    if (instruction.operator.equals("la")) {
      programCounter += 4;
    }
    // dynamic pseudo instruction li varies depending on immediate size
    else if (instruction.operator.equals("li")) {
      // long num = Long.parseLong(instruction.operands[0].name, 16);
      long num = Long.decode(instruction.operands[0].name);
      if (num < 1024) {
        programCounter += 2; // zero out and a soli
      } else if (num < 1024 * 1024) {
        programCounter += 3; // zero out and 2 soli
      } else {
        programCounter += 4; // zero out or 4 solis
      }
    } else {
      programCounter += 1;
    }
  }

  @Override
  void initialization() throws IOException {
    regMap.put("$0", "0000");
    regMap.put("$s0", "0001");
    regMap.put("$s1", "0010");
    regMap.put("$s2", "0011");
    regMap.put("$s3", "0100");
    regMap.put("$s4", "0101");
    regMap.put("$t0", "0110");
    regMap.put("$t1", "0111");
    regMap.put("$t2", "1000");
    regMap.put("$t3", "1001");
    regMap.put("$a0", "1010");
    regMap.put("$a1", "1011");
    regMap.put("$v0", "1100");
    regMap.put("$v1", "1101");
    regMap.put("$sp", "1110");
    regMap.put("$ra", "1111");
  }
  
  private String getReg(Instruction instr, String reg){
    String res = regMap.get(reg);
    if(res == null){
      System.err.println("Error looking up register: " + reg + " at PC " + programCounter);
      instr.print();
      System.out.flush();
      System.exit(1);
    }
    return res;
  }

 private boolean isBinary(String s) {
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) != '0' && s.charAt(i) != '1') {
        return false;
      }
    }
    return true;
  }

  @Override
  void replaceInstructionLabel(Instruction instruction) {

    if (instruction.operator.equals("jal") || instruction.operator.equals("j")) {
      // Check that operand is label
      if (!getOperandType(instruction.operands[0].name).equals("label")) {
        printOpErr(instruction);
      }
      // Get address of label from map
      Long addr = labelMap.get(instruction.operands[0].name);
      // Check label exists
      if (addr == null) {
        System.err.println("No label named" + instruction.operands[0].name
            + "\n With pc value: " + programCounter);
        System.exit(1);
      }
      long offset = addr - programCounter;
      // Check for overflow
      if (offset > 127 || offset < -128) {
        System.err.println("Offset Overflow for label: "
            + instruction.operands[0].name + "\n With pc value: "
            + programCounter);
        System.exit(1);
      }
      // subtract programCounter from addr to get offset for jump
      instruction.operands[0].name = String.valueOf(offset);
    }

    else if (instruction.operator.equals("bne")
        || instruction.operator.equals("blt")) {
      // Check that operand is label
      if (!getOperandType(instruction.operands[1].name).equals("label")) {
        printOpErr(instruction);
      }
      // Get address of label from map
      Long addr = labelMap.get(instruction.operands[1].name);
      // Check label exists
      if (addr == null) {
        System.err.println("No label named" + instruction.operands[1].name
            + "\n With pc value: " + programCounter);
        System.exit(1);
      }
      long offset = addr - programCounter;
      // Check for overflow
      if (offset > 31 || offset < -32) {
        System.err.println("Offset Overflow for label: "
            + instruction.operands[1].name + "\n With pc value: "
            + programCounter);
        System.exit(1);
      }

      // subtract programCounter from addr to get offset for jump
      instruction.operands[1].name = String.valueOf(offset);
    }
    else if (instruction.operator.equals("la")) {
      // Check that operand is label
      if (!getOperandType(instruction.operands[0].name).equals("label")) {
        printOpErr(instruction);
      }
      // Get address of label from map
      Long addr = labelMap.get(instruction.operands[0].name);
      // Check label exists
      if (addr == null) {
        System.err.println("No label named" + instruction.operands[0].name
            + "\n With pc value: " + programCounter);
        System.exit(1);
      }
      // subtract programCounter from addr to get offset for jump
      instruction.operands[0].name = String.valueOf(addr);
    }
  }

  @Override
  void replaceMemoryLabel() {

  }

  public static void main(String[] arg) throws IOException {
    SixLixAssembler assembler = new SixLixAssembler(arg);
    assembler.AssembleCode(arg);
  }

}
