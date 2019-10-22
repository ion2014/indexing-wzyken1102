import java.util.*;
import java.io.PrintWriter;

public class TupleCollection {
    HashMap<Tuple, Integer> map;
    HashSet<String> cols;

    public TupleCollection() {
        map = new HashMap<Tuple, Integer>();
        cols = new HashSet<String>();
    }

    public void addTuple(Tuple t) {
        for (String name : t.keySet()) {
            cols.add(name);
        }

        Integer v = map.putIfAbsent(t, 1);
        if (!(v==null)) {
            map.replace(t, v+1);
        }
    }

    public void toCSV(String file) throws Exception {
        Vector<String> temp = new Vector<String>();

        for (String c : cols) {
            temp.add(c);
        }

        String[] csv_cols = Arrays.copyOf(temp.toArray(),
                                          temp.size(),
                                          String[].class);

        String lines = "";
        Arrays.sort(csv_cols);
        for (Tuple t: map.keySet()) {
            Integer n = map.get(t);
            for (Integer i = 0; i < n; i++) {
                String line = "";
                for (String col : csv_cols) {
                    line = line + t.get(col) + ",";
                }
                // remove last comma and add new line
                line = line.substring(0, line.length() - 1);
                lines = lines + line + "\n";
            }
        }

        // remove last new line
        lines = lines.substring(0, lines.length() - 1);

        // write to csv
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.println(lines);
        writer.close();
    }
}
