import java.util.HashMap;
public class SymbolTable {
    private HashMap<String, Integer> symbolTable;

    public SymbolTable(){
        this.symbolTable = new HashMap<String, Integer>();
        init();
    }

    public void init(){
        for (int i = 0; i < 16; i++) {
            symbolTable.put("R" + i, i);
        }
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
    }

    public void addEntry(String symbol, int address){
        this.symbolTable.put(symbol, address);
    }

    public boolean contains(String symbol){
        return this.symbolTable.containsKey(symbol);
    }

    public int getAddress(String symbol){
        return this.symbolTable.get(symbol);
    }

}
