import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolTable {

    private String scopeName;
    private SymbolTable parent = null;
    private List<SymbolTable> children = null;
    private Map<String, Symbol> vTable = new HashMap<>();
    private Map<String, Symbol> fTable = new HashMap<>();

    public SymbolTable(String scopeName, SymbolTable parent){
        this.scopeName = scopeName;
        this.parent = parent;
    }

    //Binding Function and var binding rule logic
    //False if error/does not conform to rules

    public Boolean BindVar(String fromName, String toName, String type) {
        if (vTable.containsKey(fromName)) {
            return false;
        } else {
            Symbol sym = new Symbol(toName, type);
            vTable.put(fromName, sym);
            return true;
        }
    }
    
    public Boolean BindFunc(String fromName, String toName,String type){
        if (fromName.equals(scopeName)) {
            return false;
        } else if (fTable.containsKey(fromName)) {
            return false;
        } else {
            // Bind the function in the current scope
            Symbol sym = new Symbol(toName, type);
            fTable.put(fromName, sym);
            //add scope to children scope/open new scope
            this.AddChild(fromName);
            return true;
        }
    }

    //Lookup Function and var lookup rule logic 
    //Null if error/decl not found/not valid function call

    public Symbol LookupVar(String name) {
        if (vTable.containsKey(name)) {
            return vTable.get(name);
        } else if (parent != null) {
            return parent.LookupVar(name);
        } else {
            return null;
        }
    }

    public Symbol LookupFunc(String name){
        if (name.equals("main")){
            return null;
        }
        
        if (fTable.containsKey(name)){
            return fTable.get(name);
        }
        
        if (name.equals(scopeName)){
            return parent.LookupFunc(scopeName);
        }

        return null;
    }

    //Helpers

    public void AddChild(String name){
        if (children == null) {
            children = new ArrayList<>();
        }
        SymbolTable child  = new SymbolTable(name, this);
        children.add(child);
    }

    //null if error
    public SymbolTable getChild(String scopeName){
        if (children == null) {
            return null;
        }

        for (SymbolTable child : children) {
            if (child.scopeName.equals(scopeName)) {
                return child;
            }
        }

        return null;
    }

    //null if root
    public SymbolTable getParent(){
        return this.parent;
    }

}
