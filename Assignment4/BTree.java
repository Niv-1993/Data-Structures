import java.util.Arrays;
import java.util.Comparator;

@SuppressWarnings("unchecked")
public class BTree<T extends Comparable<T>> {

    // Default to 2-3 Tree
    private int minKeySize = 1; //1
    private int minChildrenSize = minKeySize + 1; // 2
    private int maxKeySize = 2 * minKeySize + 1; // NEW //3
    private int maxChildrenSize = maxKeySize + 1; // 4

    private Node<T> root = null;
    private int size = 0;

    /**
     * Constructor for B-Tree which defaults to a 2-3 B-Tree.
     */
    public BTree() {
    }

    /**
     * Constructor for B-Tree of ordered parameter. Order here means minimum
     * number of keys in a non-root node.
     *
     * @param order of the B-Tree.
     */
    public BTree(int order) { //order = t
        this.minKeySize = order - 1; // t -1 0 added -1 to make it good like in lecture -NEW
        this.minChildrenSize = minKeySize + 1; // t
        this.maxKeySize = 2 * minKeySize + 1; //  NEW
        this.maxChildrenSize = maxKeySize + 1; // 2t
    }

    //Task 2.1
    public boolean insert(T value) {
        if (root == null) { //empty tree
            root = new Node<T>(null, maxKeySize, maxChildrenSize);
            root.addKey(value);
        } else {
            Node<T> node = root;
            while (node != null) {
                if (node.numberOfKeys() == maxKeySize) { //new
                    split(node);
                    node = node.parent;

                }
                if (node.numberOfChildren() == 0) { //leaf
                    if (node.numberOfKeys() < maxKeySize) {
                        node.addKey(value);
                        break;
                    } else {
                        split(node);
                        node = node.parent;
                    }
                }
                // Lesser or equal
                T lesser = node.getKey(0);
                if (value.compareTo(lesser) <= 0) {
                    node = node.getChild(0);
                    continue;
                }
                // Greater
                int numberOfKeys = node.numberOfKeys();
                int last = numberOfKeys - 1;
                T greater = node.getKey(last);
                if (value.compareTo(greater) > 0) {
                    node = node.getChild(numberOfKeys);
                    continue;
                }
                // Search internal nodes
                for (int i = 1; i < node.numberOfKeys(); i++) {
                    T prev = node.getKey(i - 1);
                    T next = node.getKey(i);
                    if (value.compareTo(prev) > 0 && value.compareTo(next) <= 0) {
                        node = node.getChild(i);
                        break;
                    }
                }
            }
        }
        size++;
        return true;
    }


    public T delete(T value) {
        T removed = null;
        Node<T> node = this.getNode(value,root); //NEW
        removed = remove(value, node);
        return removed;
    }

    //Task 2.2
    public boolean insert2pass(T value) { //log t(N)
        if (root == null) { //empty tree
            root = new Node<T>(null, maxKeySize, maxChildrenSize);
            root.addKey(value);
            return true;
        }
        Node<T> nodeToInsertIn = searchNode(value); //log t(N)
        if (nodeToInsertIn.numberOfKeys() == maxKeySize) {
            splitAncestor(nodeToInsertIn);
        }
        nodeToInsertIn = searchNode(value); //log t(N)
        nodeToInsertIn.addKey(value);
        return true;
    }

    private boolean splitAncestor(Node<T> node) {
        if (node == root & root.numberOfKeys() == maxKeySize) {
            split(node);
            return true;
        }
        if (node.numberOfKeys() < maxKeySize) {
            return true;
        } else {
            splitAncestor(node.parent);
            split(node);
        }
        return true;
    }

