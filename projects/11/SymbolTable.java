import java.util.Hashtable;
public class SymbolTable {
    private int staticCount;
    private int fieldCount;
    private int argCount;
    private int varCount;
    private Hashtable<String, Symbol> classTable;
    private Hashtable<String, Symbol> subroutineTable;

    public SymbolTable(){
        classTable = new Hashtable<String, Symbol>();
        subroutineTable = new Hashtable<String, Symbol>();
        staticCount = 0;
        fieldCount = 0;
        reset();
    }

    public void reset(){
        subroutineTable.clear();
        argCount = 0;
        varCount = 0;
    }

    public void define(String name, String type, Kind kind){
        if(kind == Kind.STATIC){
            classTable.put(name, new Symbol(type, kind, staticCount));
            staticCount++;
        }
        else if (kind == Kind.FIELD){
            classTable.put(name, new Symbol(type, kind, fieldCount));
            fieldCount++;
        }
        else if (kind == Kind.ARG){
            subroutineTable.put(name, new Symbol(type, kind, argCount));
            argCount++;
        }
        else if (kind == Kind.VAR){
            subroutineTable.put(name, new Symbol(type, kind, varCount));
            varCount++;
        }
    }

    public int varCount(Kind kind){
        if(kind == Kind.STATIC) {
            return staticCount;
        }
        else if(kind == Kind.FIELD){
            return fieldCount;
        }
        else if(kind == Kind.ARG){
            return argCount;
        }
        else if(kind == Kind.VAR){
            return varCount;
        }
        return -1;
    }

    public Kind kindOf(String name){
        if (subroutineTable.containsKey(name)){
            return subroutineTable.get(name).getKind();
        }
        else if(classTable.containsKey(name)){
            return classTable.get(name).getKind();
        }
        else{
            return null;
        }
    }

    public String typeOf(String name){
        if (subroutineTable.containsKey(name)){
            return subroutineTable.get(name).getType();
        }
        else if(classTable.containsKey(name)){
            return classTable.get(name).getType();
        }
        else{
            return null;
        }
    }

    public int indexOf(String name){
        if (subroutineTable.containsKey(name)){
            return subroutineTable.get(name).getIndex();
        }
        else if(classTable.containsKey(name)){
            return classTable.get(name).getIndex();
        }
        else{
            return -1;
        }
    }

    public void incArgCounter() {
        argCount++;
    }
}
