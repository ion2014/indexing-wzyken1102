package aggregator;

public interface Aggregator {
    String target_attribute();
    void operation(Integer x);
    Integer result();
}

