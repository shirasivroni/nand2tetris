import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0 || !new File(args[0]).exists()) {
            throw new Error("Please give a file / dir name");
        }
        File inputFileOrDir = new File(args[0]);
        JackAnalyzer jackAnalyzer = new JackAnalyzer(inputFileOrDir);
    }
}
