import java.util.*;
import java.lang.*;

// The BPlusTree class. You'll need to fill the methods in. DO NOT change the
// function signatures of these methods. Our checker relies on these methods and
// assume these function signatures.

public class BPlusTree {

    // A tree has a root node, and an order
    public Node root;
    private Integer order;
    private boolean isSecondary;
    private Integer minKey = Integer.MAX_VALUE;

    // Required methods to implement. DO NOT change the function signatures of
    // these methods.

    // Instantiate a BPlusTree with a specific order
    public BPlusTree(Integer order) {
        root = new LNode(order);
        this.order = order;
    }

    public BPlusTree(Integer order, boolean isSec) {
        if (!isSec) {
            return;
        }
        isSecondary = true;
        root = new SecLNode(order);
        this.order = order;
    }

    // Given a key, returns the value associated with that key or null if doesnt
    // exist
    public Integer get(Integer key) {
        return root.get(key);
    }
    
    public Integer getBound(Integer key) {
        return root.getBound(key);
    }

    public void getRange(Filter f, HashSet<Integer> result) {
        if (!isSecondary) return;

        if (f.low != null && f.high != null) {
            SecLNode lowerNode = root.getLastRow(f.low);
            if (lowerNode == null) return;

            Integer index = lowerNode.search(f.low);
            while (lowerNode != null) {
                while (index < lowerNode.numChildren && lowerNode.keys[index] <= f.high) {
                    result.addAll(lowerNode.values[index]);
//                    System.out.println(result);
//                    System.out.println("the key is " + lowerNode.keys[index] + " the index is " + index);
                    ++index;
                }
                if (index.equals(lowerNode.numChildren)) {
                    //System.out.println(lowerNode.rightSibling != null ? "Normal" : "the right sibling is null");
                    lowerNode = lowerNode.rightSibling;
                    index = 0;
                } else {
                    break;
                }
            }
        } else if (f.low != null) {
            SecLNode lowerNode = root.getLastRow(f.low);
            if (lowerNode == null) return;

            Integer index = lowerNode.search(f.low);
            while (lowerNode != null) {
                while (index < lowerNode.numChildren) {
                    result.addAll(lowerNode.values[index]);
                    ++index;
                }
                if (index.equals(lowerNode.numChildren)) {
                    lowerNode = lowerNode.rightSibling;
                    index = 0;
                }
            }
        } else if (f.high != null) {
            SecLNode lowerNode = root.getLastRow(minKey);
            Integer index = 0;
            while (lowerNode != null) {
                while (index < lowerNode.numChildren && lowerNode.keys[index] <= f.high) {
                    result.addAll(lowerNode.values[index]);
                    ++index;
                }
                if (index.equals(lowerNode.numChildren)) {
                    lowerNode = lowerNode.rightSibling;
                    index = 0;
                } else {
                    break;
                }
            }
        }
    }

    public PostingList getIdSet(Integer key) {
        if (isSecondary) {
            return root.getIDs(key);
        } else {
            return null;
        }
    }
    // Insert a key-value pair into the tree. This tree does not need to support
    // duplicate keys
    public void insert(Integer key, Integer value) {
        if (minKey > key) {
            minKey = key;
        }
        Split rootSplit = root.insert(key, value);
        if (rootSplit != null) {
            root = new INode(order, rootSplit);
        }
    }

    // Delete a key and its value from the tree
    public void delete(Integer key) {
        root.delete(key);
    }


    // Optional methods to write
    // This might be a helpful function for your debugging needs
//     public void print() { }
}

// DO NOT change this enum. There are two types of nodes; an Internal node, and
// a Leaf node
enum NodeType {
    LEAF,
    INTERNAL,
}

// This class encapsulates the pair of left and right nodes after a split
// occurs, along with the key that divides the two nodes. Both leaf and internal
// nodes split. For this reason, we use Java's generics (e.g. <T extends Node>).
// This is a helper class. Your implementation might not need to use this class
class Split<T extends Node> {
    public Integer key;
    public T left;
    public T right;

    public Split(Integer k, T l, T r) {
        key = k;
        right = r;
        left = l;
    }
}

// An abstract class for the node. Both leaf and internal nodes have the a few
// attributes in common.
abstract class Node {

    // DO NOT edit this attribute. You should use to store the keys in your
    // nodes. Our checks for correctness rely on this attribute. If you change
    // it, your tree will not be correct according to our checker. Values in
    // this array that are not valid should be null.
    public Integer[] keys;

    // Do NOT edit this attribute. You should use it to keep track of the number
    // of CHILDREN or VALUES this node has. Our checks for correctness rely on
    // this attribute. If you change it, your tree will not be correct according
    // to our checker.
    public Integer numChildren;

