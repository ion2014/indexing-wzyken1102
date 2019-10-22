import java.util.*;
import java.lang.*;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ThreadLocalRandom;

public class Benchmarks {
    Random generator;
    Integer iterations;
    Integer dataSize;
    Long startTime;
    String banner;
    LocalDateTime currentTime;
    static PrintWriter writer;
    static {
        try {
            writer = new PrintWriter("benchmark_results.txt", "UTF-8");
        } catch(Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public Benchmarks() {
        generator = new Random();
        generator.setSeed(12345);
        iterations = 100;
        dataSize = 1000;
        banner = new Main().bannerid;
        currentTime = LocalDateTime.now(ZoneId.of("America/New_York"));
    }

    void startTimer() {
        startTime = System.nanoTime();
    }

    Long endTimer() {
        Long endTime = System.nanoTime();
        return (endTime - startTime); // convert to millis because 64bit?
    }

    void printDuration(String label, Float nanos) {
        writer.println(nanos + " " + banner + " " + currentTime + " Benchmark: " + label);
    }

    Table setupTable() {
        HashSet<String> a = new HashSet<String>();
        a.add("A");
        a.add("B");
        a.add("C");
        return new Table("test_table", a);
    }

    Vector<Tuple> setupTuples() {
        Vector<Tuple> v = new Vector<Tuple>();

        for (Integer i = 0; i < dataSize; ++i) {
            Tuple t = new Tuple();
            t.put("A", generator.nextInt());
            t.put("B", generator.nextInt());
            t.put("C", generator.nextInt());
            v.add(t);
        }

        return v;
    }

    public void insertBenchmark() {

        Long sum = Long.valueOf(0);
        for (Integer i = 0; i < iterations; ++i) {
            Table x = setupTable();
            Vector<Tuple> v = setupTuples();
            startTimer();
            for (Tuple t : v) {
                x.insert(t);
            }
            Long end = endTimer();
            sum += end;
        }
        Float s = Float.valueOf(sum);
        Float iter = Float.valueOf(iterations);
        printDuration("insert benchmark", s/iter);
    }

    public void bulkLoadBenchmark() {
        Long sum = Long.valueOf(0);
        for (Integer i = 0; i < iterations; ++i) {
            Table x = setupTable();
            Vector<Tuple> v = setupTuples();
            startTimer();
            x.load(v);
            sum += endTimer();
        }
        Float s = Float.valueOf(sum);
        Float iter = Float.valueOf(iterations);
        printDuration("bulkload benchmark", s/iter);
    }

    public void deleteBenchmark() {

        TupleIDSet tuples = randomIds();

        Long sum = Long.valueOf(0);
        for (Integer i = 0; i < iterations; ++i) {
            Table x = setupTable();
            Vector<Tuple> v = setupTuples();
            x.load(v);
            startTimer();
            x.delete(tuples);
            sum += endTimer();
        }
        Float s = Float.valueOf(sum);
        Float iter = Float.valueOf(iterations);
        printDuration("delete benchmark", s/iter);
    }

    public void updateBenchmark() {
    }

    TupleIDSet randomIds() {
        TupleIDSet tuples = new TupleIDSet();
        for (Integer i = 0; i < dataSize; i++) {
            tuples.add(i);
        }
        return tuples;
    }

    void finish() {
        writer.close();
    }
}
