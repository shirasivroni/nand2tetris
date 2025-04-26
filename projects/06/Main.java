import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == -1 || !new File(args[0]).exists()) {
            throw new Error("Please give a file / dir name");
        }
        File inputFileOrDir = new File(args[0]);
        if (inputFileOrDir.isDirectory()) {
            File[] asmFiles = inputFileOrDir.listFiles((dir, name) -> name.endsWith(".asm"));
            if (asmFiles != null) {
                for (File asmFile : asmFiles) {
                    HackAssembler hackAssembler = new HackAssembler(asmFile);
                    hackAssembler.parse();
                }
            }
        } else if (inputFileOrDir.isFile() && args[0].endsWith(".asm")) {
            HackAssembler hackAssembler = new HackAssembler(inputFileOrDir);
            hackAssembler.parse();
        } else {
            throw new Error("Invalid input");
        }
    }
}