    private Node<T> searchNode(T value) {
        Node<T> node = root;
        Node<T> prev = root;
        while (node != null) {
            T lesser = node.getKey(0);
            if (value.compareTo(lesser) < 0) {
                if (node.numberOfChildren() > 0) {
                    prev = node.getChild(0);
                    node = node.getChild(0);
                } else
                    node = null;
                continue;
            }
            int numberOfKeys = node.numberOfKeys();
            int last = numberOfKeys - 1;
            T greater = node.getKey(last);
            if (value.compareTo(greater) > 0) {
                if (node.numberOfChildren() > numberOfKeys) {
                    prev = node.getChild(numberOfKeys);
                    node = node.getChild(numberOfKeys);
                } else
                    node = null;
                continue;
            }
            if (prev.numberOfChildren() == 0)
                return prev;

            for (int i = 0; i < numberOfKeys; i++) {
                T currentValue = node.getKey(i);
                int next = i + 1;
                if (next <= last) {
                    T nextValue = node.getKey(next);
                    if (currentValue.compareTo(value) < 0 && nextValue.compareTo(value) > 0) {
                        if (next < node.numberOfChildren()) {
                            prev = node.getChild(next);
                            node = node.getChild(next);
                            break;
                        }
                    }
                }
            }
        }
        return prev;
    }

    /**
     * {@inheritDoc}
     */
    public boolean add(T value) {
        if (root == null) { //empty tree
            root = new Node<T>(null, maxKeySize, maxChildrenSize);
            root.addKey(value);
        } else {
            Node<T> node = root;
            while (node != null) {
                if (node.numberOfChildren() == 0) { //leaf
                    node.addKey(value);
                    if (node.numberOfKeys() <= maxKeySize) {
                        // A-OK
                        break;
                    }
                    // Need to split up
                    split(node);
                    break;
                }
                // Navigate

                // Lesser or equal
                T lesser = node.getKey(0);
                if (value.compareTo(lesser) <= 0) {
                    node = node.getChild(0);
                    continue;
                }

                // Greater
                int numberOfKeys = node.numberOfKeys();
                int last = numberOfKeys - 1;
                T greater = node.getKey(last);
                if (value.compareTo(greater) > 0) {
                    node = node.getChild(numberOfKeys);
                    continue;
                }

                // Search internal nodes
                for (int i = 1; i < node.numberOfKeys(); i++) {
                    T prev = node.getKey(i - 1);
                    T next = node.getKey(i);
                    if (value.compareTo(prev) > 0 && value.compareTo(next) <= 0) {
                        node = node.getChild(i);
                        break;
                    }
                }
            }
        }

        size++;

        return true;
    }

    /**
     * The node's key size is greater than maxKeySize, split down the middle.
     *
     * @param nodeToSplit to split.
     */
    private void split(Node<T> nodeToSplit) {
        Node<T> node = nodeToSplit;
        int numberOfKeys = node.numberOfKeys();
        int medianIndex = numberOfKeys / 2;
        T medianValue = node.getKey(medianIndex);

        Node<T> left = new Node<T>(null, maxKeySize, maxChildrenSize);
        for (int i = 0; i < medianIndex; i++) {
            left.addKey(node.getKey(i));
        }
        if (node.numberOfChildren() > 0) {
            for (int j = 0; j <= medianIndex; j++) {
                Node<T> c = node.getChild(j);
                left.addChild(c);
            }
        }

        Node<T> right = new Node<T>(null, maxKeySize, maxChildrenSize);
        for (int i = medianIndex + 1; i < numberOfKeys; i++) {
            right.addKey(node.getKey(i));
        }
        if (node.numberOfChildren() > 0) {
            for (int j = medianIndex + 1; j < node.numberOfChildren(); j++) {
                Node<T> c = node.getChild(j);
                right.addChild(c);
            }
        }

        if (node.parent == null) {
            // new root, height of tree is increased
            Node<T> newRoot = new Node<T>(null, maxKeySize, maxChildrenSize);
            newRoot.addKey(medianValue);
            node.parent = newRoot;
            root = newRoot;
            node = root;
            node.addChild(left);
            node.addChild(right);
        } else {
            // Move the median value up to the parent
            Node<T> parent = node.parent;
            parent.addKey(medianValue);
            parent.removeChild(node);
            parent.addChild(left);
            parent.addChild(right);

            if (parent.numberOfKeys() > maxKeySize) split(parent);
        }
    }

