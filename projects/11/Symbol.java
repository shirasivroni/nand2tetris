enum Kind{
    STATIC,
    FIELD,
    ARG,
    VAR
}
public class Symbol {
    private String type;
    private Kind kind;
    private int index;

    public Symbol(String t, Kind k, int i){
        type = t;
        kind = k;
        index = i;
    }

    public String getType(){
        return type;
    }

    public Kind getKind(){
        return kind;
    }

    public int getIndex(){
        return index;
    }

    public String toString(){
        return "Symbol{" + "type='" + type + "\'" + ", kind=" + kind + ", index=" + index + "}";
    }
}
