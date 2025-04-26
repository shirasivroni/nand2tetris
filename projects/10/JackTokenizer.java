import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.StreamSupport;

enum TokenType {
    KEYWORD,
    SYMBOL,
    IDENTIFIER,
    INT_CONST,
    STRING_CONST
}

enum Keyword {
    CLASS,
    METHOD,
    FUNCTION,
    CONSTRUCTOR,
    INT,
    BOOLEAN,
    CHAR,
    VOID,
    VAR,
    STATIC,
    FIELD,
    LET,
    DO,
    IF,
    ELSE,
    WHILE,
    RETURN,
    TRUE,
    FALSE,
    NULL,
    THIS
}

public class JackTokenizer {
    private ArrayList<String> tokens;
    private String[] keywords;
    public String symbols;
    private Scanner myReader;
    public int currTokenIndex;
    public String currToken;

    public JackTokenizer(File inputFile) throws FileNotFoundException {
        tokens = new ArrayList<>();
        symbols = "{}()[].,;+-*/&|<>=~";
        initKeywords();
        initTokens(inputFile);
        currTokenIndex = -1;
        currToken = "";
        advance();
    }

    public void initTokens(File inputFile) throws FileNotFoundException {
        myReader = new Scanner(inputFile);
        String currLine;
        String allFile = "";
        while (myReader.hasNextLine()) {
            currLine = myReader.nextLine();
            while (currLine.trim().isEmpty() || currLine.contains("//") || currLine.contains("/*") || currLine.startsWith(" *")) {
                if (currLine.contains("//") || currLine.contains("/*") || currLine.startsWith(" *")) {
                    if (currLine.contains("//")) {
                        currLine = currLine.substring(0, currLine.indexOf("//")).trim();
                    } else if (currLine.contains("/*")) {
                        currLine = currLine.substring(0, currLine.indexOf("/*")).trim();
                    } else if (currLine.startsWith(" *")) {
                        currLine = currLine.substring(0, currLine.indexOf("*")).trim();
                    }
                }
                if (currLine.trim().isEmpty()) {
                    if (myReader.hasNextLine()) {
                        currLine = myReader.nextLine();
                    } else {
                        break;
                    }
                }
            }
            allFile += currLine.trim();
        }
        while (allFile.length() > 0) {
            while (allFile.charAt(0) == ' ') {
                allFile = allFile.substring(1);
            }
            for (int i = 0; i < 21; i++) {
                if (allFile.startsWith(keywords[i])) {
                    tokens.add(keywords[i]);
                    allFile = allFile.substring(keywords[i].length());
                }
            }
            if (symbols.contains(allFile.substring(0, 1))) {
                tokens.add(allFile.substring(0, 1));
                allFile = allFile.substring(1);
            } else if (Character.isDigit(allFile.charAt(0))) {
                String num = allFile.substring(0, 1);
                allFile = allFile.substring(1);
                while (Character.isDigit(allFile.charAt(0))) {
                    num += allFile.substring(0, 1);
                    allFile = allFile.substring(1);
                }
                tokens.add(num);
            } else if (allFile.substring(0, 1).equals("\"")) {
                String str = allFile.substring(0, 1);
                allFile = allFile.substring(1);
                while (!allFile.substring(0, 1).equals("\"")) {
                    str += allFile.substring(0, 1);
                    allFile = allFile.substring(1);
                }
                str += allFile.substring(0, 1);
                allFile = allFile.substring(1);
                tokens.add(str);
            } else if (Character.isLetter(allFile.charAt(0)) || allFile.substring(0, 1).equals("_")) {
                String str = allFile.substring(0, 1);
                allFile = allFile.substring(1);
                while (Character.isLetter(allFile.charAt(0)) || allFile.substring(0, 1).equals("_")) {
                    str += allFile.substring(0, 1);
                    allFile = allFile.substring(1);
                }
                tokens.add(str);
            }

        }
    }

    public void initKeywords() {
        keywords = new String[21];
        keywords[0] = "class";
        keywords[1] = "constructor";
        keywords[2] = "function";
        keywords[3] = "method";
        keywords[4] = "field";
        keywords[5] = "static";
        keywords[6] = "var";
        keywords[7] = "int";
        keywords[8] = "char";
        keywords[9] = "boolean";
        keywords[10] = "void";
        keywords[11] = "true";
        keywords[12] = "false";
        keywords[13] = "null";
        keywords[14] = "this";
        keywords[15] = "let";
        keywords[16] = "do";
        keywords[17] = "if";
        keywords[18] = "else";
        keywords[19] = "while";
        keywords[20] = "return";
    }

    public boolean hasMoreTokens() {
        return tokens.size() - 1 > currTokenIndex;
    }

    public void advance() {
        if (hasMoreTokens()) {
            currTokenIndex++;
            currToken = tokens.get(currTokenIndex);

        }
    }

    public boolean isKeyword(String token) {
        for (String keyword : keywords) {
            if (keyword.equals(token)) {
                return true;
            }
        }
        return false;
    }

    public boolean isNumber(String token) {
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public TokenType tokenType() {
        if (isKeyword(currToken)) {
            return TokenType.KEYWORD;
        } else if (symbols.contains(currToken)) {
            return TokenType.SYMBOL;
        } else if (isNumber(currToken)) {
            return TokenType.INT_CONST;
        } else if (currToken.startsWith("\"")) {
            return TokenType.STRING_CONST;
        }
        return TokenType.IDENTIFIER;
    }

    public Keyword keyWord() {
        if (currToken.equals("class")) {
            return Keyword.CLASS;
        } else if (currToken.equals("method")) {
            return Keyword.METHOD;
        } else if (currToken.equals("function")) {
            return Keyword.FUNCTION;
        } else if (currToken.equals("constructor")) {
            return Keyword.CONSTRUCTOR;
        } else if (currToken.equals("int")) {
            return Keyword.INT;
        } else if (currToken.equals("boolean")) {
            return Keyword.BOOLEAN;
        } else if (currToken.equals("char")) {
            return Keyword.CHAR;
        } else if (currToken.equals("void")) {
            return Keyword.VOID;
        } else if (currToken.equals("var")) {
            return Keyword.VAR;
        } else if (currToken.equals("static")) {
            return Keyword.STATIC;
        } else if (currToken.equals("field")) {
            return Keyword.FIELD;
        } else if (currToken.equals("let")) {
            return Keyword.LET;
        } else if (currToken.equals("do")) {
            return Keyword.DO;
        } else if (currToken.equals("if")) {
            return Keyword.IF;
        } else if (currToken.equals("else")) {
            return Keyword.ELSE;
        } else if (currToken.equals("while")) {
            return Keyword.WHILE;
        } else if (currToken.equals("return")) {
            return Keyword.RETURN;
        } else if (currToken.equals("true")) {
            return Keyword.TRUE;
        } else if (currToken.equals("false")) {
            return Keyword.FALSE;
        } else if (currToken.equals("null")) {
            return Keyword.NULL;
        } else if (currToken.equals("this")) {
            return Keyword.THIS;
        }
        return null;
    }

    public char symbol() {
        return currToken.charAt(0);
    }

    public String identifier() {
        return currToken;
    }

    public int intVal() {
        return Integer.parseInt(currToken);
    }

    public String stringVal() {
        return currToken.substring(1, currToken.length() - 1);
    }
}
