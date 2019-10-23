all:
	@javac *.java

testtable:
	@java -ea Main -testtable
	@data_validation/compare.sh

testtree:
	@java -ea Main -testtree

bench:
	@java -ea Main -bench
	@grep "insert" benchmark_results.txt >> /home/franco/benchmarks/insert
	@grep "delete" benchmark_results.txt >> /home/franco/benchmarks/delete

data:
	cd data_validation ; ./generate_data.py ; ./range_filter.py ; ./delete.py; ./update.py

clean:
	rm *.class