    // DO NOT edit this method.
    abstract NodeType nodeType();

    // You may edit everything that occurs in this class below this line.
    // *********************************************************************

    // Both leaves and nodes need to keep track of a few things:
    //      their parent
    //      a way to tell another class whether it is a leaf or a node

    // A node is instantiated by giving it an order, and a node type
    public Node(Integer order, NodeType nt) { }

    // A few things both leaves and internal nodes need to do. You are likely
    // going to need to implement these functions. Our correctness checks rely
    // on the structure of the keys array, and values and children arrays in the
    // leaf and child nodes so you may choose to forgo these functions.

    // You might find that printing your nodes' contents might be helpful in
    // debugging. The function signature here assumes spaces are used to
    // indicate the level in the tree.
    // abstract void print(Integer nspaces);

    // You might want to implement a search method to search for the
    // corresponding position of a given key in the node
    abstract Integer search(Integer key);


    abstract  Integer mid();
    // You might want to implement a split method for nodes that need to split.
    // We use the split class defined above to encapsulate the information
    // resulting from the split.
    abstract Split split();          // Note the use of split here

    // You might want to implement an insert method. We use the Split class to
    // indicate whether a node split as a result of an insert because splits in
    // lower levels of the tree may propagate upward.
    abstract Split insert(Integer key, Integer value); // And split here

    // You might want to implement a delete method that traverses down the tree
    // calling a child's delete method until you hit the leaf.
    abstract void delete(Integer key);

    // You might want to implement a get method that behaves similar to the
    // delete method. Here, the get method recursively calls the child's get
    // method and returns the integer up the recursion.
    abstract Integer get(Integer key);

    abstract PostingList getIDs(Integer key);

    abstract SecLNode getLastRow(Integer key);
    
    abstract Integer getBound(Integer key);
    
    // You might want to implement a helper function that cleans up a node. Note
    // that the keys, values, and children of a node should be null if it is
    // invalid. Java's memory manager won't garbage collect if there are
    // references hanging about.
    //abstract void cleanEntries();
    public static Integer binarySearch(Integer[] keys, Integer key, int n) {
        Integer left = 0;
        Integer right = n;
//        System.out.println("left is " + left + " right is " + right);
        while (left < right) {
            Integer mid = left + (right - left)/2;
            if (keys[mid] >= key) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return right;
    }
}

// A leaf node (LNode) is an instance of a Node
class LNode extends Node {

    // DO NOT edit this attribute. You should use to store the values in your
    // leaf node. Our checks for correctness rely on this attribute. If you
    // change it, your tree will not be correct according to our checker. Values
    // in this array that are not valid should be null.
    public Integer[] values;
    public Integer order;
    private LNode rightSibling;

    // DO NOT edit this method;
    public NodeType nodeType() { return NodeType.LEAF; };

    // You may edit everything that occurs in this class below this line.
    // *************************************************************************

    // A leaf has siblings on the left and on the right.

    // A leaf node is instantiated with an order
    public LNode(Integer order) {

        // Because this is also a Node, we instantiate the Node (abstract)
        // superclass, identifying itself as a leaf.
        super(order, NodeType.LEAF);

        // A leaf needs to instantiate the values array.
        this.order = order;
        keys = new Integer[order];
        values = new Integer[order];
        numChildren = 0;
        rightSibling = null;
    }

    @Override
    public Integer get(Integer key) {
        Integer index = search(key);
        if (index < numChildren && key.equals(keys[index])) {
            return values[index];
        } else {
            return null;
        }
    }
    
    @Override
    public Integer getBound(Integer key) {
        Integer index = search(key);
        if (index < numChildren) {
            return values[index];
        } else {
            return null;
        }
    }

    @Override
    public SecLNode getLastRow(Integer key) {
        return null;
    }

    @Override
    public PostingList getIDs(Integer key) {
        return null;
    }

    @Override
    public Integer search(Integer key) {
        Integer index = binarySearch(keys, key, numChildren);
        return index;
    }

    @Override
    public Integer mid() {
        return order/2;
    }

    @Override
    public Split split() {
        LNode right = new LNode(order);
        Integer mid = mid();
        System.arraycopy(keys, mid, right.keys, 0, numChildren - mid);
        System.arraycopy(values, mid, right.values, 0, numChildren - mid);
        right.numChildren = numChildren - mid;
        Integer newKey = keys[mid];
        
        for (int i = mid; i < order; i++) {
            keys[i] = null;
            values[i] = null;
        }
        
        if (this.rightSibling != null) {
            right.rightSibling = this.rightSibling;
        }
        this.rightSibling = right;
        this.numChildren = mid;
        return new Split(newKey, this, right);
    }

