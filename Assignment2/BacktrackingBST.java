import org.w3c.dom.Node;

public class BacktrackingBST implements Backtrack, ADTSet<BacktrackingBST.Node> {
    private Stack stack;
    private Stack redoStack;
    BacktrackingBST.Node root = null;


    // Do not change the constructor's signature
    public BacktrackingBST(Stack stack, Stack redoStack) {
        this.stack = stack;
        this.redoStack = redoStack;
    }

    public Node getRoot() {  // O(1)
        return root;
    }

    public Node search(int x) {  // O(n)
        Node y = root;
        while (y != null && y.key != x) {
            if (y.key > x) {
                y = y.left;
            } else {
                y = y.right;
            }
        }
        return y;
    }

    public void insert(BacktrackingBST.Node z) { // O(n)
        // in every 'insert' operation, we will do an ordinary insertion as we do in a BST,
        // and than we will push to the stack 2 objects:
        // 1. the node that inserted
        // 2. '0' - means that the last performed operation was 'insert'
        Node x = root;
        Node y = null;
        while (x != null) {
            y = x;
            if (z.key <= x.key)
                x = x.left;
            else
                x = x.right;
        }
        z.parent = y;
        if (y == null)
            root = z;
        else if (z.key <= y.key)
            y.left = z;
        else
            y.right = z;
        stack.push(z); // push to the stack this new node
        stack.push(0);  // push to the stack '0' - means that the last action performed was 'insert'
    }


    public void delete(Node x) { // O(n)
        // there are 3 cases when we want to delete a node from BST:
        // in case 1 x is a leaf and we will push to the stack first the value of x, than its parent, than we will push 'null' and than '1'-
        // that means the last performed operation was 'delete'
        // in case 2 x has one child and we will push to the stack first the key of x, than its child and '1'- that means
        // the last performed operation was 'delete'
        // in case 3 x has 2 children and we will push to the stack first the minimum in the rooted subtree in x after the modifying,
        // than we will push the key of x, than the successor of x and in the end we will push to the stack '1' - that means
        // the last performed operation was 'delete'
        if (root != null) {
            if (x.left == null & x.right == null) { // case 1: x is a leaf
                int value1 = x.getKey();
                if (x.parent == null)
                    root = null;
                else if (x.getKey() < x.parent.getKey())
                    x.parent.left = null;
                else x.parent.right = null;
                stack.push(value1);
                stack.push(x.parent);
                stack.push(null);
            } else if ((x.left == null & x.right != null) | (x.left != null & x.right == null)) { // case 2: x has 1 children
                if (x.left == null) {
                    stack.push(x.key);
                    x.key = x.right.key;
                    x.right = null;
                    stack.push(x);
                } else {
                    stack.push(x.key);
                    x.key = x.left.key;
                    x.left = null;
                    stack.push(x);
                }
            } else { // case 3: x has 2 children
                int a = x.key;
                Node tmp = successor(x);
                if (tmp.parent.getKey() > tmp.getKey())
                    tmp.parent.left = null;
                else tmp.parent.right = null;
                x.key = tmp.getKey();
                stack.push(minimum(x.right)); //O(n)
                stack.push(a);
                stack.push(x);
            }
            stack.push(1); // push to the stack '1' - means that the last action performed was 'delete'
        }
    }

    private Node minimum(Node x) { // O(n)
        if (x != null) {
            while (x.left != null) {
                x = x.left;
            }
            return x;
        }
        return null;
    }

    public Node minimum() { // O(n)
        Node min = root;
        if (root != null) {
            while (min.left != null) {
                min = min.left;
            }
            return min;
        }
        return null;
    }

    public Node maximum() { // O(n)
        Node max = root;
        if (root != null) {
            while (max.right != null) {
                max = max.right;
            }
            return max;
        }
        return null;
    }

