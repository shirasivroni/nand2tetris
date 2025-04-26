import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static boolean sys = false;

    public static void translateFile(File file, CodeWriter codeWriter) throws IOException{
        Parser parser = new Parser(file);
        codeWriter.setFileName(file.getName());
        while(parser.hasMoreLines()){
            if (parser.commandType() == CommandType.C_POP || parser.commandType() == CommandType.C_PUSH){
                codeWriter.writePushPop(parser.commandType(), parser.args1(), parser.args2());
            }
            else if(parser.commandType() == CommandType.C_ARITMHETIC){
                codeWriter.writeArithmetic(parser.args1());
            }
            else if (parser.commandType() == CommandType.C_LABEL){
                codeWriter.writeLabel(parser.args1());
            }
            else if(parser.commandType() == CommandType.C_GOTO){
                codeWriter.writeGoto(parser.args1());
            }
            else if(parser.commandType() == CommandType.C_IF){
                codeWriter.writeIf(parser.args1());
            }
            else if (parser.commandType() == CommandType.C_CALL){
                codeWriter.writeCall(parser.args1(), parser.args2());
            }
            else if(parser.commandType() == CommandType.C_FUNCTION){
                codeWriter.writeFunction(parser.args1(), parser.args2());
            }
            else if (parser.commandType() == CommandType.C_RETURN){
                codeWriter.writeReturn();
            }
            else {
                throw new Error("Invalid command type");
            }
            parser.advance();
        }
        //codeWriter.endOfFile();
        //parser.close();
        //codeWriter.close();

    }

    public static void dirOfVM(File dir, List<File> files){
        if (dir.isDirectory()){
            File[] vmFiles = dir.listFiles();
            if (files != null){
                for (File file : vmFiles){
                    if (file.isDirectory()){
                        dirOfVM(file, files);
                    }
                    else if(file.getName().endsWith(".vm")){
                        files.add(file);
                        if(file.getName().equals("Sys.vm")){
                            sys = true;
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0 || !new File(args[0]).exists()) {
            throw new Error("Please give a file / dir name");
        }
        File inputFileOrDir = new File(args[0]);
        if (inputFileOrDir.isDirectory()) {
            List<File> vmFiles = new ArrayList<>();
            dirOfVM(inputFileOrDir, vmFiles);
            if (vmFiles != null) {
                File outputFile = new File(inputFileOrDir, inputFileOrDir.getName() + ".asm");
                outputFile.createNewFile();
                CodeWriter codeWriter = new CodeWriter(outputFile);
                for (File vmFile : vmFiles) {
                    Main.translateFile(vmFile, codeWriter);
                }
                codeWriter.close();
            }
        } else if (inputFileOrDir.isFile() && args[0].endsWith(".vm")) {
            CodeWriter codeWriter = new CodeWriter(inputFileOrDir);
            Main.translateFile(inputFileOrDir, codeWriter);
            codeWriter.close();
        } else {
            throw new Error("Invalid input");
        }
    }
}