    /**
     * {@inheritDoc}
     */
    public T remove(T value) {
        T removed = null;
        Node<T> node = this.getNode(value,root);
        removed = remove(value, node);
        return removed;
    }

    /**
     * Remove the value from the Node and check invariants
     *
     * @param value T to remove from the tree
     * @param node  Node to remove value from
     * @return True if value was removed from the tree.
     */
    private T remove(T value, Node<T> node) {
        if (node == null) return null; // In case the tree is empty

        T removed = null;
        if (node.numberOfChildren() == 0) { // In case the current node is a leaf- case 1:
            if (node.parent != null) { // In case the leaf is not the root
                removed = node.removeKey(value); // We removed the required value from the proper node (we already know
                // that this node has at least t keys because of the function 'getNode')
            } else if (node == root && node.numberOfKeys() == 1) { // In case the leaf is the root and its contains 1 key
                root = null;
            } else if (node == root && node.numberOfKeys() > 1) { // In case the leaf is the root and its contains more than 1 key
                removed = node.removeKey(value);
            }
        } else {
            // those cases are relevant for internal nodes
            int index = node.indexOf(value);
            Node<T> leftChild = node.getChild(index);
            Node<T> rightChild = node.getChild(index + 1);
            if (leftChild.numberOfKeys() > minKeySize) { // Case 2- in that case the required node has left son that contains
                // at least t keys
                Node<T> pre = this.getGreatestNode(leftChild); // We are looking for the node that contains the predecessor of value
                T predecessor = pre.getKey(pre.keysSize - 1); // We save the value of the predecessor of value
                removed = value;
                node.keys[index] = predecessor; // Override - it means that we replace value with the predecessor's value
                pre = getNode(predecessor,leftChild); // We are looking for the node that contains the predecessor of value by
                // beginning from the left child to the leaf and all the entire route we combined the nodes that have t-1 keys
                remove(predecessor, pre); // We remove in a recursive way from case 1 the second appearance of the predecessor
            } else if (rightChild.numberOfKeys() > minKeySize) { // Case 3- in that case the required has right son that contains
                // at least t keys
                Node<T> suc = this.getSmallestNode(rightChild); // We are looking for the node that contains the successor of value
                T successor = suc.getKey(0); // We save the value of the successor of value
                removed = value;
                node.keys[index] = successor; // Override - it means that we replace value with the successor's value
                suc = getNode(successor,rightChild); // We are looking for the node that contains the successor of value by
                // beginning from the right child to the leaf and all the entire route we combined the nodes that have t-1 keys
                remove(successor, suc); // We remove in a recursive way from case 1 the second appearance of the successor
            } else { // Case 4- in that case both of the required node's son have t-1 keys
                mergeWithLeft(rightChild); // we have a priority to merge with the left child
                remove(value,rightChild);
            }
        }
        size--;
        return removed;
    }

    private void mergeWithLeft(Node<T> node) { // NEW
        Node<T> parent = node.parent;
        int index = parent.indexOf(node); // Where is the location of the node in its parent children's array
        int indexOfLeftNeighbor = index - 1;
        Node<T> leftNeighbor = parent.getChild(indexOfLeftNeighbor);
        // combined with left neighbor
        T removeValue = leftNeighbor.getKey(leftNeighbor.numberOfKeys() - 1);
        int prev = getIndexOfNextValue(parent, removeValue);
        T parentValue = parent.removeKey(prev);
        parent.removeChild(leftNeighbor);
        node.addKey(parentValue);
        for (int i = 0; i < leftNeighbor.keysSize; i++) {
            T v = leftNeighbor.getKey(i);
            node.addKey(v);
        }
        for (int i = 0; i < leftNeighbor.childrenSize; i++) {
            Node<T> c = leftNeighbor.getChild(i);
            node.addChild(c);
        }
        if (parent.parent != null && parent.numberOfKeys() < minKeySize) { // In case the remove operation cause the parent
            // to contain less than t-1 keys
            this.combined(parent);
        } else if (parent.numberOfKeys() == 0) { // In that case the parent has no keys at all, so it will become the new root
            node.parent = null;
            root = node;
        }
    }

