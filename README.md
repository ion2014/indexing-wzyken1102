# CS127 Indexing Programming Assignment

## Overview of Expectations for this Programming Assignment

### Learning Goals

In this programming assignment, students learn how to:

* implement a B+ tree index in a main-memory column-store
* understand the implications of using an index on the rest of the system
* understand the trade-offs of primary and secondary indices
* explore other indexing schemes for different workloads

### Tasks

Students will accomplish the following tasks, in order of priority, along with
a recommended timeline:

1. Implement a B+ tree (4 days)
2. Implement a primary and secondary indices using this B+ tree for a `Table` (1
   day)
3. Explore and implement one alternative avenue of indexing a database (3 days)
4. Write a one-page summary of optimizations performed on the B+ tree (if any)
   and results of item 4 above (<1 day)

The remainder of the time should be used to get your extra credit!

### Grading

Students will be graded in the following way:

1. B: A baseline grade of for getting correct results
2. B+: Minimal working implementation of a B+ tree (pun intended)
3. A-: Implementing primary index for a `Table` and at least one secondary index
4. A: Exploration and implementation of one alternative avenue of indexing
5. Extra Credit for highly performant implementations based on the benchmarks
   described in the next subsection:
    1. X extra points for the 50th percentile of the class
    2. Y extra points for the 25th percentile of the class
    3. Z extra points for the 10th percentile of the class

## Code

The goal of the code base is to keep things small and streamlined. This allows
you the student to go bazinga(s) over modifications and optimizations without
fear that you shouldn't modify some file.

The files you can (and likely should modify) are the following:
* `Main.java`
* `Column.java`
* `BPlusTree.java`
* `Table.java`

You can make any changes to the files above so long as you follow the
instructions on what you are able to change and not. You can also make new Java
files that contain your own code.

### Installing and Running

Unfortunately, the code is written in Java. As such, you need the Java JDK. We
use version 11. I'm no Java programmer so I use a Makefile.


To compile the code:

```bash
make
```

To generate the test data:

```bash
make data
```

To run the tests the code:

```bash
make test
```

To run the benchmarks:

```bash
make bench
```

To clean up class files:

```bash
make clean
```

Now wasn't that easy (the make part, not the JDK part)?

### Tests for Correctness

## B+ Tree

You can see the tests for correctness in the `BPlusTreeTests.java` file. This
file won't work when you initially `make test`

### Benchmarking

Arguably equivalent in importance to correctness is performance. Indices are
built to ensure performant databases. As such, we provide a benchmarking
framework to estimate how fast your implementations run. You can run these
benchmarks by running `make bench`


### An implementation overview

The code implements a simple column-oriented `Table` supporting only the Integer
data type. This `Table` supports a few essential database operations: inserts,
deletes, filters, and aggregations (currently a sum()). In addition, the table
supports a new operator you might not have seen called "materialize". These are
discussed below.

It is **import to note** that the code distributed to students can be modificed
in almost any way and is written to show how the initial implementation works.
However, there are limits (as with everything):

**DO NOT change the function signatures of the `Table` class.**

### Tuples, Columns, and Tables

#### Tuples
`Tuple.java` contains the code for a `Tuple`. You'll see that the `Tuple` class
is simply a hashtable where the key is a String, and the value is an Integer.
The key is the attribute, and the value is well, the value of that attribute.
For example, the code below inserts an instance of a `Tuple` into the table
`someTable`. This `Tuple` has two attributes: "A" = 1, and "B" = 2.

```Java
Tuple t = new Tuple();
t.put("A", 1);
t.put("B", 2);
some`Table`.insert(t1);
```

#### Columns
`Columns.java` contains the code for the *physical representation* for the
*logical attribute* in a `Table`. Because the `Table` is a column-oriented data
storage scheme, the set of values of a given attribute is stored as an array in
contiguous memory. You'll see that this column is simply an ArrayList of
Integers.

#### Tables
`Table.java` contains a bulk of the initial code. It has several internal
variables:

* `String name` is the name of the `Table`. It's not used much but there anyways
for future use.
* `Hashtable<String, Column> attributes` is the collection of columns and the
attribute name corresponding to those columns.
* `ArrayList<Boolean> valid` is an array that indicates whether a tuple is valid
or not (for example, when the tuple is deleted)

Internally, the column-oriented storage allows the `Table` to use the index of a
tuple in its column as the tuple's id. In the code below, `Tuple` t1 will be
referenced internally with id 0, t2 with id 1, and t3 with id 2. `Tuple` t2's
value for attribute "A" can be accessed in the Column corresponding to attribute
A equivalent to `columnA[0]`.

```Java

// Instantiate a new table with two attribtues "A" and "B".
HashSet<String> cols = new HashSet<String>();
cols.add("A");
cols.add("B");
Table someTable = new Table("test_table", cols);

// Insert tuples into the Table
Tuple t1 = new Tuple();
t1.put("A", 1);
t1.put("B", 2);
toTest.insert(t1);

Tuple t2 = new Tuple();
t2.put("A", 1);
t2.put("B", 3);
toTest.insert(t2);

Tuple t3 = new Tuple();
t3.put("A", 3);
t3.put("B", 1);
toTest.insert(t3);

```

The `Table` class provides several methods we describe below.

**insert**: Inserts a `Tuple` into the table by appending the values of the
tuple to each of the columns. Internally marks that tuple as valid by also
pushing `true` into the `valid` variable.

**delete**: Given a `Tuple`'s Integer ID, marks the `Tuple` as invalid by
setting the `valid` variable for that id as `false`. *Note:* this extremely
naive approach is done on purpose. At some point, there are performance
implications to not garbage collecting or physically deleting these entries.

**filter**: Given a `Filter`, returns the `HashSet` of valid tuple IDs that
qualify. See the description of a `Filter` later in the document.

**materialize**: Given a set of attributes and a set of valid tuple ids
(`Integer`s), returns a `MaterializedView` of the attributes and tuples
requested. This is particularly necessary for column stores because a `Tuple`'s
values are stored separately in columns. This operation is also quite expensive
and is typically only used as the very last step in the query plan. This
technique is called *late tuple materialization*. We describe a
`MaterializedView` later in the document.

**load**: Given a list of `Tuple`s, load all of these tuples into the Table.

**Tip**: the `load` method currently calls `insert` each time. This *might* be
an issue when you are building an index...

#### Other stuff relating to Tuples, Columns, and Tables

**Filter**: Provides an interface for Filter classes. It requires two methods
for the classes that implement it: `target_attribute` and `check`. Look at the
`RangeFilter` class that implements `Filter` for a range filter that checks if a
value `x` is `low <= x < high`.

**MaterializedResults**: This is a very lightweight wrapper around ArrayList<Tuple>
mostly because we wanted to override the toString method (for printing).

### Indices

We provide an interface for indices in `Index.java` for the required methods for
indices. We also provide skeleton code for your B+ Tree implementation, along
with psuedocode later in this document or in the references in the book. The
`BPlustTree.java` contains skeleton code for the B+ Tree, the Leaf and the
Internal nodes.

#### Phase #1: Code base and B+ Tree
There are two tasks to the first phase:
* First, get familiar with the codebase - specifically, the tbale

Want to know how a B+ tree works in a fun and interactive visualization? Check
this out:

https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html





TODO: More instruction here when the skeleton code is more fully fleshed out.


