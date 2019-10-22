import java.util.*;
import java.lang.*;

public class BPlusTreeTests {

    BPlusTree tree;
    Integer degree;
    Random generator;
    Integer seeder;
    Integer count;
    HashSet<Integer> values;

    public BPlusTreeTests(Integer d, Integer seed) {
        tree = new BPlusTree(d);
        degree = d;
        generator = new Random();
        if (seed != null) {
            seeder = seed;
        } else {
            seeder = (new Random()).nextInt();
        }
        System.out.println("Seed for random generator: " + seeder);
        generator.setSeed(seeder);
        count = 0;
        values = new HashSet<Integer>();
    }

    Integer insert() {
        Integer x = generator.nextInt();
        count++;
        values.add(x);
        tree.insert(x, x);
        if (x != tree.get(x)) {
            throw new RuntimeException("Bad insert! Can't find value "
                                       + x
                                       + " after inserting ");
        }
        return x;
    }

    void delete() {

        Integer v = values.iterator().next();

        if (v != tree.get(v)) {
            throw new RuntimeException("Bad delete! Can't find value "
                                       + v
                                       + " before removing ");
        }

        tree.delete(v);
        if (null != tree.get(v)) {
            throw new RuntimeException("Bad delete! value "
                                       + v
                                       + " after removing is not null");
        }

        values.remove(v);
    }

    void get() {
        Integer v = values.iterator().next();
        if (v != tree.get(v)) {
            throw new RuntimeException("Bad get! Can't find value "
                                       + v
                                       + " but is in insert set");
        }
    }

    public void testGet() {
        for (Integer i=0; i<values.size(); i++) {
            this.get();
        }
        System.out.println("Successfully got all "
                           + values.size()
                           + " expected values");
    }

    public void testDelete(Integer n) {
        Integer counter = 0;
        for (Integer i = 0; i < n; i++) {
            this.delete();
            counter++;
            if (values.isEmpty()) {
                break;
            }
        }
        System.out.println("Successfully deleted: "
                           + counter
                           + " items with "
                           + values.size()
                           + " items remaining");
    }

    public void testMix(Integer n, Integer writeRatio) {
        if ((writeRatio < 0) || (writeRatio > 100)) {
            throw new RuntimeException("Write ratio should be between 0 and 100");
        }

        Integer iCount = 0;
        Integer dCount = 0;

        Random rw = new Random();
        for (Integer i = 0; i < n; i++) {
            if ((rw.nextInt(100) < writeRatio) || (values.isEmpty())) {
                this.insert();
                iCount++;
            } else {
                this.delete();
                dCount++;
            }
        }

        System.out.println("Successfully written "
                           + iCount
                           + " and deleted "
                           + dCount
                           + " and "
                           + n
                           + " operations with writeRatio "
                           + writeRatio);
    }

    public void testInsert(Integer n) {
        for (int i = 0; i < n; ++i) {
            Integer inserted = insert();
        }
        System.out.println("Successfully inserted: " + n + " items");
    }

    public ValidationError testValidate() {
        if (tree.root.nodeType() == NodeType.INTERNAL) {
            INodeValidator v = new INodeValidator((INode)tree.root);
            ValidationError result = v.validate();
            if (result != null) { return result; }
        }
        System.out.println("Successfully validated tree");
        return null;
    }
}
