import java.io.*;

public class CodeWriter {
    private final BufferedReader bufferedReader;
    private final File outputFile;
    private BufferedWriter bufferedWriter;
    private final Parser parser;
    private int currLine;
    private String fileName = "";
    private int jumpIndex = 0;

    public CodeWriter(File inputFile) throws IOException{
        if (!inputFile.exists()){
            throw new FileNotFoundException((inputFile.getAbsolutePath()));
        }
        try{
            FileReader fileReader = new FileReader(inputFile);
            this.bufferedReader = new BufferedReader(fileReader);
            this.outputFile = new File(inputFile.getParent(), inputFile.getName().replace("vm", "asm"));
            FileWriter fileWriter = new FileWriter(outputFile.getAbsoluteFile());
            this.bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e){
            throw new Error("cant read file or write to file");
        }
        this.currLine = 0;
        this.parser = new Parser(inputFile);
        this.fileName = inputFile.getName().split("\\.")[0];
        this.fileInit();
    }

    public void fileInit(){
        writeLineToFile("@256");
        writeLineToFile("D=A");
        writeLineToFile("@SP");
        writeLineToFile("M=D");
    }

    public void writeArithmetic(String command){
        writeLineToFile("//" + command);
        writeLineToFile("@SP");
        writeLineToFile("M=M-1");
        writeLineToFile("A=M");
        writeLineToFile("D=M");
        //writeLineToFile("A=A-1");

        if (command.trim().equals("add")){
            writeLineToFile("@SP");
            writeLineToFile("M=M-1");
            writeLineToFile("A=M");
            writeLineToFile("M=M+D");
        }
        else if (command.trim().equals("sub")){
            writeLineToFile("@SP");
            writeLineToFile("M=M-1");
            writeLineToFile("A=M");
            writeLineToFile("M=M-D");
        }
        else if (command.trim().equals("and")){
            writeLineToFile("@SP");
            writeLineToFile("M=M-1");
            writeLineToFile("A=M");
            writeLineToFile("M=M&D");
        }
        else if (command.trim().equals("or")){
            writeLineToFile("@SP");
            writeLineToFile("M=M-1");
            writeLineToFile("A=M");
            writeLineToFile("M=M|D");
        }
        else if (command.trim().equals("eq")){
            writeLineToFile("@SP");
            writeLineToFile("M=M-1");
            writeLineToFile("A=M");
            writeLineToFile("D=M-D");
            writeLineToFile("M=-1");
            writeLineToFile("@EQUAL");
            writeLineToFile("D;JEQ");
            writeLineToFile("@SP");
            writeLineToFile("A=M");
            writeLineToFile("M=0");
            writeLineToFile("(EQUAL)");
        }
        else if (command.trim().equals("gt")){
            writeLineToFile("@SP");
            writeLineToFile("M=M-1");
            writeLineToFile("A=M");
            writeLineToFile("D=M-D");
            writeLineToFile("M=-1");
            writeLineToFile("@GREATER");
            writeLineToFile("D;JLT");
            writeLineToFile("@SP");
            writeLineToFile("A=M");
            writeLineToFile("M=0");
            writeLineToFile("(GREATER)");
        }
        else if (command.trim().equals("lt")){
            writeLineToFile("@SP");
            writeLineToFile("M=M-1");
            writeLineToFile("A=M");
            writeLineToFile("D=M-D");
            writeLineToFile("M=-1");
            writeLineToFile("@LESS");
            writeLineToFile("D;JGT");
            writeLineToFile("@SP");
            writeLineToFile("A=M");
            writeLineToFile("M=0");
            writeLineToFile("(LESS)");
        }
        else if (command.trim().equals("neg")){
            writeLineToFile("M=-D");
        }
        else if (command.trim().equals("not")){
            writeLineToFile("M=!D");
        }
        writeLineToFile("@SP");
        writeLineToFile("M=M+1");
    }

