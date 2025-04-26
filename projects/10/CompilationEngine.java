import java.io.*;
import java.util.Scanner;

public class CompilationEngine {
    private BufferedWriter bufferedWriter;
    private final JackTokenizer jackTokenizer;
    private Scanner myReader;
    private int indentation;
    private String op;

    public CompilationEngine(File inputFile, File outputFile) throws IOException {
        if (!inputFile.exists()) {
            throw new FileNotFoundException((inputFile.getAbsolutePath()));
        }
        jackTokenizer = new JackTokenizer(inputFile);
        FileWriter fileWriter = new FileWriter(outputFile);
        bufferedWriter = new BufferedWriter(fileWriter);
        indentation = 0;
        op = "+-*/&|<>=";
    }

    public void compileClass() throws IOException {
        if (jackTokenizer.hasMoreTokens()) {
            formatAndWriteLine("class");
            indentation++;
            formatAndWriteLine("keyword", jackTokenizer.currToken);
            formatAndWriteLine("identifier", jackTokenizer.identifier());
            formatAndWriteLine("symbol", String.valueOf(jackTokenizer.symbol()));
            while (jackTokenizer.hasMoreTokens() && (jackTokenizer.currToken.equals("static") || jackTokenizer.currToken.equals("field"))) {
                compileClassVarDec();
            }
            while (jackTokenizer.hasMoreTokens() && (jackTokenizer.currToken.equals("constructor") || jackTokenizer.currToken.equals("function") || jackTokenizer.currToken.equals("method"))) {
                compileSubroutine();
            }
            formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            indentation--;
            formatAndWriteLine("/class");
        }
    }

    public void compileClassVarDec() {
        formatAndWriteLine("classVarDec");
        indentation++;
        formatAndWriteLine("keyword", jackTokenizer.currToken);
        compileTypeAndVarName();
        indentation--;
        formatAndWriteLine("/classVarDec");
    }

    public void compileTypeAndVarName() {
        if (jackTokenizer.tokenType() == TokenType.KEYWORD) {
            formatAndWriteLine("keyword", jackTokenizer.currToken);
        } else if (jackTokenizer.tokenType() == TokenType.IDENTIFIER) {
            formatAndWriteLine("identifier", jackTokenizer.identifier());
        }
        formatAndWriteLine("identifier", jackTokenizer.identifier());
        while (jackTokenizer.symbol() == ',') {
            formatAndWriteLine("symbol", String.valueOf(jackTokenizer.symbol()));
            formatAndWriteLine("identifier", jackTokenizer.identifier());
        }
        formatAndWriteLine("symbol", String.valueOf(jackTokenizer.symbol()));

    }

    public void compileSubroutine() {
        formatAndWriteLine("subroutineDec");
        indentation++;
        formatAndWriteLine("keyword", jackTokenizer.currToken);
        if (jackTokenizer.tokenType() == TokenType.KEYWORD) {
            formatAndWriteLine("keyword", jackTokenizer.currToken);
        } else if (jackTokenizer.tokenType() == TokenType.IDENTIFIER) {
            formatAndWriteLine("identifier", jackTokenizer.identifier());
        }
        formatAndWriteLine("identifier", jackTokenizer.identifier());
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        compileParameterList();
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        compileSubroutineBody();
        indentation--;
        formatAndWriteLine("/subroutineDec");
    }

    public void compileParameterList() {
        formatAndWriteLine("parameterList");
        indentation++;
        while (jackTokenizer.tokenType() != TokenType.SYMBOL) {
            if (jackTokenizer.tokenType() == TokenType.KEYWORD) {
                formatAndWriteLine("keyword", jackTokenizer.currToken);
            } else if (jackTokenizer.tokenType() == TokenType.IDENTIFIER) {
                formatAndWriteLine("identifier", jackTokenizer.identifier());
            }
            formatAndWriteLine("identifier", jackTokenizer.identifier());
            if (jackTokenizer.symbol() == ',') {
                formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            }
        }
        indentation--;
        formatAndWriteLine("/parameterList");
    }

    public void compileSubroutineBody() {
        formatAndWriteLine("subroutineBody");
        indentation++;
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        while (jackTokenizer.keyWord() == Keyword.VAR) {
            compileVarDec();
        }
        compileStatements();
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        indentation--;
        formatAndWriteLine("/subroutineBody");
    }

