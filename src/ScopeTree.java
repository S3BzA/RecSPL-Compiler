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

    public void ResetTreeToRoot(){
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

    public String FindDeclType(TreeNode<Token> tok){
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

        TreeNode<Token> ftypeNode = headerChildren.get(0);
        if (!ftypeNode.getData().getWord().equals("FTYPE") || ftypeNode.getData().getId() != -1) {
            throw new IllegalArgumentException("Expected FTYPE node as first child of HEADER");
        }

        // The FNAME node should have a terminal child with the function name
        List<TreeNode<Token>> fnameChildren = ftypeNode.getChildren();
        if (fnameChildren == null || fnameChildren.isEmpty()) {
            throw new IllegalArgumentException("FNAME node does not have any children");
        }

        TreeNode<Token> functionTypeNode = fnameChildren.get(0);
        Token functionTypeToken = functionTypeNode.getData();

        // Ensure it's a terminal node (id != -1)
        if (functionTypeToken.getId() == -1) {
            throw new IllegalArgumentException("Expected terminal node with function name");
        }

        // Return the function name
        return functionTypeToken.getWord();

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
    
    public String FindVarDeclType(TreeNode<Token> tok){

        if (tok == null) {
            throw new RuntimeException("Invalid declaration name fetch attempt node is null");
        }

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
                //get globvars children
                List<TreeNode<Token>> globvarsChildren = parentParent.getChildren();
                //get first child
                TreeNode<Token> vtypeNode = globvarsChildren.get(0);
                //get children of vtype
                List<TreeNode<Token>> vtypeChildren = vtypeNode.getChildren();
                //get first child
                TreeNode<Token> varTypeNode = vtypeChildren.get(0);
                //get data
                Token varTypeToken = varTypeNode.getData();
        
                // Ensure it's a terminal node (id != -1)
                if (varTypeToken.getId() == -1) {
                    throw new IllegalArgumentException("Expected terminal node with function name");
                }
        
                // Return the var name
                return varTypeToken.getWord();
            }
            
            if (parentParent.getData().getWord() == "LOCVARS" && (parentParent.getData().getId() == -1)){
                //get locvars children
                List<TreeNode<Token>> locvarsChildren = parentParent.getChildren();
                
                int indexOfVNAME = locvarsChildren.indexOf(parent);
                if (indexOfVNAME == -1) {
                    throw new RuntimeException("VNAME node not found among LOCVARS children");
                }
                if (indexOfVNAME == 0) {
                    throw new RuntimeException("VNAME node is the first child, no left sibling");
                }

                TreeNode<Token> vtypNode = locvarsChildren.get(indexOfVNAME - 1);
                if (!vtypNode.getData().getWord().equals("VTYP") || vtypNode.getData().getId() != -1) {
                    throw new IllegalArgumentException("Expected VTYP node to the left of VNAME");
                }

                List<TreeNode<Token>> vtypChildren = vtypNode.getChildren();
                if (vtypChildren == null || vtypChildren.isEmpty()) {
                    throw new IllegalArgumentException("VTYP node does not have any children");
                }

                TreeNode<Token> varTypeNode = vtypChildren.get(0);
                Token varTypeToken = varTypeNode.getData();
                if (varTypeToken.getId() == -1) {
                    throw new IllegalArgumentException("Expected terminal node with variable type");
                }
                return varTypeToken.getWord();
            }
            
            if (parentParent.getData().getWord() == "HEADER" && (parentParent.getData().getId() == -1)){
                return "num";
            }
        }

        throw new RuntimeException("Type could not be resolved for var"+tok.getData().getWord());

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
            if (tok.getData().getTokenClass().equals("F") && (tok.getData().getId() != -1)){

                TreeNode<Token> parent = tok.getParent();
                if (parent == null){
                    throw new RuntimeException("Parent is null when checking var declaration");
                }

                TreeNode<Token> parentParent = parent.getParent();
                if (parentParent == null){
                    throw new RuntimeException("Parent is null when checking var declaration");
                }

                if (!IsDeclNode(parentParent.getParent())){
                    return true;
                }
            }
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
        // Collect the content lines
        List<String> contentLines = new ArrayList<>();
    
        // First line: Scope Name
        contentLines.add("Scope Name: " + node.GetScopeName());
    
        // Functions
        Map<String, Symbol> fTable = node.getFTable();
        if (!fTable.isEmpty()) {
            contentLines.add("Functions:");
            for (Map.Entry<String, Symbol> entry : fTable.entrySet()) {
                contentLines.add(Ansi.purple("   " + entry.getKey() + " : " + entry.getValue()));
            }
        } else {
            contentLines.add("Functions:");
            contentLines.add(Ansi.purple("   None"));
        }
    
        // Variables
        Map<String, Symbol> vTable = node.getVTable();
        if (!vTable.isEmpty()) {
            contentLines.add("Variables:");
            for (Map.Entry<String, Symbol> entry : vTable.entrySet()) {
                contentLines.add(Ansi.cyan("   " + entry.getKey() + " : " + entry.getValue()));
            }
        } else {
            contentLines.add("Variables:");
            contentLines.add(Ansi.cyan("   None"));
        }
    
        // Compute the maximum line length (excluding ANSI codes)
        int maxLength = 0;
        for (String line : contentLines) {
            int length = stripAnsiCodes(line).length();
            if (length > maxLength) {
                maxLength = length;
            }
        }
    
        // Total length includes borders and padding
        int totalLength = maxLength + 4;
    
        // Construct the top and bottom border lines
        String borderLine = "+" + "-".repeat(totalLength - 2) + "+";
    
        // Print the top border line
        System.out.println(borderLine);
    
        // Print each line within borders
        for (String line : contentLines) {
            String strippedLine = stripAnsiCodes(line);
            int lineLength = strippedLine.length();
            int paddingLength = totalLength - 4 - lineLength;
            String padding = " ".repeat(Math.max(paddingLength, 0));
            String formattedLine = "| " + line + padding + " |";
            System.out.println(formattedLine);
        }
    
        // Print the bottom border line
        System.out.println(borderLine);
    
        // Recursively print details of child scopes
        for (SymbolTable child : node.getChildren()) {
            printScopeDetails(child);
        }
    }
    
    private String stripAnsiCodes(String str) {
        return str.replaceAll("\\u001B\\[[;\\d]*m", "");
    }
}
