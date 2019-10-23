JFLAGS =
JC = javac
.SUFFIXES: .java .class

.java.class:
	@echo Building $*.java
	@$(JC) $(JFLAGS) $*.java

CLASSES = $(wildcard *.java)
default: classes

classes: $(CLASSES:.java=.class)

testtable: default
	@java -ea Main -testtable
	@data_validation/compare.sh

testtree: default
	@java -ea Main -testtree

bench: default
	@java -ea Main -bench

register:
	@sqlite3 /home/franco/benchmarks.db < benchmarks/results/benchmark_results.sql
	@echo Registered into sqlitedb

query:
	@sqlite3 /home/franco/benchmarks.db < benchmarks/query.sql

data:
	cd data_validation ; ./generate_data.py ; ./range_filter.py ; ./delete.py; ./update.py

clean:
	@rm -rf *.class
	@rm -rf benchmarks/results/benchmark_results.sql
	@rm -rf data_validation/results/*.csv
	@echo "Done cleaning"

