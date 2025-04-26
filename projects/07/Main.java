import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void translateFile(File file, CodeWriter codeWriter) throws IOException{
        Parser parser = new Parser(file);
        while(parser.hasMoreLines()){
            if (parser.commandType() == CommandType.C_POP || parser.commandType() == CommandType.C_PUSH){
                codeWriter.writePushPop(parser.commandType(), parser.args1(), parser.args2());
            }
            else {
                codeWriter.writeArithmetic(parser.args1());
            }
            parser.advance();
        }

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