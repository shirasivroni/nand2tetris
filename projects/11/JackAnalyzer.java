import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JackAnalyzer {
    public JackAnalyzer(File inputFileOrDir) throws IOException{
        if (inputFileOrDir.isDirectory()) {
            List<File> jackFiles = new ArrayList<>();
            dirOfJack(inputFileOrDir, jackFiles);
            for (File jackFile : jackFiles) {
                File outputFile = new File(jackFile.getParent(), jackFile.getName().replace("jack", "xml"));
                CompilationEngine compilationEngine = new CompilationEngine(jackFile, outputFile);
                compilationEngine.compileClass();
                compilationEngine.close();
            }
        } else if (inputFileOrDir.isFile() && inputFileOrDir.getName().endsWith(".jack")) {
            File outputFile = new File(inputFileOrDir.getName().replace("jack", "xml"));
            outputFile.createNewFile();
            CompilationEngine compilationEngine = new CompilationEngine(inputFileOrDir, outputFile);
            compilationEngine.compileClass();
            compilationEngine.close();

        } else {
            throw new Error("Invalid input");
        }
    }
    public static void dirOfJack(File dir, List<File> files) {
        if (dir.isDirectory()) {
            File[] jackFiles = dir.listFiles();
            if (files != null) {
                for (File file : jackFiles) {
                    if (file.isDirectory()) {
                        dirOfJack(file, files);
                    } else if (file.getName().endsWith(".jack")) {
                        files.add(file);
                    }
                }
            }
        }
    }
}