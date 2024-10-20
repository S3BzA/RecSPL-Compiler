import java.util.List;

public class ScopeAnalyser {

    //root of concrete syntax tree
    private final TreeNode<Token> rootTreeNode;
    private ScopeTree scopeTree;

    public ScopeAnalyser(TreeNode<Token> root){
        this.rootTreeNode =  root;
        this.scopeTree = new ScopeTree();
    }

    //population
    //dfs
    //when to enter and exit scope
    //when to bind

    public void PopulateTree(){
        dfsTraversal(rootTreeNode);
    }

    private void dfsTraversal(TreeNode<Token> node) {
        if (node == null) {
            return;
        }

        // Process the current node's data
        // For example, print the token or perform other operations
        System.out.println(node.getData());

        // Recursively traverse the children
        List<TreeNode<Token>> children = node.getChildren();
        if (children != null) {
            for (TreeNode<Token> child : children) {
                dfsTraversal(child);
            }
        }

    }
    
    //semantic analysis
    //dfs
    //when to enter and exit scope
    //when to lookup
    
    public void AnalyseTree(){}

}
