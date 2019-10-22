import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class CSVReader {
    String path;
    public CSVReader(String p) {
        path = p;
    }

    public Vector<Tuple> read() throws Exception {
        Vector<Tuple> vec = new Vector<Tuple>();

        BufferedReader br = new BufferedReader(new FileReader(path)); 
        String line = "";
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            Tuple t = new Tuple();
            char col = 'A';
            for (String s : data) {
                Integer val = Integer.parseInt(s);
                String colname = "" + col;
                t.put(colname, val);
                col += 1;
            }
            vec.add(t);
        }
        return vec;
    }
}