    public Node successor(Node x) {  // O(n)
        Node suc;
        if (x.right != null) {
            suc = x.right;
            while (suc.left != null) {
                suc = suc.left;
            }
            // we need to look at the right rooted subtree in x and return the minimum
        } else { //  the successor is the lowest ancestor of x
            // whose left child is also an ancestor of x
            suc = x.parent;
            while (suc != null && x == suc.right) {
                x = suc;
                suc = suc.parent;
            }
        }
        return suc;
    }

    public Node predecessor(Node x) {  // O(n)
        Node pre;
        if (x.left != null) {
            pre = x.left;
            while (pre.right != null) {
                pre = pre.right;
            }
            // we need to look at the left rooted subtree in x and return thr maximum
        } else { //  the predecessor is the highest ancestor of x
            // whose right child is also an ancestor of x
            pre = x.parent;
            while (pre != null && x == pre.left) {
                x = pre;
                pre = pre.parent;
            }
        }
        return pre;
    }

    @Override
    public void backtrack() { // O(1)
        if (!stack.isEmpty()) {
            int action = (int) stack.pop();
            if (action == 0) { // last action performed was 'insert'
                BacktrackingBST.Node node = (BacktrackingBST.Node) stack.pop(); // if we want to delete a node right after we
                // insert, we will delete this node as we delete a leaf- so after we pulled out the relevant node from the stack,
                // we will update its parent left/right pointer to null (if this node is the only node that exists in the tree,
                // after the modifying the root is null
                int value1 = node.getKey();
                if (node.parent == null)
                    root = null;
                else if (node.getKey() < node.parent.getKey())
                    node.parent.left = null;
                else node.parent.right = null;
                redoStack.push(value1);
                redoStack.push(node.parent);
                redoStack.push(1);

            } else { // last action performed was 'delete' - in delete the are 3 cases:
                BacktrackingBST.Node suc = (BacktrackingBST.Node) stack.pop();
                if (suc == null) { // case 1: the node is a leaf
                    BacktrackingBST.Node suc1 = (BacktrackingBST.Node) stack.pop(); // this node is the parent of the node
                    // that we want to insert to the tree
                    int data = (int) stack.pop(); // the key of the node that we want to insert

                    if (suc1.getKey() < data) { // by comparing the keys of the two nodes, we will know which pointer (left/right)
                        // of suc1 (the parent of the node we want to insert) will point to a node with the key of the node
                        // we want to insert
                        suc1.right = new BacktrackingBST.Node(data, null);
                    } else {
                        suc1.left = new BacktrackingBST.Node(data, null);
                    }
                    redoStack.push(data);
                    redoStack.push(suc1);
                    redoStack.push(null);
                } else if (suc.left == null && suc.right == null) { // case 2: the node has 1 child
                    int sucValue = suc.getKey(); // the key of parent of the node we want to insert
                    int data = ((int) stack.pop()); // the key of the node we want to insert
                    suc.key = data; // we replace between the keys
                    if (sucValue < data) { // by comparing the keys of the two nodes, we will know which pointer (left/right)
                        // of suc (after we change the data, now suc is the node that we inserted) will point to a node with the key
                        // of sucValue (the key of the node that before the insertion took the place of the node we deleted)
                        suc.left = new BacktrackingBST.Node(sucValue, null);
                    } else {
                        suc.right = new BacktrackingBST.Node(sucValue, null);
                    }
                    redoStack.push(sucValue);
                    redoStack.push(suc);
                } else { // case 3: the node has 2 children
                    int sucValue = suc.getKey(); // the key of the successor of the node we want to insert (before we deleted it)
                    suc.key = ((int) stack.pop()); // the key of the node we want to insert
                    BacktrackingBST.Node sucParent = (BacktrackingBST.Node) stack.pop(); // the parent of the successor of the node we
                    // want to insert (before we deleted it), also we can say that this node is the minimum of the rooted subtree in
                    // the node suc before we changed the keys
                    if (sucParent == null) {
                        suc.right = new BacktrackingBST.Node(sucValue, null);
                    } else {
                        sucParent.left = new BacktrackingBST.Node(sucValue, null);
                    }
                    redoStack.push(sucParent);
                    redoStack.push(sucValue);
                    redoStack.push(suc);

                }
                redoStack.push(0);
            }
            System.out.print("backtracking performed");
        }
    }

