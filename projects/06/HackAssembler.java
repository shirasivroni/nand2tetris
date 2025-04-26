import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HackAssembler {
    final File inputFile;
    final File outputFile;
    final Code code;
    final SymbolTable symbolTable;
    private BufferedWriter bufferedWriter;
    private int countLine = 0;
    public int firstEmptyCell = 16;

    public HackAssembler(File file) {
        this.inputFile = file;
        this.outputFile = new File(file.getParent(), file.getName().replace("asm", "hack"));
        this.code = new Code();
        this.bufferedWriter = null;
        this.symbolTable = new SymbolTable();
    }

    public static String stringToBinary(int num) {
        String result = Integer.toBinaryString(num);
        return String.format("%16s", result).replace(' ', '0');
    }

    public void parse() throws IOException {
        Parser parser = new Parser(inputFile);
        try {
            FileWriter fileWriter = new FileWriter(outputFile.getAbsoluteFile());
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            throw new Error("Error");
        }

        while (parser.hasMoreLines()) {
            if (parser.instructionType() == InstructionType.L_INSTRUCTION) {
                symbolTable.addEntry(parser.symbol(), countLine);
            } else {
                this.countLine++;
            }
            parser.advance();
        }
        parser = new Parser(this.inputFile);
        String currSymbol = "";
        String binary = "";
        while (parser.hasMoreLines()) {
            if (parser.instructionType() == InstructionType.A_INSTRUCTION) {
                currSymbol = parser.symbol();
                boolean isNumeric = currSymbol.chars().allMatch(Character::isDigit);
                if (!isNumeric) {
                    if (!symbolTable.contains(currSymbol)) {
                        symbolTable.addEntry(currSymbol, this.firstEmptyCell);
                        this.firstEmptyCell++;

                    }
                    binary = stringToBinary(symbolTable.getAddress(currSymbol));

                    try {
                        bufferedWriter.write(binary);
                        bufferedWriter.newLine();
                    } catch (IOException e) {
                        throw new Error("Can't write to file");
                    }
                } else {
                    binary = stringToBinary(Integer.parseInt(currSymbol));

                    try {
                        bufferedWriter.write(binary);
                        bufferedWriter.newLine();
                    } catch (IOException e) {
                        throw new Error("Can't write to file");
                    }
                }
            }

            if (parser.instructionType() == InstructionType.C_INSTRUCTION){
                String destB = "000";
                String compB = "0101010";
                String jumpB = "000";

                if(parser.comp() != null){
                    compB = code.comp(parser.comp());
                }
                if(parser.dest() != null){
                    destB = code.dest(parser.dest());
                }
                if(parser.jump() != null){
                    jumpB = code.jump(parser.jump());
                }
                binary = "111" + compB + destB + jumpB;
                try {
                    bufferedWriter.write(binary);
                    bufferedWriter.newLine();
                } catch (IOException e) {
                    throw new Error("Can't write to file");
                }
            }

            parser.advance();
        }
        try{
            bufferedWriter.close();
        } catch (IOException e){
            throw new Error("Can't write to file");

        }
    }

}


