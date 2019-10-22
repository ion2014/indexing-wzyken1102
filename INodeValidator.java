import java.util.*;

class ValidationError {
    String msg;
    INode node;
    public ValidationError(String m, INode n) {
        msg = m;
        node = n;
    }

    public void printError() {
        System.out.println(msg);
    }
}

public class INodeValidator {
    INode node;
    HashSet<Integer> seenKeys;
    Integer error;

    public INodeValidator(INode n) {
        node = n;
        seenKeys = new HashSet<Integer>();
        error = 0;
    }

    public INodeValidator(INode n, HashSet<Integer> k) {
        node = n;
        seenKeys = new HashSet<Integer>();
        error = 0;
    }

    public ValidationError validateKeys() {
        Boolean sawNull = false;
        Integer last = null;
        for (Integer i : this.node.keys) {
            if (i == null) { sawNull = true; }

            else if (sawNull == true) {
                return new ValidationError(
                        "Non-null keys after seeing a null",
                        node);
            }

            else if (seenKeys.contains(i)) {
                return new ValidationError(
                        "Duplicated keys in internal nodes",
                        node);
            }

            else if ((last != null) && (last >= i)) {
                return new ValidationError(
                        "Keys are not in sorted order",
                        node);
            }
            last = i;
            seenKeys.add(i);
        }
        return null;
    }

    public ValidationError validateChildKeys(Integer l, Integer h, Node c) {
        for (Integer k : c.keys) {
            if (k != null) {

                if ((l == null) && (h == null)) {
                    return new ValidationError(
                            "Something wrong happened",
                            node);
                }

                // First child will have null low bounds (defined from parent of
                // this node. Recursive check should catch this
                else if ((l == null) && (k >= h)) {
                    return new ValidationError(
                            "Purported first child keys don't match to high",
                            node);
                }

                // Last child will have null higher bound
                else if ((h == null) && (k < l)) {
                    return new ValidationError(
                            "Purported last child keys don't match to l",
                            node);
                }

                // Chldren are somewhere in between
                else if ((l != null) && (h != null) && ((k >= h) || (k < l))) {
                    return new ValidationError(
                            "Child keys don't match to low high",
                            node);
                }

            }
        }
        return null;
    }

    public ValidationError validateChildren() {
        Integer counter = 0;
        Boolean seenNull = false;
        for (Node child : node.children) {

            if (child == null) { seenNull = true; }

            else if (seenNull == true) {
                return new ValidationError(
                        "Non null child after seeing a null child",
                        node);
            }

            else if (counter.equals(0)) {
                Integer h = node.keys[counter];
                ValidationError result = validateChildKeys(null, h, child);
                if (result != null) {
                    return result;
                }
            }

            else {
                Integer l = node.keys[counter-1];
                Integer h = node.keys[counter];

                if ((l == null) && (h == null)) {
                    return new ValidationError(
                            "A non-null child corresponding to a null keys",
                            node);
                }

                ValidationError result = validateChildKeys(l, h, child);
                if (result != null) {
                    return result;
                }
            }

            counter++;
        }
        return null;
    }

    public NodeType childType() {
        if (node.children[0] == null) { return null; }
        return node.children[0].nodeType();
    }

    public ValidationError validate() {

        ValidationError result = null;

        result = validateKeys();
        if (result != null) { return result;}

        result = validateChildren();
        if (result != null) { return result;}

        if (childType() == NodeType.INTERNAL) {
            for (Node child: node.children) {
                if (child != null) {
                    INodeValidator v = new INodeValidator((INode)child, seenKeys);
                    result = v.validate();
                    if (result != null) { return result; }
                }
            }
        }

        return null;
    }

}