    public void compileVarDec() {
        formatAndWriteLine("varDec");
        indentation++;
        formatAndWriteLine("keyword", jackTokenizer.currToken);
        compileTypeAndVarName();
        indentation--;
        formatAndWriteLine("/varDec");
    }

    public void compileStatements() {
        formatAndWriteLine("statements");
        indentation++;
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
        indentation--;
        formatAndWriteLine("/statements");
    }

    public void compileLet() {
        formatAndWriteLine("letStatement");
        indentation++;
        formatAndWriteLine("keyword", jackTokenizer.currToken);
        formatAndWriteLine("identifier", jackTokenizer.identifier());
        if (jackTokenizer.currToken.equals("[")) {
            formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            compileExpression();
            formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        }
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        compileExpression();
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        indentation--;
        formatAndWriteLine("/letStatement");
    }

    public void compileIf() {
        formatAndWriteLine("ifStatement");
        indentation++;
        formatAndWriteLine("keyword", jackTokenizer.currToken);
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        compileExpression();
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        compileStatements();
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        if (jackTokenizer.currToken.equals("else")) {
            formatAndWriteLine("keyword", jackTokenizer.currToken);
            formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            compileStatements();
            formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        }
        indentation--;
        formatAndWriteLine("/ifStatement");
    }

    public void compileWhile() {
        formatAndWriteLine("whileStatement");
        indentation++;
        formatAndWriteLine("keyword", jackTokenizer.currToken);
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        compileExpression();
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        compileStatements();
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        indentation--;
        formatAndWriteLine("/whileStatement");
    }

    public void compileDo() {
        formatAndWriteLine("doStatement");
        indentation++;
        formatAndWriteLine("keyword", jackTokenizer.currToken);
        compileSubroutineCall();
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        indentation--;
        formatAndWriteLine("/doStatement");
    }

    public void compileReturn() {
        formatAndWriteLine("returnStatement");
        indentation++;
        formatAndWriteLine("keyword", jackTokenizer.currToken);
        if (!(jackTokenizer.currToken.equals(";"))) {
            compileExpression();
        }
        formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
        indentation--;
        formatAndWriteLine("/returnStatement");
    }


    public void compileExpression() {
        formatAndWriteLine("expression");
        indentation++;
        compileTerm();
        while (jackTokenizer.tokenType() == TokenType.SYMBOL && op.contains(jackTokenizer.currToken)) {
            formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
            compileTerm();
        }
        indentation--;
        formatAndWriteLine("/expression");
    }

    public void compileTerm() {
        formatAndWriteLine("term");
        indentation++;
        if (jackTokenizer.tokenType() == TokenType.INT_CONST) {
            formatAndWriteLine("integerConstant", String.valueOf(jackTokenizer.intVal()));
        } else if (jackTokenizer.tokenType() == TokenType.STRING_CONST) {
            formatAndWriteLine("stringConstant", jackTokenizer.stringVal());
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
        indentation--;
        formatAndWriteLine("/term");
    }

    public void compileExpressionList() {
        formatAndWriteLine("expressionList");
        indentation++;
        if (jackTokenizer.tokenType() != TokenType.SYMBOL) {
            compileExpression();
            while (jackTokenizer.currToken.equals(",")) {
                formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
                compileExpression();
            }
        }
        if (jackTokenizer.currToken.equals("(")) {
            compileExpression();
            while (jackTokenizer.currToken.equals(",")) {
                formatAndWriteLine("symbol", Character.toString(jackTokenizer.symbol()));
                compileExpression();
            }
        }
        indentation--;
        formatAndWriteLine("/expressionList");
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

    public void formatAndWriteLine(String type, String token) {
        String ind = "";
        for (int i = 0; i < indentation; i++) {
            ind += new String(" ");
        }
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
        writeLineToFile(ind + "<" + type + "> " + token + " </" + type + ">");
        jackTokenizer.advance();
    }

    public void formatAndWriteLine(String type) {
        String ind = "";
        for (int i = 0; i < indentation; i++) {
            ind += new String(" ");
        }
        writeLineToFile(ind + "<" + type + ">");
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
        bufferedWriter.close();
    }

}
