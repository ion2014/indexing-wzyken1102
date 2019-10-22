package aggregator;

public class SumAggregator implements aggregator.Aggregator {
    Integer sum = 0;
    String name;

    public SumAggregator(String n) {
	    name = n;
    }

    public String target_attribute() {
	    return name;
    }

    public Integer result() {
	    return sum;
    }

    public void operation(Integer x) {
	sum += x;
    }

}