    @Override
    public void delete(Integer key) {
        Integer index = search(key);

        if (keys[index] != key || index.equals(numChildren)) {
            return;
        }
        for (Integer i = index; i < numChildren - 1; i++) {
            keys[i] = keys[i+1];
            values[i] = values[i+1];
        }
        keys[numChildren - 1] = null;
        values[numChildren - 1] = null;
        --numChildren;
    }

    @Override
    public Split insert(Integer key, Integer value) {
        Integer index = search(key);
//        System.out.println("insert searched result is " + index + " children number is " + numChildren);
        if (index.equals(numChildren)) {
            keys[index] = key;
            values[index] = value;
            ++numChildren;
        } else if (keys[index].equals(key)) {
            values[index] = value;
        } else {
            System.arraycopy(keys, index, keys, index + 1, numChildren - index);
            System.arraycopy(values, index, values, index + 1, numChildren - index);
            keys[index] = key;
            values[index] = value;
            ++numChildren;
        }

        if (numChildren.equals(order)) {
            return this.split();
        } else {
            return null;
        }
    }
}

// An internal node (INode) is an instance of a Node
class INode extends Node {

    // DO NOT edit this attribute. You should use to store the children of this
    // internalnode. Our checks for correctness rely on this attribute. If you
    // change it, your tree will not be correct according to our checker. Values
    // in this array that are not valid should be null.
    // An INode (as opposed to a leaf) has children. These children could be
    // either leaves or internal nodes. We use the abstract Node class to tell
    // Java that this is the case. Using this abstract class allows us to call
    // abstract functions regardless of whether it is a leaf or an internal
    // node. For example, children[x].get() would work regardless of whether it
    // is a leaf or internal node if the get function is an abstract method in
    // the Node class.
    public Node[] children;
    public Integer order;

    // DO NOT edit this method;
    public NodeType nodeType() { return NodeType.INTERNAL; };

    // You may edit everything that occurs in this class below this line.
    // *************************************************************************

    // A leaf node is instantiated with an order
    public INode(Integer order, Split split) {

        // Because this is also a Node, we instantiate the Node (abstract)
        // superclass, identifying itself as a leaf.
        super(order, NodeType.INTERNAL);
        // An INode needs to instantiate the children array
        this.order = order;
        children = new Node[order + 1];
        keys = new Integer[order];
        if (split != null) {
            children[0] = split.left;
            children[1] = split.right;
            keys[0] = split.key;
            numChildren = 2;
        }
    }

    @Override
    public Integer search(Integer key) {
        Integer index = binarySearch(keys, key, numChildren - 1);
        return index;
    }

    @Override
    public Integer mid() {
        return order/2;
    }

    @Override
    public Split split() {
        INode right = new INode(order, null);
        Integer mid = order % 2 == 0 ? mid() : mid() + 1;
        //Consider the situation of odd or even
        System.arraycopy(keys, mid, right.keys, 0, numChildren - mid - 1);
        System.arraycopy(children, mid, right.children, 0, numChildren - mid);
        right.numChildren = numChildren - mid;
        numChildren = mid;
        Integer newKey = keys[mid - 1];
        
        for (int i = mid - 1; i < order; i++) {
            keys[i] = null;
            children[i + 1] = null;
        }

        return new Split(newKey, this, right);
    }

    @Override
    public Split insert(Integer key, Integer value) {
        Integer index = search(key);
        Split split = null;

        if (index == numChildren - 1 || key < keys[index]) {
            split = children[index].insert(key, value);
        } else if (key.equals(keys[index])) {
            split = children[index + 1].insert(key, value);
        }

        if (split == null) {
            return split;
        } else {
            return insertSplit(split);
        }
    }

    public Split insertSplit(Split splitToInsert) {
        Integer index = search(splitToInsert.key);

        if (index == numChildren - 1) {
            keys[index] = splitToInsert.key;
            children[index + 1] = splitToInsert.right;
        } else {
            System.arraycopy(keys, index, keys, index + 1, numChildren - index - 1);
            System.arraycopy(children, index + 1, children, index + 2, numChildren - index - 1);
            keys[index] = splitToInsert.key;
            children[index + 1] = splitToInsert.right;
        }

        ++numChildren;
        if (numChildren > order) {
            return this.split();
        } else {
            return null;
        }
    }

    @Override
    public void delete(Integer key) {
        Integer index = this.search(key);
        if (index == numChildren - 1 || key < keys[index]) {
            children[index].delete(key);
        } else if (key.equals(keys[index])) {
            children[index+1].delete(key);
        }
    }

    @Override
    public PostingList getIDs(Integer key) {
        return null;
    }

