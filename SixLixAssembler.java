import java.io.IOException;


public class SixLixAssembler extends Assembler {

  public SixLixAssembler(String[] args) throws IOException {
    super(args);
    // TODO Auto-generated constructor stub
  }

  @Override
  void processLabel(String sourceCode) {
    // TODO Auto-generated method stub

  }

  @Override
  String generateCode(Instruction instruction) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  void updateProgramCounter(Instruction instruction) {
    if (instruction.operator.equals("la")) {
      programCounter += 4;
    }
    else if (instruction.operator.equals("li")) {
     long num = Integer.parseInt(instruction.operands[0].name, 16);
     if (num < 1024) {
       programCounter += 2;
     }
     else if (num < 1024*1024) {
       programCounter += 3;
     }
     else {
       programCounter += 4;
     }
    }

  }

  @Override
  void initialization() throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  void replaceInstructionLabel(Instruction instruction) {
    // TODO Auto-generated method stub

  }

  @Override
  void replaceMemoryLabel() {
    // TODO Auto-generated method stub

  }
  
  public static void main(String[] arg) throws IOException
  {
      SixLixAssembler assembler = new SixLixAssembler(arg);
      assembler.AssembleCode(arg); 
  }

}
