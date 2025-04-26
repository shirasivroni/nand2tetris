import java.io.*;

enum InstructionType{
    A_INSTRUCTION,
    C_INSTRUCTION,
    L_INSTRUCTION
}
public class Parser {
    final BufferedReader bufferedReader;
    private String currLine;

    public Parser(File inputFile) throws IOException {
        if (!inputFile.exists()){
            System.out.println("File not found");
            throw new FileNotFoundException((inputFile.getAbsolutePath()));
        }
        FileReader fileReader = new FileReader(inputFile);
        this.bufferedReader = new BufferedReader(fileReader);
        this.advance();
    }
    public boolean hasMoreLines() {
        return (this.currLine != null);
    }

    public void advance() {
        try {
            String nextLine = this.bufferedReader.readLine();
            while (nextLine != null && (nextLine.isBlank() || nextLine.contains("//"))) {
                nextLine = bufferedReader.readLine();
            }
            currLine = nextLine;
        } catch (IOException e) {
            throw new Error("Can not read file");

        }
    }

    public InstructionType instructionType() {
        String currInstruction = this.currLine.trim();
        if (currInstruction.startsWith("@")) {
            return InstructionType.A_INSTRUCTION;
        } else if (currInstruction.startsWith("(") && currInstruction.endsWith(")")) {
            return InstructionType.L_INSTRUCTION;
        } else {
            return InstructionType.C_INSTRUCTION;
        }
    }

    public String symbol() {
        InstructionType it = instructionType();
        if(it == InstructionType.C_INSTRUCTION){
            throw new Error("Given C instruction");
        }
        String currInstruction = this.currLine.trim();
        if(it == InstructionType.L_INSTRUCTION){
            return currInstruction.substring(1,currInstruction.length()-1);
        }

        return currInstruction.substring( 1);
    }

    public String dest(){
        InstructionType it = instructionType();
        String currInstruction = this.currLine.trim();
        if (it == InstructionType.C_INSTRUCTION){
            if (currInstruction.indexOf("=") != -1) {
                return currInstruction.substring(0, currInstruction.indexOf("="));
            }
            else {
                return "null";
            }
        }
        if(it == InstructionType.A_INSTRUCTION){
            return "Given A instruction";
        }
        return "Given L instruction";
    }

    public String comp(){
        InstructionType it = instructionType();
        String currInstruction = this.currLine.trim();
        if (it == InstructionType.C_INSTRUCTION){
            currInstruction =currInstruction.substring(currInstruction.indexOf("=")+1);
            int currIndex = currInstruction.indexOf(";");
            if (currIndex != -1){
                return currInstruction.substring(0,currIndex);
            } else {
                return currInstruction;
            }
        }

        if (it == InstructionType.L_INSTRUCTION){
            return "Given L instruction";
        }

        return "Given A instruction";
    }

    public String jump(){
        InstructionType it = instructionType();
        String currInstruction = this.currLine.trim();
        if (it == InstructionType.C_INSTRUCTION){
            int currIndex = currInstruction.indexOf(";");
            if (currIndex != -1){
                return currInstruction.substring(currIndex+1);
            } else {
                return "null";
            }
        }

        if(it == InstructionType.L_INSTRUCTION){
            return "Given A instruction";
        }
        return "Given L instruction";

    }
}
