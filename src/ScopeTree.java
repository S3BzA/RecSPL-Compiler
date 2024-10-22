import java.util.ArrayList;
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
            return true;
        }else{
            curSymbolTable = curSymbolTable.getParent();
            return true;
        }
    }

    public SymbolTable GetCurrentSymbolTable(){
        return curSymbolTable;
    }

    public void CalculateScope(TreeNode<Token> tok) {
        // Reset the current scope to the root
        this.curSymbolTable = this.root;
    
        // Initialize a list to store scope names (function names)
        List<String> scopeNames = new ArrayList<>();
    
        TreeNode<Token> currentNode = tok;
    
        // If the passed-in node is a DECL node, we should not include its scope
        if (IsDeclNode(currentNode)) {
            currentNode = currentNode.getParent(); // Start from the parent node
        }
    
        // Traverse up the tree to the root
        while (currentNode != null) {
            // If the current node is a DECL node
            if (IsDeclNode(currentNode)) {
                // Get the function name
                String functionName = FindDeclName(currentNode);
                if (functionName != null) {
                    // Add the function name to the front of the list to maintain correct order
                    scopeNames.add(0, functionName);
                }else{
                    throw new RuntimeException("Function name not found when getting scope");
                }
            }
            // Move up to the parent node
            currentNode = currentNode.getParent();
        }
    
        // Now, from the root, enter scopes in order of collected scope names
        for (String scopeName : scopeNames) {
            // Enter the scope with the given name
            EnterScope(scopeName);
        }
    }
    
    public Boolean IsDeclNode(TreeNode<Token> tok){
     if (tok.getData().getWord() == "DECL" && tok.getData().getId() ==-1){
        return true;
     }
     return false;
    }

    public String FindDeclName(TreeNode<Token> tok){
        if (tok == null){
            throw new RuntimeException("Invalid declaration name fetch attempt node is null");
        }

        if (!tok.getData().getWord().equals("DECL") || tok.getData().getId() != -1) {
            throw new IllegalArgumentException("Expected DECL node");
        }

        List<TreeNode<Token>> declChildren = tok.getChildren();
        if (declChildren == null || declChildren.isEmpty()) {
            throw new IllegalArgumentException("DECL node does not have any children");
        }

        // Only inspect the immediate HEADER child of DECL
        TreeNode<Token> headerNode = null;
        for (TreeNode<Token> child : declChildren) {
            if (child.getData().getWord().equals("HEADER") && child.getData().getId() == -1) {
                headerNode = child;
                break; // Only consider the first HEADER child
            }
        }

        if (headerNode == null) {
            throw new IllegalArgumentException("DECL node does not have a HEADER child");
        }

        List<TreeNode<Token>> headerChildren = headerNode.getChildren();
        if (headerChildren == null || headerChildren.size() < 9) {
            throw new IllegalArgumentException("HEADER node does not have expected children");
        }

        // According to the grammar, the HEADER has children: FTYP and FNAME
        // We can directly access the second child, which should be FNAME
        TreeNode<Token> fnameNode = headerChildren.get(1);
        if (!fnameNode.getData().getWord().equals("FNAME") || fnameNode.getData().getId() != -1) {
            throw new IllegalArgumentException("Expected FNAME node as second child of HEADER");
        }

        // The FNAME node should have a terminal child with the function name
        List<TreeNode<Token>> fnameChildren = fnameNode.getChildren();
        if (fnameChildren == null || fnameChildren.isEmpty()) {
            throw new IllegalArgumentException("FNAME node does not have any children");
        }

        TreeNode<Token> functionNameNode = fnameChildren.get(0);
        Token functionNameToken = functionNameNode.getData();

        // Ensure it's a terminal node (id != -1)
        if (functionNameToken.getId() == -1) {
            throw new IllegalArgumentException("Expected terminal node with function name");
        }

        // Return the function name
        return functionNameToken.getWord();
    }

    public Boolean IsVarDeclaration(TreeNode<Token> tok){
        //assuming tok is V with non negative id
        if (tok.getData().getTokenClass().equals("V") && (tok.getData().getId() != -1)){

            TreeNode<Token> parent = tok.getParent();
            if (parent == null){
                throw new RuntimeException("Parent is null when checking var declaration");
            }
            TreeNode<Token> parentParent = parent.getParent();
            if (parentParent == null){
                throw new RuntimeException("Parent is null when checking var declaration");
            }

            if (parent.getData().getWord() == "VNAME" && (parent.getData().getId() == -1)){
                if (parentParent.getData().getWord() == "GLOBVARS" && (parentParent.getData().getId() == -1)){
                    return true;
                }
                if (parentParent.getData().getWord() == "LOCVARS" && (parentParent.getData().getId() == -1)){
                    return true;
                }
                if (parentParent.getData().getWord() == "HEADER" && (parentParent.getData().getId() == -1)){
                    return true;
                }
            }
        }

        return false;

    }
    
    public Boolean IsVarUsage(TreeNode<Token> tok){
        if (!this.IsVarDeclaration(tok)){
            if (tok.getData().getTokenClass().equals("V") && (tok.getData().getId() != -1)){
                return true;
            }
        }
        return false;
    }
    
    public Boolean IsFuncUsage(TreeNode<Token> tok){
        return false;
    }
    //Printing

    public void printScopeTreeAndTables() {
        // First, print the tree of scope names
        Ansi.printlnFormatted(Ansi.green("\nScope Tree:"));
        printScopeNames(root, "", true);

        // Then, print the details of each scope
        Ansi.printlnFormatted(Ansi.green("\nScope Tables:"));
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
                Ansi.printlnFormatted(Ansi.purple("   " + entry.getKey() + " : " + entry.getValue()));
            }
        } else {
            System.out.println("Functions: ");
            Ansi.printlnFormatted(Ansi.purple("   "+"None"));
        }

        // Print variables
        Map<String, Symbol> vTable = node.getVTable();
        if (!vTable.isEmpty()) {
            System.out.println("Variables:");
            for (Map.Entry<String, Symbol> entry : vTable.entrySet()) {
                Ansi.printlnFormatted(Ansi.cyan("   " + entry.getKey() + " : " + entry.getValue()));
            }
        } else {
            System.out.println("Variables: ");
            Ansi.printlnFormatted(Ansi.cyan("   "+"None"));
        }

        // Recursively print details of child scopes
        for (SymbolTable child : node.getChildren()) {
            printScopeDetails(child);
        }
    }

}