    @Override
    public SecLNode getLastRow(Integer key) {
        Integer index = search(key);
        if (index == numChildren - 1 || key < keys[index]) {
            return children[index].getLastRow(key);
        } else {
            return children[index + 1].getLastRow(key);
        }
    }

    @Override
    public Integer get(Integer key) {
        Integer index = search(key);
        //System.out.println("Inode's children number is " + numChildren + " the index is " + index + " the keysindex is " + keys[index]);
        if (index == numChildren - 1 || key < keys[index]) {
            return children[index].get(key);
        } else {
            return children[index + 1].get(key);
        }
    }
    
    @Override
    public Integer getBound(Integer key) {
        Integer index = search(key);
        if (index == numChildren - 1 || key < keys[index]) {
            return children[index].getBound(key);
        } else {
            return children[index + 1].getBound(key);
        }
    }
}

class PostingList extends HashSet<Integer>{
}

class SecLNode extends Node {
    public PostingList[] values;
    public Integer order;
    public SecLNode rightSibling;

    // DO NOT edit this method;
    public NodeType nodeType() { return NodeType.LEAF; };

    // You may edit everything that occurs in this class below this line.
    // *************************************************************************

    // A leaf has siblings on the left and on the right.

    // A leaf node is instantiated with an order
    public SecLNode(Integer order) {

        // Because this is also a Node, we instantiate the Node (abstract)
        // superclass, identifying itself as a leaf.
        super(order, NodeType.LEAF);

        // A leaf needs to instantiate the values array.
        this.order = order;
        keys = new Integer[order];
        values = new PostingList[order];
        numChildren = 0;
        rightSibling = null;
    }

    @Override
    public Integer get(Integer key) {
//        return null;
        Integer index = search(key);
        if (index < numChildren && key.equals(keys[index])) {
            return index;
        } else {
            return null;
        }
    }
    
    @Override
    public Integer getBound(Integer key) {
        Integer index = search(key);
        if (index < numChildren) {
            return index;
        } else {
            return null;
        }
    }

    @Override
    public SecLNode getLastRow(Integer key) {
        Integer index = search(key);
        if (index < numChildren) {
            return this;
        } else {
            return null;
        }
    }

    public PostingList getIDs(Integer key) {
        Integer index = search(key);
        if (index < numChildren && key.equals(keys[index])) {
            return values[index];
        } else {
            return null;
        }
    }

    @Override
    public Integer search(Integer key) {
        Integer index = binarySearch(keys, key, numChildren);
        return index;
    }

    @Override
    public Integer mid() {
        return order/2;
    }

    @Override
    public Split split() {
        SecLNode right = new SecLNode(order);
        Integer mid = mid();
        System.arraycopy(keys, mid, right.keys, 0, numChildren - mid);
        System.arraycopy(values, mid, right.values, 0, numChildren - mid);
        right.numChildren = numChildren - mid;
        Integer newKey = keys[mid];
        for (int i = mid; i < order; i++) {
            keys[i] = null;
            values[i] = null;
        }
        if (this.rightSibling != null) {
            right.rightSibling = this.rightSibling;
        }
        this.rightSibling = right;
        this.numChildren = mid;
        
        return new Split(newKey, this, right);
    }

    @Override
    public void delete(Integer key) {
        Integer index = search(key);

        if (!keys[index].equals(key) || index.equals(numChildren)) {
            return;
        }
        for (Integer i = index; i < numChildren - 1; i++) {
            keys[i] = keys[i+1];
            values[i] = values[i+1];
        }
        --numChildren;
    }

    @Override
    public Split insert(Integer key, Integer value) {
        Integer index = search(key);
//        System.out.println("insert searched result is " + index + " children number is " + numChildren);
        if (index.equals(numChildren)) {
            keys[index] = key;
            values[index] = new PostingList();
            values[index].add(value);
            ++numChildren;
        } else if (keys[index].equals(key)) {
            values[index].add(value);
        } else {
            System.arraycopy(keys, index, keys, index + 1, numChildren - index);
            System.arraycopy(values, index, values, index + 1, numChildren - index);
            keys[index] = key;
            values[index] = new PostingList();
            values[index].add(value);
            ++numChildren;
        }

        if (numChildren.equals(order)) {
            return this.split();
        } else {
            return null;
        }
    }
}


// This is potentially encapsulates the resulting information after a node
// splits. This is might help when passing split information from the split
// child to the parent. Sea README for more details.
/*
class Split<T extends Node> {
    public Integer key;
    public T left;
    public T right; // always splits rightward

    public Split(Integer k, T l, T r) {
        key = k;
        right = r;
        left = l;
    }
}
*/

