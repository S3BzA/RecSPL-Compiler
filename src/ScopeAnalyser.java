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
        // scopeTree.GetCurrentSymbolTable().BindFunc("F_a", nameGen.getFunName(), "");
        // scopeTree.EnterScope("F_a");
        // scopeTree.GetCurrentSymbolTable().BindFunc("F_b", nameGen.getFunName(), "");
        // scopeTree.EnterScope("F_b");
        // scopeTree.GetCurrentSymbolTable().LookupFunc("F_a");

        scopeTree.GetCurrentSymbolTable().BindVar("V_a", nameGen.getVarName(), "");
        scopeTree.GetCurrentSymbolTable().BindVar("V_b", nameGen.getVarName(), "");
        scopeTree.GetCurrentSymbolTable().BindFunc("F_a", nameGen.getFunName(), "");
        scopeTree.GetCurrentSymbolTable().BindFunc("F_b", nameGen.getFunName(), "");
        scopeTree.EnterScope("F_a");
        scopeTree.GetCurrentSymbolTable().BindFunc("F_b", nameGen.getFunName(), "");
        scopeTree.GetCurrentSymbolTable().BindVar("V_a", nameGen.getVarName(), "");
        scopeTree.EnterScope("F_b");
        scopeTree.GetCurrentSymbolTable().BindVar("V_b", nameGen.getVarName(), "");
        scopeTree.ExitScope();
        scopeTree.ExitScope();
        scopeTree.EnterScope("F_b");
        scopeTree.GetCurrentSymbolTable().BindVar("V_b", nameGen.getVarName(), "");
        System.out.println(scopeTree.GetCurrentSymbolTable().LookupVar("V_a"));
        System.out.println(scopeTree.GetCurrentSymbolTable().LookupVar("V_b"));
        scopeTree.printScopeTreeAndTables();
    }

    public void BuildScopeTree(){
        
    }
    // Phase 1 build empty  scope trees aka bind functions->implicity creates scope trees !will error when functions are incorrectly declared 
    // dfs the tree
    // if node is a declare
    // 1) get The current scope of the decl node using calculatescope(treenode)
    // 2) add this new scope as new symboltable child to current scope and bind func to current scope
    // 3) when all nodes visited set scope tree

    //Phase 2 Visit each node and populate scope trees with variables
    //Phase 3 Analyse using variable lookups if var use is correct !will error if functions and variables are incorrectly called and used
}