    @Override
    public void retrack() {
        if (!redoStack.isEmpty()) {
            int action = (int) redoStack.pop();
            if (action == 1) { // means that the last performed operation was 'delete'
                Node node = (Node) redoStack.pop(); // the node we want to insert back to the tree
                if (node == null) { // if the tree is empty
                    root = new BacktrackingBST.Node((int) redoStack.pop(), null);
                } else {
                    int keyOfNode = node.getKey(); // the key of the parent of the node we want to insert
                    int keyOfLeaf = (int) redoStack.pop(); // the key of the node we want to insert
                    if (keyOfLeaf < keyOfNode) { // // by comparing the keys of the two nodes, we will know which pointer (left/right)
                        // of node (the parent of the node we want to insert) will point to a node with the key
                        // of the node we want to insert
                        node.left = (new BacktrackingBST.Node(keyOfLeaf, null));
                    } else {
                        node.right = (new BacktrackingBST.Node(keyOfLeaf, null));
                    }
                }
            } else { // means that last performed operation was 'insert'
                BacktrackingBST.Node node = (BacktrackingBST.Node) redoStack.pop();
                if (node == null) { // case 1: the node we want to delete is a leaf
                    BacktrackingBST.Node node1 = (BacktrackingBST.Node) redoStack.pop(); // this node is the parent of the
                    // node we want to delete from the tree
                    int keyOfNode1 = node1.getKey(); // the key of the parent of the node we want to delete
                    int keyOfLeaf = (int) redoStack.pop(); // the key of the node we want to delete
                    if (keyOfLeaf < keyOfNode1) { // by comparing the keys of the two nodes, we will know which pointer (left/right)
                        // of node1 (the parent of the node we want to delete) will point to null;
                        node1.left = null;
                    } else node1.right = null;
                } else if ((node.left != null && node.right == null) | (node.left == null && node.right != null)) { // case 2:
                    // the node we want to delete has 1 child
                    int keyOfNode = node.getKey(); // the key of the node we want to delete
                    int keyValue = (int) redoStack.pop(); // the key of the child of the node we want to delete
                    if (keyValue < keyOfNode) { // by comparing the keys of the two nodes, we will know which pointer (left/right)
                        // of node (after we change the data, now node is the child of the node we deleted) will point to null
                        node.key = keyValue;
                        node.left = null;
                    } else {
                        node.key = keyValue;
                        node.right = null;
                    }
                } else { //case 3: the node we want to delete has 2 children
                    node.key = (int) redoStack.pop(); // we change the key of the node we want to delete to the key of
                    // its successor
                    BacktrackingBST.Node node2 = (BacktrackingBST.Node) redoStack.pop(); // this node is the parent of the successor
                    // of the node we want to delete
                    if (node2 == null)
                        node.right = null;
                    else
                        node2.left = null; // after the successor took the place of the node we want to delete, we need to update
                    // that its parent left pinter point to null
                }
            }
        }
    }

    public void printPreOrder() {
        print();
    }

    @Override
    public void print() { //print Pre-Order
        if (root != null) {
            root.printPreOrder();
        }
    }

    //-----------------------------REMOVE--------------------------------

    public static class Node {
        //These fields are public for grading purposes. By coding conventions and best practice they should be private.
        public BacktrackingBST.Node left;
        public BacktrackingBST.Node right;

        private BacktrackingBST.Node parent;
        private int key;
        private Object value;

        public Node(int key, Object value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public void printPreOrder() {
            System.out.print(key);
            if (left != null) {
                System.out.print(" ");
                left.printPreOrder();
            }
            if (right != null) {
                System.out.print(" ");
                right.printPreOrder();
            }
        }
    }


}