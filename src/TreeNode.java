import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
    private T data;  // Will be a Token in our case
    private final List<TreeNode<T>> children;
	private boolean isTerminal;
	private static int idCounter = 0;
	private final int id;

	public TreeNode(T data) {
		this.data = data;
		this.children = new ArrayList<>();
		this.isTerminal = true; // True until a children are added
		this.id = idCounter++;
	}

	public int getId() {
		return id;
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
		if (this.isTerminal) {
			this.isTerminal = false;
		}
    }

    public void addChildren(List<TreeNode<T>> children) {
        this.children.addAll(children);
		if (this.isTerminal) {
			this.isTerminal = false;
		}
    }

    public List<TreeNode<T>> getChildren() {
		// if chidren is empty, return null
		if (children.isEmpty()) {
			return null;
		}
		return children;
    }

    public boolean hasChildren() {
        return children.isEmpty();
    }

	// Print the tree in a readable format
	// Prefix is used to indent the tree
	public void printTree(String prefix) {
		// Null check for data
		if (data == null) {
			return;
		}

		// Print the current node's data
		System.out.println(prefix + data);

		// Null check for children
		if (children == null) {
			return;
		}

		// Iterate over children and print them
		for (TreeNode<T> child : children) {
			child.printTree(prefix + "  ");
		}
	}

	public void printTree(String prefix, boolean isTail) {
        // Print the current node's data (will use Token's toString method)
        System.out.println(prefix + (isTail ? "└── " : "├── ") + data.toString());

        // Iterate over the children
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                // Determine if the child is the last one
                boolean isLast = (i == children.size() - 1);
                // Recursively print the children with updated prefix
                children.get(i).printTree(prefix + (isTail ? "    " : "│   "), isLast);
            }
        }
    }
}
