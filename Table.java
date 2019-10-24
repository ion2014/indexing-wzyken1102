import java.util.*;

// DO NOT CHANGE THE METHOD SIGNATURE FOR THE METHODS WE GIVE YOU BUT YOU MAY
// CHANGE THE METHOD'S IMPLEMENTATION

public class Table {

    String name = "This is table";
    Hashtable<String, Column> attributes;
    Vector<Boolean> valid;

    public Table(String table_name, HashSet<String> attribute_names) {
        name = table_name;
        attributes = new Hashtable<String, Column>();
        valid = new Vector<Boolean>();

        for (String attribute_name : attribute_names) {
            attributes.put(attribute_name, new Column());
        }
    }

    public void setClusteredIndex(String attribute) { }
    public void setSecondaryIndex(String attribute) { }

    // Insert a tuple into the Table
    public void insert(Tuple tuple) {

        if (!attributes.keySet().equals(tuple.keySet())) {
            throw new RuntimeException("Tuples and attributes don't match");
        }

        for (String key : attributes.keySet()) {
            attributes.get(key).add(tuple.get(key));
        }
        valid.add(true);
    }

    // Loads each tuple into the Table
    public void load(Vector<Tuple> data) {
        for (Tuple datum : data) {
            this.insert(datum);
        }
    }

    // Uses a filter to find the qualifying tuples. Returns a set of tupleIDs
    public TupleIDSet filter(Filter f) {

        if (f.binary == false) {
            return filterHelperUnary(f);
        } else {
            return filterHelperBinary(f);
        }
    }

    TupleIDSet filterHelperBinary(Filter f) {
        TupleIDSet left, right;
        if (f.left.binary == true) {
            left = filterHelperBinary(f.left);
        } else {
            left = filterHelperUnary(f.left);
        }

        if (f.right.binary == true) {
            right = filterHelperBinary(f.right);
        } else {
            right = filterHelperUnary(f.right);
        }

        if (f.op == FilterOp.AND) {
            left.retainAll(right);
        } else if (f.op == FilterOp.OR) {
            left.addAll(right);
        }
        return left;
    }

    TupleIDSet filterHelperUnary(Filter f) {
        String attribute = f.attribute;
        Column col = attributes.get(attribute);
        if (col == null) {
            throw new RuntimeException("Column not in Table");
        }

        TupleIDSet result = new TupleIDSet();
        int counter = 0;

        if ((f.low != null) && (f.high != null)) {
            for (int v : col) {
                if ((v >= f.low) && (v <= f.high) && valid.get(counter)) {
                    result.add(counter);
                }
                counter++;
            }
        } else if (f.low != null) {
            for (int v : col) {
                if ((v >= f.low) && valid.get(counter)) {
                    result.add(counter);
                }
                counter++;
            }
        } else if (f.high != null) {
            for (int v : col) {
                if ((v <= f.high) && valid.get(counter)) {
                    result.add(counter);
                }
                counter++;
            }
        }
        return result;
    }

    // Deletes a set of tuple ids. If ids is null, deletes all tuples
    public void delete(TupleIDSet ids) {

        if (ids == null) {
            for (Integer i=0; i < valid.size(); i++) {
                valid.set(i, false);
            }
        } else {
            for (Integer id : ids) {
                valid.set(id, false);
            }
        }

    }

    // Update an attribute for a set of tupleIds, to a given value
    // if tupleIds is null, updates all tuples
    public void update(String attribute,
                       TupleIDSet ids,
                       Integer value) {
        Column col = attributes.get(attribute);
        if (ids == null) {
            for (Integer i=0; i < col.size(); i++) {
                col.set(i, value);
            }
        } else {
            for (Integer id : ids) {
                col.set(id, value);
            }
        }
    }

    // Materializes the set of valid tuple ids given. If no tuple ids given,
    // materializes  all valid tuples.
    public MaterializedResults materialize(Set<String> attributes,
                                           TupleIDSet tupleIds) {

        MaterializedResults result = new MaterializedResults();

        if (tupleIds != null) {
            for (int tupleId : tupleIds) {

                if (!valid.get(tupleId)) {
                    throw new RuntimeException("tupleID is not valid");
                }

                Tuple t = new Tuple();
                for (String attribute : attributes) {
                    t.put(attribute,
                          this.attributes.get(attribute).get(tupleId));
                }

                result.add(t);

            }
        } else {
            for (Integer i = 0; i < valid.size(); i++) {
                if (valid.get(i)) {
                    Tuple t = new Tuple();
                    for (String attribute : attributes) {
                        t.put(attribute,
                              this.attributes.get(attribute).get(i));
                    }
                    result.add(t);
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        String v = name + " Columns: " + attributes.keySet();
        return v;
    }
}
