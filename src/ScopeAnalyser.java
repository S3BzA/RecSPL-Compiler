import java.util.List;

public class ScopeAnalyser {

    private final TreeNode<Token> rootTreeNode;
    private ScopeTree scopeTree;
    private NameGenerator nameGen = NameGenerator.getNameGenerator();

    public ScopeAnalyser(TreeNode<Token> root){
        this.rootTreeNode =  root;
        this.scopeTree = new ScopeTree();
    }
    //testing symbol tables
    public void ScopeTest(){
        //can manually test scope bindings
    }

    public ScopeTree PopulateTree(){
        //must first verify returns are correct semantically
        DfsVerifyReturns(rootTreeNode);
        DfsBuild(rootTreeNode);
        DfsPopulateVar(rootTreeNode);
        System.out.println("");
        Ansi.printlnFormatted(Ansi.green("Scope Tree and Tables..."));
        scopeTree.printScopeTreeAndTables();
        Ansi.printlnFormatted(Ansi.green("Variable Usage..."));
        DfsVerifyVarUse(rootTreeNode);
        Ansi.printlnFormatted(Ansi.green("Function Usage..."));
        DfsVerifyFuncUse(rootTreeNode);
        scopeTree.ResetTreeToRoot();
        return this.scopeTree;
    }

    //Helpers

    //phase 1
    public void DfsBuild(TreeNode<Token> node) {
        if (node == null) {
            return;
        }

        if (scopeTree.IsDeclNode(node)){
            scopeTree.CalculateScope(node);
            String functionName = scopeTree.FindDeclName(node);
            String type = scopeTree.FindDeclType(node);
            scopeTree.GetCurrentSymbolTable().BindFunc(functionName, nameGen.getFunName(), type);
        }
    
        // Recursively visit all the children
        List<TreeNode<Token>> children = node.getChildren();
        if (children != null) {
            for (TreeNode<Token> child : children) {
                DfsBuild(child); // Recursive call for each child
            }
        }
    }
    
    //phase 2
    public void DfsPopulateVar(TreeNode<Token> node){
        if (node == null) {
            return;
        }

        if (scopeTree.IsVarDeclaration(node)){
            scopeTree.CalculateScope(node);
            String type = scopeTree.FindVarDeclType(node);
            scopeTree.GetCurrentSymbolTable().BindVar(node.getData().getWord(), nameGen.getVarName(), type);
        }
    
        // Recursively visit all the children
        List<TreeNode<Token>> children = node.getChildren();
        if (children != null) {
            for (TreeNode<Token> child : children) {
                DfsPopulateVar(child); // Recursive call for each child
            }
        }
    }

    //Phase 3 Analyse using variable lookups if var use is correct !will error if functions and variables are incorrectly called and used
    public void DfsVerifyVarUse(TreeNode<Token> node){
        //if is not var declaration but is a var with vname parent
        if (node == null) {
            return;
        }

        if (scopeTree.IsVarUsage(node)){
            scopeTree.CalculateScope(node);
            Symbol s =scopeTree.GetCurrentSymbolTable().LookupVar(node.getData().getWord());
            //comment out to remove printing
            Ansi.printlnFormatted(("\nScope Name: " +scopeTree.GetCurrentSymbolTable().GetScopeName()));
            System.out.println("    "+node.getData().getWord()+" Symbol: "+s);
        }
    
        // Recursively visit all the children
        List<TreeNode<Token>> children = node.getChildren();
        if (children != null) {
            for (TreeNode<Token> child : children) {
                DfsVerifyVarUse(child); // Recursive call for each child
            }
        }
    }
    
    public void DfsVerifyFuncUse(TreeNode<Token> node){
        if (node == null) {
            return;
        }

        if (scopeTree.IsFuncUsage(node)){
            scopeTree.CalculateScope(node);
            Symbol s =scopeTree.GetCurrentSymbolTable().LookupFunc(node.getData().getWord());
            //comment out to remove printing
            Ansi.printlnFormatted(("\nScope Name: " +scopeTree.GetCurrentSymbolTable().GetScopeName()));
            System.out.println("    "+node.getData().getWord()+" Symbol: "+s);
        }
    
        // Recursively visit all the children
        List<TreeNode<Token>> children = node.getChildren();
        if (children != null) {
            for (TreeNode<Token> child : children) {
                DfsVerifyFuncUse(child); // Recursive call for each child
            }
        }
    }

    //TODO: implement
    public void DfsVerifyReturns(TreeNode<Token> node){

        if (node == null) {
            return;
        }

        if (scopeTree.IsProgNode(node)){
            //ensure no return in main algo anywhere
        }

        if (scopeTree.IsDeclNode(node)){
            String type = scopeTree.FindDeclType(node);

            if (type.equals("void")){
                //ensure there are no returns in the algo for void anywhere
                System.out.println(node.getData().getWord());
                System.out.println(type);
            }else{
                //ensure there is one return and its at the end of algo
                System.out.println(node.getData().getWord());
                System.out.println(type);
            }
        }
    
        // Recursively visit all the children
        List<TreeNode<Token>> children = node.getChildren();
        if (children != null) {
            for (TreeNode<Token> child : children) {
                DfsVerifyReturns(child); // Recursive call for each child
            }
        }
    }
}
