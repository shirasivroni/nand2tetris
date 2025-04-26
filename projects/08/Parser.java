import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

enum CommandType{
    C_ARITMHETIC,
    C_PUSH,
    C_POP,
    C_LABEL,
    C_GOTO,
    C_IF,
    C_FUNCTION,
    C_RETURN,
    C_CALL
}
public class Parser {
    private static final HashMap<Integer,String> arithmeticCommands = new HashMap<Integer,String>();
    private final BufferedReader bufferedReader;
    public String currLine;
    public Parser(File inputFile) throws IOException {
        if (!inputFile.exists()){
            System.out.println("File not found");
            throw new FileNotFoundException((inputFile.getAbsolutePath()));
        }
        FileReader fileReader = new FileReader(inputFile);
        this.bufferedReader = new BufferedReader(fileReader);
        this.advance();
        this.setArithmeticCommandsValues();
    }

    public void setArithmeticCommandsValues(){
        arithmeticCommands.put(1, "add");
        arithmeticCommands.put(2, "sub");
        arithmeticCommands.put(3, "neg");
        arithmeticCommands.put(4, "eq");
        arithmeticCommands.put(5, "gt");
        arithmeticCommands.put(6, "lt");
        arithmeticCommands.put(7, "and");
        arithmeticCommands.put(8, "or");
        arithmeticCommands.put(9, "not");
    }

    public boolean hasMoreLines() {

        return (this.currLine != null && !this.currLine.trim().isEmpty());
    }

    public void advance() {
        try {
            String nextLine = this.bufferedReader.readLine();
            while (nextLine != null && (nextLine.isBlank() || nextLine.trim().startsWith("//"))) {
                nextLine = bufferedReader.readLine();
            }
            if (nextLine != null){
                int index = nextLine.indexOf("//");
                if (index != -1){
                    nextLine = nextLine.substring(0, index);
                }
            }
            currLine = nextLine;
        } catch (IOException e) {
            throw new Error("Can not read file: " + e.getMessage(), e);
        }
    }

    public CommandType commandType() {
        String currCommand = this.currLine.trim();
        String[] words = currCommand.split(" ");
        String firstWord = words[0];

        if (firstWord.equals("push")){
            return CommandType.C_PUSH;
        }
        else if (firstWord.equals("pop")){
            return CommandType.C_POP;
        }
        else if (arithmeticCommands.containsValue(firstWord)){
            return CommandType.C_ARITMHETIC;
        }
        else if(firstWord.equals("label")){
            return CommandType.C_LABEL;
        }
        else if(firstWord.equals("goto")){
            return CommandType.C_GOTO;
        }
        else if (firstWord.equals("if-goto")){
            return CommandType.C_IF;
        }
        else if (firstWord.equals("function")){
            return CommandType.C_FUNCTION;
        }
        else if (firstWord.equals("call")){
            return CommandType.C_CALL;
        }
        else if(firstWord.equals("return")){
            return CommandType.C_RETURN;
        }
        else {
            throw new IllegalArgumentException("Unknown command type");
        }
    }

    public String args1(){
        CommandType currCommandType = commandType();
        if (currCommandType == CommandType.C_RETURN){
            throw new Error("Function should not be called if the current command is C_RETURN");
        }
        else{
            String currCommand = this.currLine.trim();
            String[] words = currCommand.split(" ");
            if (currCommandType == CommandType.C_ARITMHETIC) {
                return words[0];
            }
            else{
                return words[1];
            }
        }
    }

    public int args2(){
        CommandType currCommandType = commandType();
        if (currCommandType == CommandType.C_PUSH || currCommandType == CommandType.C_POP ||
                currCommandType == CommandType.C_FUNCTION || currCommandType == CommandType.C_CALL){
            String currCommand = this.currLine.trim();
            String[] words = currCommand.split(" ");
            return Integer.parseInt(words[2].trim());
        }
        else {
            throw new Error("Function should not be called if the current command not C_PUSH / C_POP / C_FUNCTION / C_CALL");
        }
    }

    public void close(){
        try{
            bufferedReader.close();
        } catch (IOException e){
            throw new Error("Can't write to file");
        }
    }

}
