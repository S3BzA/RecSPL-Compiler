public class ScopeTree {
    private SymbolTable root;
    private SymbolTable curSymbolTable;

    public ScopeTree(){
        this.root  = new SymbolTable("main", null);
        this.curSymbolTable = this.root;
    }

    public Boolean EnterScope(String scopeName){
        if (curSymbolTable.getChild(scopeName) == null){
            return false;
        }else{
            curSymbolTable = curSymbolTable.getChild(scopeName);
            return true;
        }
    }

    public Boolean ExitScope(){
        if (curSymbolTable.getParent() == null){
            return false;
        }else{
            curSymbolTable = curSymbolTable.getParent();
            return true;
        }
    }

    public void CalculateScope(TreeNode<Token> tok){}

}