    /**
     * Remove greatest valued key from node.
     *
     * @param node to remove greatest value from.
     * @return value removed;
     */
    private T removeGreatestValue(Node<T> node) {
        T value = null;
        if (node.numberOfKeys() > 0) {
            value = node.removeKey(node.numberOfKeys() - 1);
        }
        return value;
    }

    private T removeSmallestValue(Node<T> node) {
        T value = null;
        if (node.numberOfKeys() > 0) {
            value = node.removeKey(0);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(T value) {
        Node<T> node = getNode(value,root);
        return (node != null);
    }

    /**
     * Get the node with value.
     *
     * @param value to find in the tree.
     * @return Node<T> with value.
     */
    private Node<T> getNode(T value, Node<T> fromNode) { //NEW
        Node<T> node = fromNode;
        while (node != null) {
            if (node.numberOfKeys() == minKeySize && node != root) { //NEW
                this.combined(node);//NEW
            }
            T lesser = node.getKey(0);
            if (value.compareTo(lesser) < 0) {
                if (node.numberOfChildren() > 0)
                    node = node.getChild(0);
                else
                    node = null;
                continue;
            }

            int numberOfKeys = node.numberOfKeys();
            int last = numberOfKeys - 1;
            T greater = node.getKey(last);
            if (value.compareTo(greater) > 0) {
                if (node.numberOfChildren() > numberOfKeys)
                    node = node.getChild(numberOfKeys);
                else
                    node = null;
                continue;
            }

            for (int i = 0; i < numberOfKeys; i++) {
                T currentValue = node.getKey(i);
                if (currentValue.compareTo(value) == 0) {
                    return node;
                }

                int next = i + 1;
                if (next <= last) {
                    T nextValue = node.getKey(next);
                    if (currentValue.compareTo(value) < 0 && nextValue.compareTo(value) > 0) {
                        if (next < node.numberOfChildren()) {
                            node = node.getChild(next);
                            break;
                        }
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the greatest valued child from node.
     *
     * @param nodeToGet child with the greatest value.
     * @return Node<T> child with greatest value.
     */
    private Node<T> getGreatestNode(Node<T> nodeToGet) {
        Node<T> node = nodeToGet;
        while (node.numberOfChildren() > 0) {
            node = node.getChild(node.numberOfChildren() - 1);
        }
        return node;
    }

    private Node<T> getSmallestNode(Node<T> nodeToGet) {
        Node<T> node = nodeToGet;
        while (node.numberOfChildren() > 0) {
            node = node.getChild(0);
        }
        return node;
    }

    /**
     * Combined children keys with parent when size is less than minKeySize.
     *
     * @param node with children to combined.
     * @return True if combined successfully.
     */
    private boolean combined(Node<T> node) {
        Node<T> parent = node.parent;
        int index = parent.indexOf(node);
        int indexOfLeftNeighbor = index - 1;
        int indexOfRightNeighbor = index + 1;


        Node<T> leftNeighbor = null;
        int leftNeighborSize = -minChildrenSize;
        if (indexOfLeftNeighbor >= 0) {
            leftNeighbor = parent.getChild(indexOfLeftNeighbor);
            leftNeighborSize = leftNeighbor.numberOfKeys();
        }
        if (leftNeighbor != null && leftNeighborSize > minKeySize) {
            // Try to borrow from left neighbor
            T removeValue = leftNeighbor.getKey(leftNeighbor.numberOfKeys() - 1);
            int prev = getIndexOfNextValue(parent, removeValue);
            T parentValue = parent.removeKey(prev);
            T neighborValue = leftNeighbor.removeKey(leftNeighbor.numberOfKeys() - 1);
            node.addKey(parentValue);
            parent.addKey(neighborValue);
            if (leftNeighbor.numberOfChildren() > 0) {
                node.addChild(leftNeighbor.removeChild(leftNeighbor.numberOfChildren() - 1));
            }
        } else {
            Node<T> rightNeighbor = null;
            int rightNeighborSize = -minChildrenSize;
            if (indexOfRightNeighbor < parent.numberOfChildren()) {
                rightNeighbor = parent.getChild(indexOfRightNeighbor);
                rightNeighborSize = rightNeighbor.numberOfKeys();
            }
            // Try to borrow neighbor
            if (rightNeighbor != null && rightNeighborSize > minKeySize) {
                // Try to borrow from right neighbor
                T removeValue = rightNeighbor.getKey(0);
                int prev = getIndexOfPreviousValue(parent, removeValue);
                T parentValue = parent.removeKey(prev);
                T neighborValue = rightNeighbor.removeKey(0);
                node.addKey(parentValue);
                parent.addKey(neighborValue);
                if (rightNeighbor.numberOfChildren() > 0) {
                    node.addChild(rightNeighbor.removeChild(0));
                }
            } else if (leftNeighbor != null && parent.numberOfKeys() > 0) {
                // Can't borrow from neighbors, try to combined with left neighbor
                T removeValue = leftNeighbor.getKey(leftNeighbor.numberOfKeys() - 1);
                int prev = getIndexOfNextValue(parent, removeValue);
                T parentValue = parent.removeKey(prev);
                parent.removeChild(leftNeighbor);
                node.addKey(parentValue);
                for (int i = 0; i < leftNeighbor.keysSize; i++) {
                    T v = leftNeighbor.getKey(i);
                    node.addKey(v);
                }
                for (int i = 0; i < leftNeighbor.childrenSize; i++) {
                    Node<T> c = leftNeighbor.getChild(i);
                    node.addChild(c);
                }
                if (parent.parent != null && parent.numberOfKeys() < minKeySize) {
                    // removing key made parent too small, combined up tree
                    this.combined(parent);
                } else if (parent.numberOfKeys() == 0) {
                    // parent no longer has keys, make this node the new root
                    // which decreases the height of the tree
                    node.parent = null;
                    root = node;
                }
            } else if (rightNeighbor != null && parent.numberOfKeys() > 0) {
                // Can't borrow from neighbors, try to combined with right neighbor
                T removeValue = rightNeighbor.getKey(0);
                int prev = getIndexOfPreviousValue(parent, removeValue);
                T parentValue = parent.removeKey(prev);
                parent.removeChild(rightNeighbor);
                node.addKey(parentValue);
                for (int i = 0; i < rightNeighbor.keysSize; i++) {
                    T v = rightNeighbor.getKey(i);
                    node.addKey(v);
                }
                for (int i = 0; i < rightNeighbor.childrenSize; i++) {
                    Node<T> c = rightNeighbor.getChild(i);
                    node.addChild(c);
                }

                if (parent.parent != null && parent.numberOfKeys() < minKeySize) {
                    // removing key made parent too small, combined up tree
                    this.combined(parent);
                } else if (parent.numberOfKeys() == 0) {
                    // parent no longer has keys, make this node the new root
                    // which decreases the height of the tree
                    node.parent = null;
                    root = node;
                }
            }
        }
        return true;
    }

    /**
     * Get the index of previous key in node.
     *
     * @param node  to find the previous key in.
     * @param value to find a previous value for.
     * @return index of previous key or -1 if not found.
     */
    private int getIndexOfPreviousValue(Node<T> node, T value) {
        for (int i = 1; i < node.numberOfKeys(); i++) {
            T t = node.getKey(i);
            if (t.compareTo(value) >= 0)
                return i - 1;
        }
        return node.numberOfKeys() - 1;
    }

    /**
     * Get the index of next key in node.
     *
     * @param node  to find the next key in.
     * @param value to find a next value for.
     * @return index of next key or -1 if not found.
     */
    private int getIndexOfNextValue(Node<T> node, T value) {
        for (int i = 0; i < node.numberOfKeys(); i++) {
            T t = node.getKey(i);
            if (t.compareTo(value) >= 0)
                return i;
        }
        return node.numberOfKeys() - 1;
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    public boolean validate() {
        if (root == null) return true;
        return validateNode(root);
    }

    /**
     * Validate the node according to the B-Tree invariants.
     *
     * @param node to validate.
     * @return True if valid.
     */
    private boolean validateNode(Node<T> node) {
        int keySize = node.numberOfKeys();
        if (keySize > 1) {
            // Make sure the keys are sorted
            for (int i = 1; i < keySize; i++) {
                T p = node.getKey(i - 1);
                T n = node.getKey(i);
                if (p.compareTo(n) > 0)
                    return false;
            }
        }
        int childrenSize = node.numberOfChildren();
        if (node.parent == null) {
            // root
            if (keySize > maxKeySize) {
                // check max key size. root does not have a min key size
                return false;
            } else if (childrenSize == 0) {
                // if root, no children, and keys are valid
                return true;
            } else if (childrenSize < 2) {
                // root should have zero or at least two children
                return false;
            } else if (childrenSize > maxChildrenSize) {
                return false;
            }
        } else {
            // non-root
            if (keySize < minKeySize) {
                return false;
            } else if (keySize > maxKeySize) {
                return false;
            } else if (childrenSize == 0) {
                return true;
            } else if (keySize != (childrenSize - 1)) {
                // If there are chilren, there should be one more child then
                // keys
                return false;
            } else if (childrenSize < minChildrenSize) {
                return false;
            } else if (childrenSize > maxChildrenSize) {
                return false;
            }
        }

        Node<T> first = node.getChild(0);
        // The first child's last key should be less than the node's first key
        if (first.getKey(first.numberOfKeys() - 1).compareTo(node.getKey(0)) > 0)
            return false;

        Node<T> last = node.getChild(node.numberOfChildren() - 1);
        // The last child's first key should be greater than the node's last key
        if (last.getKey(0).compareTo(node.getKey(node.numberOfKeys() - 1)) < 0)
            return false;

        // Check that each node's first and last key holds it's invariance
        for (int i = 1; i < node.numberOfKeys(); i++) {
            T p = node.getKey(i - 1);
            T n = node.getKey(i);
            Node<T> c = node.getChild(i);
            if (p.compareTo(c.getKey(0)) > 0)
                return false;
            if (n.compareTo(c.getKey(c.numberOfKeys() - 1)) < 0)
                return false;
        }

        for (int i = 0; i < node.childrenSize; i++) {
            Node<T> c = node.getChild(i);
            boolean valid = this.validateNode(c);
            if (!valid)
                return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return TreePrinter.getString(this);
    }


    public static class Node<T extends Comparable<T>> {

        private T[] keys = null;
        private int keysSize = 0;
        private Node<T>[] children = null;
        private int childrenSize = 0;
        private Comparator<Node<T>> comparator = new Comparator<Node<T>>() {
            public int compare(Node<T> arg0, Node<T> arg1) {
                return arg0.getKey(0).compareTo(arg1.getKey(0));
            }
        };

        protected Node<T> parent = null;

        private Node(Node<T> parent, int maxKeySize, int maxChildrenSize) {
            this.parent = parent;
            this.keys = (T[]) new Comparable[maxKeySize + 1];
            this.keysSize = 0;
            this.children = new Node[maxChildrenSize + 1];
            this.childrenSize = 0;
        }

        private T getKey(int index) {
            return keys[index];
        }

        private int indexOf(T value) {
            for (int i = 0; i < keysSize; i++) {
                if (keys[i].equals(value)) return i;
            }
            return -1;
        }

        private void addKey(T value) {
            keys[keysSize++] = value;
            Arrays.sort(keys, 0, keysSize);
        }

        private T removeKey(T value) {
            T removed = null;
            boolean found = false;
            if (keysSize == 0) return null;
            for (int i = 0; i < keysSize; i++) {
                if (keys[i].equals(value)) {
                    found = true;
                    removed = keys[i];
                } else if (found) {
                    // shift the rest of the keys down
                    keys[i - 1] = keys[i];
                }
            }
            if (found) {
                keysSize--;
                keys[keysSize] = null;
            }
            return removed;
        }

        private T removeKey(int index) {
            if (index >= keysSize)
                return null;
            T value = keys[index];
            for (int i = index + 1; i < keysSize; i++) {
                // shift the rest of the keys down
                keys[i - 1] = keys[i];
            }
            keysSize--;
            keys[keysSize] = null;
            return value;
        }

        private int numberOfKeys() {
            return keysSize;
        }

        private Node<T> getChild(int index) {
            if (index >= childrenSize)
                return null;
            return children[index];
        }

        private int indexOf(Node<T> child) {
            for (int i = 0; i < childrenSize; i++) {
                if (children[i].equals(child))
                    return i;
            }
            return -1;
        }

        private boolean addChild(Node<T> child) {
            child.parent = this;
            children[childrenSize++] = child;
            Arrays.sort(children, 0, childrenSize, comparator);
            return true;
        }

        private boolean removeChild(Node<T> child) {
            boolean found = false;
            if (childrenSize == 0)
                return found;
            for (int i = 0; i < childrenSize; i++) {
                if (children[i].equals(child)) {
                    found = true;
                } else if (found) {
                    // shift the rest of the keys down
                    children[i - 1] = children[i];
                }
            }
            if (found) {
                childrenSize--;
                children[childrenSize] = null;
            }
            return found;
        }

        private Node<T> removeChild(int index) {
            if (index >= childrenSize)
                return null;
            Node<T> value = children[index];
            children[index] = null;
            for (int i = index + 1; i < childrenSize; i++) {
                // shift the rest of the keys down
                children[i - 1] = children[i];
            }
            childrenSize--;
            children[childrenSize] = null;
            return value;
        }

        private int numberOfChildren() {
            return childrenSize;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            builder.append("keys=[");
            for (int i = 0; i < numberOfKeys(); i++) {
                T value = getKey(i);
                builder.append(value);
                if (i < numberOfKeys() - 1)
                    builder.append(", ");
            }
            builder.append("]\n");

            if (parent != null) {
                builder.append("parent=[");
                for (int i = 0; i < parent.numberOfKeys(); i++) {
                    T value = parent.getKey(i);
                    builder.append(value);
                    if (i < parent.numberOfKeys() - 1)
                        builder.append(", ");
                }
                builder.append("]\n");
            }

            if (children != null) {
                builder.append("keySize=").append(numberOfKeys()).append(" children=").append(numberOfChildren()).append("\n");
            }

            return builder.toString();
        }
    }

    private static class TreePrinter {

        public static <T extends Comparable<T>> String getString(BTree<T> tree) {
            if (tree.root == null) return "Tree has no nodes.";
            return getString(tree.root, "", true);
        }

        private static <T extends Comparable<T>> String getString(Node<T> node, String prefix, boolean isTail) {
            StringBuilder builder = new StringBuilder();

            builder.append(prefix).append((isTail ? "????????? " : "????????? "));
            for (int i = 0; i < node.numberOfKeys(); i++) {
                T value = node.getKey(i);
                builder.append(value);
                if (i < node.numberOfKeys() - 1)
                    builder.append(", ");
            }
            builder.append("\n");

            if (node.children != null) {
                for (int i = 0; i < node.numberOfChildren() - 1; i++) {
                    Node<T> obj = node.getChild(i);
                    builder.append(getString(obj, prefix + (isTail ? "    " : "???   "), false));
                }
                if (node.numberOfChildren() >= 1) {
                    Node<T> obj = node.getChild(node.numberOfChildren() - 1);
                    builder.append(getString(obj, prefix + (isTail ? "    " : "???   "), true));
                }
            }

            return builder.toString();
        }
    }

}