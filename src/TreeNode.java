import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
    private T data;  // Will be a Token in our case
    private List<TreeNode<T>> children;

    public TreeNode(T data) {
        this.data = data;
        this.children = new ArrayList<>();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // Order of addition is respected
    public void addChild(TreeNode<T> child) {
        this.children.add(child);
    }

    public void addChildren(List<TreeNode<T>> children) {
        this.children.addAll(children);
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public void printTree(String prefix) {
        System.out.println(prefix + data);
        for (TreeNode<T> child : children) {
            child.printTree(prefix + "  ");
        }
    }
}
