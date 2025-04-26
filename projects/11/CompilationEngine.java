import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class CompilationEngine {
    private final JackTokenizer jackTokenizer;
    private VMWriter vmWriter;
    private SymbolTable symbolTable;
    private String className;
    private String op;
    private ArrayList<String[]> parameterList;
    private ArrayList<String[]> localVarList;
    private int ifCount;
    private int whileCount;


    public CompilationEngine(File inputFile, File outputFile) throws IOException {
        if (!inputFile.exists()) {
            throw new FileNotFoundException((inputFile.getAbsolutePath()));
        }
        jackTokenizer = new JackTokenizer(inputFile);
        vmWriter = new VMWriter(inputFile);
        symbolTable = new SymbolTable();
        op = "+-*/&|<>=";
        parameterList = new ArrayList<String[]>();
        localVarList = new ArrayList<String[]>();
        ifCount = 0;
        whileCount = 0;
    }

    public void compileClass(){
        if (jackTokenizer.hasMoreTokens()) {
            getKeyword();
            className = getIdentifier();
            getSymbol();
            while (jackTokenizer.hasMoreTokens() && (jackTokenizer.currToken.equals("static") || jackTokenizer.currToken.equals("field"))) {
                compileClassVarDec();
            }
            while (jackTokenizer.hasMoreTokens() && (jackTokenizer.currToken.equals("constructor") ||
                    jackTokenizer.currToken.equals("function") || jackTokenizer.currToken.equals("method"))) {
                compileSubroutine();
            }
            getSymbol();
        }
    }

    public void compileClassVarDec() {
        Kind kind = Kind.valueOf(getKeyword());
        String type = getType();
        ArrayList<String> varNames = getVarNames();
        for (String varName : varNames){
            symbolTable.define(varName, type, kind);
        }
    }
//    public void compileTypeAndVarName() {
//        if (jackTokenizer.tokenType() == TokenType.KEYWORD) {
//            formatAndWriteLine("keyword", jackTokenizer.currToken);
//        } else if (jackTokenizer.tokenType() == TokenType.IDENTIFIER) {
//            formatAndWriteLine("identifier", jackTokenizer.identifier());
//        }
//        formatAndWriteLine("identifier", jackTokenizer.identifier());
//        while (jackTokenizer.symbol() == ',') {
//            formatAndWriteLine("symbol", String.valueOf(jackTokenizer.symbol()));
//            formatAndWriteLine("identifier", jackTokenizer.identifier());
//        }
//        formatAndWriteLine("symbol", String.valueOf(jackTokenizer.symbol()));
//
//    }

    public void compileSubroutine() {
        symbolTable.reset();
        String funcType = getKeyword();
        String returnType = getType();
        String funcName = getIdentifier();
        getSymbol();
        compileParameterList();
        if(funcType.equals("method")){
            symbolTable.incArgCounter();
        }
        for(String[] parameter : parameterList) {
            symbolTable.define(parameter[0], parameter[1], Kind.ARG);
        }
        getSymbol();
        getSymbol();
        compileVarDec();
        for(String[] localVar : localVarList){
            symbolTable.define(localVar[0], localVar[1], Kind.VAR);
        }
        vmWriter.writeFunction(className + "." + funcName, symbolTable.varCount(Kind.ARG)); //?
        if(funcType.equals("method")){
            vmWriter.writePush(Segment.ARGUMENT, 0);
            vmWriter.writePop(Segment.POINTER, 0);
        }
        else if (funcType.equals("constructor")){
            vmWriter.writePush(Segment.CONSTANT, symbolTable.varCount(Kind.FIELD));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(Segment.POINTER, 1);
        }
        compileSubroutineBody();
        getSymbol();

///        formatAndWriteLine("keyword", jackTokenizer.currToken);
//        if (jackTokenizer.tokenType() == TokenType.KEYWORD) {
//            formatAndWriteLine("keyword", jackTokenizer.currToken);
//        } else if (jackTokenizer.tokenType() == TokenType.IDENTIFIER) {
//            formatAndWriteLine("identifier", jackTokenizer.identifier());
//        }
//        formatAndWriteLine("identifier", jackTokenizer.identifier());
//        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
//        compileParameterList();
//        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
//        compileSubroutineBody();
//        indentation--;
//        formatAndWriteLine("/subroutineDec");
    }

    public void compileParameterList() {
        parameterList = new ArrayList<String[]>();
        String type;
        String name;
        while (jackTokenizer.tokenType() != TokenType.SYMBOL) {
            type = getType();
            name = getIdentifier();
            String[] curr = new String[2];
            curr[0] = type;
            curr[1] = name;
            parameterList.add(curr);
            getSymbol();
//            if (jackTokenizer.tokenType() == TokenType.KEYWORD) {
//                formatAndWriteLine("keyword", jackTokenizer.currToken);
//            } else if (jackTokenizer.tokenType() == TokenType.IDENTIFIER) {
//                formatAndWriteLine("identifier", jackTokenizer.identifier());
//            }
//            formatAndWriteLine("identifier", jackTokenizer.identifier());
//            if (jackTokenizer.symbol() == ',') {
//                formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
//            }
        }
        //indentation--;
        //formatAndWriteLine("/parameterList");
    }

    public void compileSubroutineBody() {
        //formatAndWriteLine("subroutineBody");
        //indentation++;
        //formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
//        while (jackTokenizer.keyWord() == Keyword.VAR) {
//            compileVarDec();
//        }
        compileStatements();
        //formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        //indentation--;
        //formatAndWriteLine("/subroutineBody");
    }

    public void compileVarDec() {
        localVarList = new ArrayList<String[]>();
        getKeyword();
        String type = getType();
        ArrayList<String> names = getVarNames();
        for (String name : names){
            String[] curr = new String[2];
            curr[0] = name;
            curr[1] = type;
            localVarList.add(curr);
        }
        //formatAndWriteLine("keyword", jackTokenizer.currToken);
        //compileTypeAndVarName();
    }

    public void compileStatements() {
        while (jackTokenizer.tokenType() == TokenType.KEYWORD) {
            if (jackTokenizer.currToken.equalsIgnoreCase("let")) {
                compileLet();
            } else if (jackTokenizer.currToken.equalsIgnoreCase("if")) {
                compileIf();
            } else if (jackTokenizer.currToken.equalsIgnoreCase("while")) {
                compileWhile();
            } else if (jackTokenizer.currToken.equalsIgnoreCase("do")) {
                compileDo();
            } else if (jackTokenizer.currToken.equalsIgnoreCase("return")) {
                compileReturn();
            }
        }
    }

    public void compileLet() {
        getKeyword();
        String var = getIdentifier();
//        formatAndWriteLine("keyword", jackTokenizer.currToken);
//        formatAndWriteLine("identifier", jackTokenizer.identifier());
        if (jackTokenizer.currToken.equals("[")) {
            vmWriter.writePush(getSegment(symbolTable.kindOf(var)), symbolTable.indexOf(var));
            getSymbol();
            //formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            compileExpression();
            //formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            getSymbol();
            vmWriter.writeArithmetic(Command.ADD);
            vmWriter.writePop(Segment.POINTER, 1);
            getSymbol();
            compileExpression();
            getSymbol();
            vmWriter.writePop(Segment.THAT, 0);
        }
        else if (jackTokenizer.currToken.equals("=")){
            getSymbol();
            //formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            compileExpression();
            //formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            getSymbol();
            vmWriter.writePush(getSegment(symbolTable.kindOf(var)), symbolTable.indexOf(var));
        }
    }

    public void compileIf() {
        //formatAndWriteLine("ifStatement");
        //indentation++;
//        formatAndWriteLine("keyword", jackTokenizer.currToken);
//        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        ifCount++;
        getKeyword();
        getSymbol();
        compileExpression();
        getSymbol();
        getSymbol();
        vmWriter.writeArithmetic(Command.NOT);
        vmWriter.writeIf("IF_TRUE" + ifCount);
        vmWriter.writeGoto("IF_FALSE" + ifCount);
        vmWriter.writeLabel("IF_TRUE" + ifCount);
        compileStatements();
        getSymbol();
        vmWriter.writeGoto("IF_END" + ifCount);
        vmWriter.writeLabel("IF_FALSE" + ifCount);
//        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
//        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        //formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        if (jackTokenizer.currToken.equals("else")) {
            getKeyword();
            getSymbol();
            compileStatements();
            getSymbol();
//            formatAndWriteLine("keyword", jackTokenizer.currToken);
//            formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
//            formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        }
        vmWriter.writeGoto("IF_END" + ifCount);
        //indentation--;
        //formatAndWriteLine("/ifStatement");
    }

    public void compileWhile() {
        whileCount++;
        getKeyword();
        getSymbol();
        vmWriter.writeLabel("WHILE_START" + whileCount);
        compileExpression();
        getSymbol();
        vmWriter.writeArithmetic(Command.NOT);
        vmWriter.writeIf("WHILE_END" + whileCount);
        getSymbol();
        compileStatements();
        getSymbol();
        vmWriter.writeGoto("WHILE_START" + whileCount);
        vmWriter.writeLabel("WHILE_END" + whileCount);
        //formatAndWriteLine("whileStatement");
        //indentation++;
//        formatAndWriteLine("keyword", jackTokenizer.currToken);
//        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
//        compileExpression();
//        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
//        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
//        compileStatements();
//        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        //indentation--;
        //formatAndWriteLine("/whileStatement");
    }

    public void compileDo() {
        getKeyword();
        compileSubroutineCall();
        getSymbol();
        vmWriter.writePop(Segment.TEMP, 0);
        //formatAndWriteLine("doStatement");
        //indentation++;
//        formatAndWriteLine("keyword", jackTokenizer.currToken);
//        compileSubroutineCall();
//        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        //indentation--;
        //formatAndWriteLine("/doStatement");
    }

    public void compileReturn() {
        //formatAndWriteLine("returnStatement");
        //indentation++;
        getKeyword();
        //formatAndWriteLine("keyword", jackTokenizer.currToken);
        if (!(jackTokenizer.currToken.equals(";"))) {
            compileExpression();
        }
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        //indentation--;
        //formatAndWriteLine("/returnStatement");
    }


    public void compileExpression() {
        //formatAndWriteLine("expression");
        //indentation++;
        compileTerm();
        while (jackTokenizer.tokenType() == TokenType.SYMBOL && op.contains(jackTokenizer.currToken)) {
            getSymbol();
            //formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            compileTerm();
        }
        //indentation--;
        //formatAndWriteLine("/expression");
    }

    public void compileTerm() {
        //formatAndWriteLine("term");
        //indentation++;
        if (jackTokenizer.tokenType() == TokenType.INT_CONST) {
            vmWriter.writePush(Segment.CONSTANT, jackTokenizer.intVal());
            //formatAndWriteLine("integerConstant", String.valueOf(jackTokenizer.intVal()));
        } else if (jackTokenizer.tokenType() == TokenType.STRING_CONST) {
            //formatAndWriteLine("stringConstant", jackTokenizer.stringVal());
        } else if (jackTokenizer.tokenType() == TokenType.KEYWORD) {
            formatAndWriteLine("keyword", jackTokenizer.currToken);
        } else if (jackTokenizer.tokenType() == TokenType.IDENTIFIER) {
            formatAndWriteLine("identifier", jackTokenizer.identifier());
            if (jackTokenizer.currToken.equals("[")) {
                formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
                compileExpression();
                formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            } else if (jackTokenizer.currToken.equals(".")) {
                formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
                formatAndWriteLine("identifier", jackTokenizer.identifier());
                formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
                compileExpressionList();
                formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            } else if (jackTokenizer.currToken.equals("(")) {
                formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
                compileExpressionList();
                formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            }
        } else if (jackTokenizer.currToken.equals("(")) {
            formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            compileExpression();
            formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        } else if (jackTokenizer.currToken.equals("-") || jackTokenizer.currToken.equals("~")) {
            formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            compileTerm();
        }
        //indentation--;
        //formatAndWriteLine("/term");
    }

    public void compileExpressionList() {
        if (jackTokenizer.tokenType() != TokenType.SYMBOL) {
            compileExpression();
            while (jackTokenizer.currToken.equals(",")) {
                getSymbol();
                //formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
                compileExpression();
            }
        }
        if (jackTokenizer.currToken.equals("(")) {
            compileExpression();
            while (jackTokenizer.currToken.equals(",")) {
                getSymbol();
                //formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
                compileExpression();
            }
        }
    }

    public void compileSubroutineCall() {
        formatAndWriteLine("identifier", jackTokenizer.identifier());
        if (jackTokenizer.symbol() == '.') {
            formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            formatAndWriteLine("identifier", jackTokenizer.identifier());
        }
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        compileExpressionList();
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
    }

    public String getKeyword(){
        String keyword = jackTokenizer.keyWord().toString();
        if(jackTokenizer.hasMoreTokens()){
            jackTokenizer.advance();
        }
        return keyword;
    }

    public String getIdentifier(){
        String identifier = jackTokenizer.identifier();
        if(jackTokenizer.hasMoreTokens()){
            jackTokenizer.advance();
        }
        return identifier;
    }

    public char getSymbol(){
        char symbol = jackTokenizer.symbol();
        if(jackTokenizer.hasMoreTokens()){
            jackTokenizer.advance();
        }
        return symbol;
    }

    public int getIntVal(){
        int intVal = jackTokenizer.intVal();
        if(jackTokenizer.hasMoreTokens()){
            jackTokenizer.advance();
        }
        return intVal;
    }

    public String getStringVal(){
        String stringVal = jackTokenizer.stringVal();
        if(jackTokenizer.hasMoreTokens()){
            jackTokenizer.advance();
        }
        return stringVal;
    }

    public String getType(){
        if(jackTokenizer.currToken.equals("int") || jackTokenizer.currToken.equals("char") ||
                jackTokenizer.currToken.equals("boolean") || jackTokenizer.currToken.equals("void")){
            return getKeyword();
        }
        else if (jackTokenizer.tokenType() == TokenType.IDENTIFIER){
            return getIdentifier();
        }
        return null;
    }

    private ArrayList<String> getVarNames() {
        ArrayList<String> varNames = new ArrayList<String>();
        varNames.add(getIdentifier());
        while(!(jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.symbol() == ';')) {
            getSymbol();
            varNames.add(getIdentifier());
        }
        getSymbol();
        return varNames;
    }

    public Segment getSegment(Kind kind){
        if (kind == Kind.FIELD){
            return Segment.THIS;
        }
        else if (kind == Kind.ARG){
            return Segment.ARGUMENT;
        }
        else if(kind == Kind.VAR){
            return Segment.LOCAL;
        }
        else if(kind == Kind.STATIC){
            return Segment.STATIC;
        }
        else{
            return null;
        }
    }


    public void formatAndWriteLine(String type, String token) {
//        String ind = "";
//        for (int i = 0; i < indentation; i++) {
//            ind += new String(" ");
//        }
        if (type.equalsIgnoreCase("symbol")) {
            if (token.equalsIgnoreCase("<")) {
                token = "&lt;";
            } else if (token.equalsIgnoreCase(">")) {
                token = "&gt;";
            } else if (token.equalsIgnoreCase("&")) {
                token = "&amp;";
            }
            if (token.equalsIgnoreCase("\"")) {
                token = "&qout;";
            }
        }
        writeLineToFile("<" + type + "> " + token + " </" + type + ">");
        jackTokenizer.advance();
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

    public void close() throws IOException {
        vmWriter.close();
    }

}
