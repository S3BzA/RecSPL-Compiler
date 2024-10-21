import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

    public void PopulateTree(){
        DfsBuild(rootTreeNode);
        DfsPopulateVars(rootTreeNode);
        scopeTree.printScopeTreeAndTables();
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
            scopeTree.GetCurrentSymbolTable().BindFunc(functionName, nameGen.getFunName(), "");
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
    public void DfsPopulateVars(TreeNode<Token> node){
        if (node == null) {
            return;
        }

        if (scopeTree.IsVarDeclaration(node)){
            scopeTree.CalculateScope(node);
            scopeTree.GetCurrentSymbolTable().BindVar(node.getData().getWord(), nameGen.getVarName(), "");
        }
    
        // Recursively visit all the children
        List<TreeNode<Token>> children = node.getChildren();
        if (children != null) {
            for (TreeNode<Token> child : children) {
                DfsPopulateVars(child); // Recursive call for each child
            }
        }
    }

    //Phase 3 Analyse using variable lookups if var use is correct !will error if functions and variables are incorrectly called and used
}
