import java.lang.reflect.Type;
import java.util.List;

public class TypeCheck {

    private ScopeTree scopeTree;
    private final TreeNode<Token> rootTreeNode;

    public TypeCheck(ScopeTree tree, TreeNode<Token> root){
        this.scopeTree = tree;
        this.rootTreeNode = root;
    }

    public void AnalyseTypes(){
       Boolean correct =  CheckTypes(rootTreeNode);
        if (!correct){
            throw new RuntimeException("TypeCheck Failed");
        }
    }

    public Boolean CheckTypes(TreeNode<Token> node){

        if (scopeTree.IsProgNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> globvarsNode = children.get(1);
            TreeNode<Token> algoNode = children.get(2);
            TreeNode<Token> functionsNode = children.get(3);

            return (CheckTypes(globvarsNode) && CheckTypes(algoNode) && CheckTypes(functionsNode));
        }

        if (scopeTree.IsGlobvarsNode(node)){
            List<TreeNode<Token>> children = node.getChildren();

            if (children == null || children.isEmpty()){
                return true;
            }else{
                //types already linked can just call globvars CheckTypes
                TreeNode<Token> globvarsNode = children.get(3);
                return CheckTypes(globvarsNode);
            }
        }

        if (scopeTree.IsAlgoNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> instrucNode = children.get(1);
            return CheckTypes(instrucNode);
        }

        if (scopeTree.IsInstrucNode(node)){
            List<TreeNode<Token>> children = node.getChildren();

            if (children == null || children.isEmpty()){
                return true;
            }else{
                TreeNode<Token> commandNode = children.get(0);
                TreeNode<Token> instrucNode = children.get(2);
                return (CheckTypes(commandNode) && CheckTypes(instrucNode));
            }

        }

        if (scopeTree.IsCommandNode(node)){
            List<TreeNode<Token>> children = node.getChildren();

            //skip
            if (children.get(0).getData().getWord().equals("skip")){
                return true;
            }

            //halt
            if (children.get(0).getData().getWord().equals("halt")){
                return true;
            }

            //print
            if (children.get(0).getData().getWord().equals("print")){
                TreeNode<Token> atomicNode = children.get(1);
                if (TypeOf(atomicNode).equals("num")){
                    return true;
                }
                if (TypeOf(atomicNode).equals("text")){
                    return true;
                }
                return false;
            }

            //return
            if (children.get(0).getData().getWord().equals("return")){
                TreeNode<Token> atomicNode = children.get(1);
                scopeTree.CalculateScope(atomicNode);
                String scopeName = scopeTree.GetCurrentSymbolTable().GetScopeName();
                String funcType = scopeTree.GetCurrentSymbolTable().LookupFunc(scopeName).type;

                if (TypeOf(atomicNode) == funcType){
                    return true;
                }else{
                    return false;
                }

            }

            //assign
            if (children.get(0).getData().getWord().equals("ASSIGN")){
                TreeNode<Token> assignNode = children.get(0);
                return CheckTypes(assignNode);
            }

            //call
            if (children.get(0).getData().getWord().equals("CALL")){
                TreeNode<Token> callNode = children.get(0);

                if (TypeOf(callNode).equals("void")){
                    return true;
                }else{
                    return false;
                }

            }

            //branch
            if (children.get(0).getData().getWord().equals("BRANCH")){
                TreeNode<Token> branchNode = children.get(0);
                return CheckTypes(branchNode);
            }
        }

        if (scopeTree.IsAssignNode(node)){
            List<TreeNode<Token>> children = node.getChildren();

            if (children.get(1).getData().getWord().equals("< input")){

                TreeNode<Token> vnameNode = children.get(0);
                if (TypeOf(vnameNode).equals("num") ){
                    return true;
                }else{
                    return false;
                }

            }else{
                TreeNode<Token> vnameNode = children.get(0);
                TreeNode<Token> termNode = children.get(2);

                if (TypeOf(vnameNode)== TypeOf(termNode)){
                    return true;
                }else{
                    return false;
                }

            }
            
        }
        return true;
    }

    public String TypeOf(TreeNode<Token> node){

        if (scopeTree.IsTermNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> childNode = children.get(0);
            return TypeOf(childNode);
        }

        if (scopeTree.IsAtomicNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> childNode = children.get(0);
            return TypeOf(childNode);
        }

        if (scopeTree.IsVnameNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> childNode = children.get(0);
            scopeTree.CalculateScope(childNode);
            Symbol vNameSymbol = scopeTree.GetCurrentSymbolTable().LookupVar(childNode.getData().getWord());
            String type = vNameSymbol.type;
            return type;
        }

        if (scopeTree.IsConstNode(node)){

            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> childNode = children.get(0);

            if (childNode.getData().getTokenClass().equals("N")){
                return "num";
            }

            if (childNode.getData().getTokenClass().equals("T")){
                return "text";
            }
        }

        throw new RuntimeException("undefined typeof call");
    }
    
}

