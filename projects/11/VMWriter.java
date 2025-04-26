import java.io.*;
enum Segment{
    CONSTANT,
    ARGUMENT,
    LOCAL,
    STATIC,
    THIS,
    THAT,
    POINTER,
    TEMP
}

enum Command{
    ADD,
    SUB,
    NEG,
    EQ,
    GT,
    LT,
    AND,
    OR,
    NOT
}
public class VMWriter {
    private final BufferedReader bufferedReader;
    private File outputFile;
    private final BufferedWriter bufferedWriter;
    public VMWriter(File inputFile) throws IOException{
        if(!inputFile.exists()){
            throw new FileNotFoundException((inputFile.getAbsolutePath()));
        }
        FileReader fileReader = new FileReader(inputFile);
        bufferedReader = new BufferedReader(fileReader);
        outputFile = new File(inputFile.getParent(), inputFile.getName().replace("jack", "vm"));
        FileWriter fileWriter = new FileWriter(outputFile);
        bufferedWriter = new BufferedWriter(fileWriter);
    }

    public void writePush(Segment segment, int index){
        writeLineToFile("push " + segment.toString() + " " + index);
    }

    public void writePop(Segment segment, int index){
        writeLineToFile("pop " + segment.toString() + " " + index);
    }

    public void writeArithmetic(Command command){
        writeLineToFile(command.toString());
    }

    public void writeLabel(String label){
        writeLineToFile("label " + label);
    }

    public void writeGoto(String label){
        writeLineToFile("goto " + label);
    }

    public void writeIf(String label){
        writeLineToFile("if-goto " + label);
    }

    public void writeCall(String name, int nArgs){
        writeLineToFile("call " + name + " " + nArgs);
    }

    public void writeFunction(String name, int nArgs){
        writeLineToFile("function " + name + " " + nArgs);
    }

    public void writeReturn(){
        writeLineToFile("return");
    }

    public void writeLineToFile(String line) {
        if (line == null) {
            return;
        }
        try {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        } catch (IOException e) {
            throw new Error("Can't write to file");
        }
    }

    public void close() throws IOException{
        bufferedWriter.close();
    }
}
