import java.util.*;

// The BPlusTree class. You'll need to fill the methods in. DO NOT change the
// function signatures of these methods. Our checker relies on these methods and
// assume these function signatures.

public class BPlusTree {

    // A tree has a root node, and an order
    public Node root;
    private Integer order;

    // Required methods to implement. DO NOT change the function signatures of
    // these methods.

    // Instantiate a BPlusTree with a specific order
    public BPlusTree(Integer order) {
        root = new Node(order, NodeType.INTERNAL);
        this.order = order;
    }

    // Given a key, returns the value associated with that key or null if doesnt
    // exist
    public Integer get(Integer key) { return null; }

    // Insert a key-value pair into the tree. This tree does not need to support
    // duplicate keys
    public void insert(Integer key, Integer value) { }

    // Delete a key and its value from the tree
    public void delete(Integer key) { }

    // Optional methods to write
    // This might be a helpful function for your debugging needs
     public void print() { }
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
     abstract Integer search(Integer key) {};

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

    // You might want to implement a helper function that cleans up a node. Note
    // that the keys, values, and children of a node should be null if it is
    // invalid. Java's memory manager won't garbage collect if there are
    // references hanging about.
     abstract void cleanEntries();
}

// A leaf node (LNode) is an instance of a Node
class LNode extends Node {

    // DO NOT edit this attribute. You should use to store the values in your
    // leaf node. Our checks for correctness rely on this attribute. If you
    // change it, your tree will not be correct according to our checker. Values
    // in this array that are not valid should be null.
    public Integer[] values;

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
        values = new Integer[order];
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

    // DO NOT edit this method;
    public NodeType nodeType() { return NodeType.INTERNAL; };

    // You may edit everything that occurs in this class below this line.
    // *************************************************************************

    // A leaf node is instantiated with an order
    public INode(Integer order) {

        // Because this is also a Node, we instantiate the Node (abstract)
        // superclass, identifying itself as a leaf.
        super(order, NodeType.INTERNAL);

        // An INode needs to instantiate the children array
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