    public void writePushPop(CommandType command, String segment, int index){
        writeLineToFile("// " + command + " " + segment + " " + index);
        if (command == CommandType.C_PUSH){
            if (segment.equals("local") || segment.equals("argument") || segment.equals("this") || segment.equals("that")){
                writeLineToFile(getLabel(segment));
                writeLineToFile("D=M");
                writeLineToFile("@" + index);
                writeLineToFile("D=D+A");
                writeLineToFile("@addr");
                writeLineToFile("M=D");
                writeLineToFile("A=M");
                writeLineToFile("D=M");
                writeLineToFile("@SP");
                writeLineToFile("A=M");
                writeLineToFile("M=D");
            }
            else if (segment.equals("temp")){
                writeLineToFile("@5");
                writeLineToFile("D=A");
                writeLineToFile("@" + index);
                writeLineToFile("D=D+A");
                writeLineToFile("@addr");
                writeLineToFile("M=D");
                writeLineToFile("A=M");
                writeLineToFile("D=M");
                writeLineToFile("@SP");
                writeLineToFile("A=M");
                writeLineToFile("M=D");
            }
            else if (segment.equals("constant")){
                writeLineToFile("@" + index);
                writeLineToFile("D=A");
                writeLineToFile("@SP");
                writeLineToFile("A=M");
                writeLineToFile("M=D");
            }
            else if (segment.equals("static")){
                writeLineToFile("@" + fileName + "." + index);
                writeLineToFile("D=M");
                writeLineToFile("@SP");
                writeLineToFile("A=M");
                writeLineToFile("M=D");
            }
            else if(segment.equals("pointer") && index == 0){
                writeLineToFile("@THIS");
                writeLineToFile("D=M");
                writeLineToFile("@SP");
                writeLineToFile("A=M");
                writeLineToFile("M=D");
            }
            else if(segment.equals("pointer") && index == 1){
                writeLineToFile("@THAT");
                writeLineToFile("D=M");
                writeLineToFile("@SP");
                writeLineToFile("A=M");
                writeLineToFile("M=D");
            }
            writeLineToFile("@SP");
            writeLineToFile("M=M+1");
        }
        else if (command == CommandType.C_POP){
            if (segment.equals("local") || segment.equals("argument") || segment.equals("this") || segment.equals("that")){
                writeLineToFile(getLabel(segment));
                writeLineToFile("D=M");
                writeLineToFile("@" + index);
                writeLineToFile("D=D+A");
                writeLineToFile("@addr");
                writeLineToFile("M=D");
                writeLineToFile("@SP");
                writeLineToFile("M=M-1");
                writeLineToFile("A=M");
                writeLineToFile("D=M");
                writeLineToFile("@addr");
                writeLineToFile("A=M");
                writeLineToFile("M=D");
            }
            else if (segment.equals("temp")){
                writeLineToFile("@5");
                writeLineToFile("D=A");
                writeLineToFile("@" + index);
                writeLineToFile("D=D+A");
                writeLineToFile("@addr");
                writeLineToFile("M=D");
                writeLineToFile("@SP");
                writeLineToFile("M=M-1");
                writeLineToFile("A=M");
                writeLineToFile("D=M");
                writeLineToFile("@addr");
                writeLineToFile("A=M");
                writeLineToFile("M=D");
            }
            else if (segment.equals("static")){
                writeLineToFile("@SP");
                writeLineToFile("M=M-1");
                writeLineToFile("A=M");
                writeLineToFile("D=M");
                writeLineToFile("@" + fileName + "." + index);
                writeLineToFile("M=D");
            }
            else if(segment.equals("pointer") && index == 0){
                writeLineToFile("@SP");
                writeLineToFile("M=M-1");
                writeLineToFile("A=M");
                writeLineToFile("D=M");
                writeLineToFile("@THIS");
                writeLineToFile("M=D");
            }
            else if(segment.equals("pointer") && index == 1){
                writeLineToFile("@SP");
                writeLineToFile("M=M-1");
                writeLineToFile("A=M");
                writeLineToFile("D=M");
                writeLineToFile("@THAT");
                writeLineToFile("M=D");
            }
        }
    }

    public void writeLineToFile(String line){
        if (line == null){
            return;
        }
        try {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            currLine++;
        } catch (IOException e) {
            throw new Error("Can't write to file");
        }
    }

    public String getLabel(String segment){
        if(segment.equals("local")){
            return "@LCL";
        }
        else if(segment.equals("argument")){
            return "@ARG";
        }
        else if(segment.equals("this")){
            return "@THIS";
        }
        else if(segment.equals("that")){
            return "@THAT";
        }
        else if(segment.equals("temp")){
            return "@R5";
        }
        else{
            return null;
        }
    }

    public void close(){
        try{
            bufferedWriter.close();
        } catch (IOException e){
            throw new Error("Can't write to file");
        }
    }

}


