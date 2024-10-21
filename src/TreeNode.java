import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
	private T data; // Will be a Token in our case
	private final List<TreeNode<T>> children;
	private final TreeNode<T> parent;
	private boolean isTerminal;
	private static int idCounter = 0;
	private final int id;

	public TreeNode(T data) {
		this.data = data;
		this.children = new ArrayList<>();
		this.isTerminal = true; // True until a children are added
		this.id = idCounter++;
		this.parent = null;
	}

	// Copy constructor for deep copying nodes (child node gets a new parent)
	private TreeNode(TreeNode<T> other, TreeNode<T> newParent) {
		this.data = other.data;
		this.children = new ArrayList<>();
		this.isTerminal = other.isTerminal;
		this.id = idCounter++;
		this.parent = newParent; // Set the new parent

		// Deep copy the children and set their parent to `this`
		for (TreeNode<T> child : other.children) {
			this.children.add(new TreeNode<>(child, this)); // Deep copy each child
		}
	}

	public void setParent(TreeNode<T> p) {
		if (this.parent != null) {
			throw new IllegalArgumentException("Parent already set");
		}
	}

	public TreeNode<T> getParent() {
		return parent;
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
	// Add a single child, using the copy constructor to make a deep copy
	public void addChild(TreeNode<T> child) {
		// Create a deep copy of the child with `this` as its parent
		TreeNode<T> newChild = new TreeNode<>(child, this);
		this.children.add(newChild);

		if (this.isTerminal) {
			this.isTerminal = false;
		}
	}

	// Add a list of children, using the copy constructor to make deep copies
	public void addChildren(List<TreeNode<T>> children) {
		for (TreeNode<T> child : children) {
			// Create a deep copy of each child with `this` as its parent
			TreeNode<T> newChild = new TreeNode<>(child, this);
			this.children.add(newChild);
		}
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
		System.out.println(prefix + (isTail ? "└── " : "├── ") + data.toString() + "---[" + id + "]---" + "{parent: "+ (parent != null ? parent.getId()+"}": "null"));

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
