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
            throw new RuntimeException("TypeCheck Failed: types are incorrect");
        }else{
            System.out.println("TypeCheck Passed: types are correct");
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
                    System.out.println("ERROR IN HERE");
                    return false;
                }

            }
            
        }
       
        if (scopeTree.IsBranchNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> condNode = children.get(1);
            TreeNode<Token> algo1Node = children.get(3);
            TreeNode<Token> algo2Node = children.get(5);

            if (TypeOf(condNode).equals("b")){
                return (CheckTypes(algo1Node) && CheckTypes(algo2Node));
            }else{
                return false;
            }

        }

        if (scopeTree.IsFunctionsNode(node)){
            List<TreeNode<Token>> children = node.getChildren();

            if (children == null || children.isEmpty()){
                return true;
            }else{
                TreeNode<Token> declNode = children.get(0);
                TreeNode<Token> functions2Node = children.get(1);

                return (CheckTypes(declNode) && CheckTypes(functions2Node));

            }
        }

        if (scopeTree.IsDeclNode(node)){
            List<TreeNode<Token>> children = node.getChildren();

            TreeNode<Token> headerNode = children.get(0);
            TreeNode<Token> bodyNode = children.get(1);

            return (CheckTypes(headerNode) && CheckTypes(bodyNode));
        }

        if (scopeTree.IsHeaderNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> vname1 = children.get(3);
            TreeNode<Token> vname2 = children.get(5);
            TreeNode<Token> vname3 = children.get(7);

            if (TypeOf(vname1).equals("num")){
                if (TypeOf(vname2).equals("num")){
                    if (TypeOf(vname3).equals("num")){
                        return true;
                    }
                }
            }

            return false;
        }

        if (scopeTree.IsBodyNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> prologNode = children.get(0);
            TreeNode<Token> locvarsNode = children.get(1);
            TreeNode<Token> algoNode = children.get(2);
            TreeNode<Token> epilogNode = children.get(3);
            TreeNode<Token> subfuncsNode = children.get(4);

            return (CheckTypes(prologNode) && CheckTypes(locvarsNode) && CheckTypes(algoNode) && CheckTypes(epilogNode) && CheckTypes(subfuncsNode));
        }

        if (scopeTree.IsPrologNode(node)){
            return true;
        }

        if (scopeTree.IsEpilogNode(node)){
            return true;
        }

        if (scopeTree.IsLocvarsNode(node)){
            return true;
        }

        if (scopeTree.IsSubfuncsNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> functionsNode = children.get(0);
            return CheckTypes(functionsNode);
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

        if (scopeTree.IsFnameNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> childNode = children.get(0);
            scopeTree.CalculateScope(childNode);
            Symbol fNameSymbol = scopeTree.GetCurrentSymbolTable().LookupFunc(childNode.getData().getWord());
            String type = fNameSymbol.type;
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

                if (TypeOf(firstNode).equals("num")){
                    if (TypeOf(arg1Node).equals("num")){
                        return "num";
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

        if (scopeTree.IsCondNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> firstNode = children.get(0);
            return TypeOf(firstNode);
        }

        if (scopeTree.IsUcondNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> unopNode = children.get(0);
            TreeNode<Token> simpleNode = children.get(0);

            if (TypeOf(unopNode).equals("b")){
                if (TypeOf(simpleNode).equals("b")){
                    return "b";
                }
            }
            
            return "u";
        }

        if (scopeTree.IsBcondNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> binopNode = children.get(0);
            TreeNode<Token> bparamNode = children.get(2);

            List<TreeNode<Token>> bParamChildren = bparamNode.getChildren();

            if (bParamChildren.get(0).getData().getWord().equals("SIMPLE")){
                TreeNode<Token> simple1 = bParamChildren.get(0);
                TreeNode<Token> simple2 = bParamChildren.get(2);

                if (TypeOf(binopNode).equals("b")){
                    if (TypeOf(simple1).equals("b")){
                        if (TypeOf(simple2).equals("b")){
                            return "b";
                        }
                    }

                }
                return "u";
            }

            if (bParamChildren.get(0).getData().getWord().equals("ATOMIC")){
                TreeNode<Token> atomic1 = bParamChildren.get(0);
                TreeNode<Token> atomic2 = bParamChildren.get(2);

                if (TypeOf(binopNode).equals("b")){
                    if (TypeOf(atomic1).equals("b")){
                        if (TypeOf(atomic2).equals("b")){
                            return "b";
                        }
                    }

                }

                if (TypeOf(binopNode).equals("c")){
                    if (TypeOf(atomic1).equals("num")){
                        if (TypeOf(atomic2).equals("num")){
                            return "b";
                        }
                    }

                }

                return "u";
            }
            
        }

        if (scopeTree.IsSimpleNode(node)){
            List<TreeNode<Token>> children = node.getChildren();
            TreeNode<Token> binopNode = children.get(0);
            TreeNode<Token> atomic1Node = children.get(2);
            TreeNode<Token> atomic2Node = children.get(4);

            if (TypeOf(binopNode).equals("b")){
                if (TypeOf(atomic1Node).equals("b")){
                    if (TypeOf(atomic2Node).equals("b")){
                        return "b";
                    }

                }
            }

            if (TypeOf(binopNode).equals("c")){
                if (TypeOf(atomic1Node).equals("num")){
                    if (TypeOf(atomic2Node).equals("num")){
                        return "b";
                    }

                }
            }

            return "u";
        }

        throw new RuntimeException("undefined typeof call "+ node.getData().getWord()+"parent:"+node.getParent().getData().getWord());
    }
    
}

