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

                if (TypeOf(atomicNode).equals(funcType)){
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

                if (TypeOf(vnameNode).equals(TypeOf(termNode))){
                    return true;
                }else{
                    return false;
                }

            }
            
        }
       
        if (scopeTree.IsBranchNode(node)){

        }
        
        throw new RuntimeException("undefined CheckTypes call " + node.getData().getWord());
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

        if (scopeTree.IsCallNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> atomic1 = children.get(2);
            TreeNode<Token> atomic2 = children.get(4);
            TreeNode<Token> atomic3 = children.get(6);
            TreeNode<Token> fnameNode = children.get(0);

            if (TypeOf(atomic1).equals("num")){
                if (TypeOf(atomic2).equals("num")){
                    if (TypeOf(atomic3).equals("num")){
                        return TypeOf(fnameNode);
                    }
                }
            }

            return "u";

        }

        if (scopeTree.IsOpNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> firstNode = children.get(0);
            
            if (firstNode.getData().getWord().equals("UNOP")){
                TreeNode<Token> arg1Node = children.get(2);
                if (TypeOf(firstNode).equals("b")){
                    if (TypeOf(arg1Node).equals("b")){
                        return "b";
                    }
                }

            }

            if (firstNode.getData().getWord().equals("BINOP")){
                TreeNode<Token> arg1Node = children.get(2);
                TreeNode<Token> arg2Node = children.get(4);
                if (TypeOf(firstNode).equals("b")){
                    if (TypeOf(arg1Node).equals("b")){
                        if (TypeOf(arg2Node).equals("b")){
                            return "b";
                        }
                    }
                }

                if (TypeOf(firstNode).equals("num")){
                    if (TypeOf(arg1Node).equals("num")){
                        if (TypeOf(arg2Node).equals("num")){
                            return "num";
                        }
                    }
                }
            }

            return "u";

        }

        if (scopeTree.IsArgNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> firstNode = children.get(0);

            return TypeOf(firstNode);
        }

        if (scopeTree.IsUnopNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> firstNode = children.get(0);

            if (firstNode.getData().getWord().equals("not")){
                return "b";
            }

            if (firstNode.getData().getWord().equals("sqrt")){
                return "num";
            }
        }

        if (scopeTree.IsBinopNode(node)){

            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> firstNode = children.get(0);

            if (firstNode.getData().getWord().equals("or")){
                return "b";
            }

            if (firstNode.getData().getWord().equals("and")){
                return "b";
            }
            
            
            if (firstNode.getData().getWord().equals("eq")){
                return "c";
            }


            if (firstNode.getData().getWord().equals("grt")){
                return "c";
            }


            if (firstNode.getData().getWord().equals("add")){
                return "num";
            }


            if (firstNode.getData().getWord().equals("sub")){
                return "num";
            }


            if (firstNode.getData().getWord().equals("mul")){
                return "num";
            }


            if (firstNode.getData().getWord().equals("div")){
                return "num";
            }
        }

        throw new RuntimeException("undefined typeof call "+ node.getData().getWord());
    }
    
}

