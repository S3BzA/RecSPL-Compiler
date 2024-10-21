import java.util.List;
import java.util.Map;

public class ScopeTree {
    private SymbolTable root;
    private SymbolTable curSymbolTable;

    public ScopeTree(){
        this.root  = new SymbolTable("main", null);
        this.curSymbolTable = this.root;
    }

    public Boolean EnterScope(String scopeName){
        if (curSymbolTable.getChild(scopeName) == null){
            throw new RuntimeException("Invalid entry of scope: "+scopeName);
        }else{
            curSymbolTable = curSymbolTable.getChild(scopeName);
            return true;
        }
    }

    public Boolean ExitScope(){
        if (curSymbolTable.getParent() == null){
            if (curSymbolTable.GetScopeName() != "main"){
                throw new RuntimeException("Scope exit of non-main scope did not resolve parent");
            }
            return false;
        }else{
            curSymbolTable = curSymbolTable.getParent();
            return true;
        }
    }

    public SymbolTable GetCurrentSymbolTable(){
        return curSymbolTable;
    }

    public void CalculateScope(TreeNode<Token> tok){}

    public Boolean IsDeclNode(TreeNode<Token> tok){
        if (tok.getData())
    }

    public String FindDeclName(TreeNode<Token> tok){
        return null;
    }

    //Printing

    public void printScopeTreeAndTables() {
        // First, print the tree of scope names
        System.out.println("Scope Tree:");
        printScopeNames(root, "", true);

        // Then, print the details of each scope
        System.out.println("\nScope Tables:");
        printScopeDetails(root);
    }

    private void printScopeNames(SymbolTable node, String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + node.GetScopeName());

        List<SymbolTable> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            boolean childIsTail = (i == children.size() - 1);
            String childPrefix = prefix + (isTail ? "    " : "│   ");
            printScopeNames(children.get(i), childPrefix, childIsTail);
        }
    }

    private void printScopeDetails(SymbolTable node) {
        System.out.println("\nScope Name: " + node.GetScopeName());

        // Print functions
        Map<String, Symbol> fTable = node.getFTable();
        if (!fTable.isEmpty()) {
            System.out.println("Functions:");
            for (Map.Entry<String, Symbol> entry : fTable.entrySet()) {
                System.out.println("   " + entry.getKey() + " : " + entry.getValue());
            }
        } else {
            System.out.println("Functions: None");
        }

        // Print variables
        Map<String, Symbol> vTable = node.getVTable();
        if (!vTable.isEmpty()) {
            System.out.println("Variables:");
            for (Map.Entry<String, Symbol> entry : vTable.entrySet()) {
                System.out.println("   " + entry.getKey() + " : " + entry.getValue());
            }
        } else {
            System.out.println("Variables: None");
        }

        // Recursively print details of child scopes
        for (SymbolTable child : node.getChildren()) {
            printScopeDetails(child);
        }
    }

}
