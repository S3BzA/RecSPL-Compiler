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

    public void BindVar(String fromName, String toName, String type) {
        if (vTable.containsKey(fromName)) {
            throw new RuntimeException("May not redeclare scoped variable "+ fromName);
        } else {
            Symbol sym = new Symbol(toName, type);
            vTable.put(fromName, sym);
        }
    }
    
    public void BindFunc(String fromName, String toName,String type){
        if (fromName.equals("main")){
            throw new RuntimeException("May not redeclare main function");
        }
        if (fromName.equals(scopeName)) {
            throw new RuntimeException("May not redeclare parent-scoped function "+ fromName);
        } else if (fTable.containsKey(fromName)) {
            throw new RuntimeException("May not redeclare sibling-scoped function "+ fromName);
        } else {
            // Bind the function in the current scope
            Symbol sym = new Symbol(toName, type);
            fTable.put(fromName, sym);
            //add scope to children scope/open new scope
            this.AddChild(fromName);
        }
    }

    //Lookup Function and var lookup rule logic 

    public Symbol LookupVar(String name) {
        if (vTable.containsKey(name)) {
            return vTable.get(name);
        } else if (parent != null) {
            return parent.LookupVar(name);
        } else {
            throw new RuntimeException("Use of undeclared or incorrectly declared variable "+ name);
        }
    }

    public Symbol LookupFunc(String name){
        if (name.equals("main")){
            throw new RuntimeException("May not make calls to main function");
        }
        
        if (fTable.containsKey(name)){
            return fTable.get(name);
        }
        
        if (name.equals(scopeName)){
            return parent.LookupFunc(scopeName);
        }

        throw new RuntimeException("Function calls may only refer to immediate child or current function");
    }

    //Helpers

    public void AddChild(String name){
        if (children == null) {
            children = new ArrayList<>();
        }
        SymbolTable child  = new SymbolTable(name, this);
        children.add(child);
    }

    public SymbolTable getChild(String scopeName){
        if (children == null) {
            throw new RuntimeException("Something went wrong fetching child no children");
        }
        SymbolTable toReturn = null;
        for (SymbolTable child : children) {
            if (child.scopeName.equals(scopeName)) {
                toReturn  = child;
            }
        }

        if (toReturn == null) {
            throw new RuntimeException("Something went wrong fetching child no children found with the scope name "+scopeName);
        }

        return toReturn;
    }

    //for printing

    public List<SymbolTable> getChildren() {
        return children != null ? children : new ArrayList<>();
    }

    public Map<String, Symbol> getVTable() {
        return vTable;
    }

    public Map<String, Symbol> getFTable() {
        return fTable;
    }

    //null if root
    public SymbolTable getParent(){
        return this.parent;
    }

    public String GetScopeName(){
        return this.scopeName;
    }
}
