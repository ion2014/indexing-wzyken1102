import java.util.*;

public class TableTests {

    Table table;
    HashMap<Integer, Tuple> tuples;
    HashSet<String> attributes;
    CSVReader reader;
    Random generator;

    public TableTests() {
        HashSet<String> a = new HashSet<String>();
        a.add("A");
        a.add("B");
        a.add("C");

        table = new Table("test_table", a);
        reader = new CSVReader("data_validation/data.csv");

        this.attributes = a;
        this.generator = new Random();
    }

    public void testLoad() throws Exception {
        Vector<Tuple> tup = reader.read();
        table.load(tup);
    }

    TupleCollection filterHelper(Filter filter) {
        TupleIDSet intermediate = table.filter(filter);
        HashSet<String> attributes = new HashSet<String>();
        attributes.add("A");
        attributes.add("B");
        attributes.add("C");
        MaterializedResults results = table.materialize(attributes, intermediate);
        TupleCollection tuples = new TupleCollection();
        for (Tuple t : results) {
            tuples.addTuple(t);
        };
        return tuples;
    }
    public void testBetween() throws Exception {
        Filter filter = new Filter("A", 500, 600);
        TupleCollection tuples = filterHelper(filter);
        tuples.toCSV("data_validation/results/between.csv");
    }

    public void testLT() throws Exception {
        Filter filter = new Filter("A", null, 600);
        TupleCollection tuples = filterHelper(filter);
        tuples.toCSV("data_validation/results/lt.csv");
    }

    public void testGT() throws Exception {
        Filter filter = new Filter("A", 500, null);
        TupleCollection tuples = filterHelper(filter);
        tuples.toCSV("data_validation/results/gt.csv");
    }

    public void testComposite() throws Exception {
        Filter filterA = new Filter("A", 500, 600);
        Filter filterB = new Filter("B", 100, 200);
        Filter composite = new Filter(filterA, filterB, FilterOp.AND);
        TupleCollection tuples = filterHelper(composite);
        tuples.toCSV("data_validation/results/composite.csv");
    }

    public void testDelete() throws Exception {
        Filter filter = new Filter("A", 500, 600);
        TupleIDSet intermediate = table.filter(filter);

        table.delete(intermediate);

        HashSet<String> attributes = new HashSet<String>();
        attributes.add("A");
        attributes.add("B");
        attributes.add("C");
        MaterializedResults results = table.materialize(attributes, null);
        TupleCollection tuples = new TupleCollection();
        for (Tuple t : results) {
            tuples.addTuple(t);
        };
        tuples.toCSV("data_validation/results/delete.csv");
    }

}
