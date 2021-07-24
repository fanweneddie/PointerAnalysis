# Pointer Analysis

## Focus:
What memory locations can a pointer expression refer to?

## Problems:

### Alias:
Two pointer expressions refer to the same storage location.

### Flow sensitivity:

* `Flow-sensitive`: computes for each program point what memory locations pointer expressions may refer to

* `Flow-insensitive`: computes what memory locations pointer expressions may refer to, at any time in program execution.

### Modeling
* For global variables, use a single "node".
* For local variables, use a single "node" per context.
* For dynamically allocated memory, need to model locations with some finite abstraction

### pointer instructions
* p = &x (taking the address of a variable)
* p = q  (copying a pointer from one variable to another)
* *p = q (assigning through a pointer)
* p = *q (dereferencing a pointer)


## Anderson algorithm

### features
`context-insensitive`, `flow-insensitive` interprocedural analysis.(No need to consider the order of program statements, which sacrifices the precision to improve performance)

### rules:

* p = &x $\rightarrow$ $l_x \in p$, which means that the address of x($l_x$), is in the set of location pointed to by $p$.

* p = q $\rightarrow$ $p \supseteq q$, which means that the set of location pointed to by $p$ must be a superset of those pointed by $q$.

* *p = q $\rightarrow$ $*p \supseteq q$.

* p = *q $\rightarrow$ $p \subseteq *q$.

And we have the constraint propagation rules:

* copy rule:
$$ \frac{p \supseteq q, l_x \in q}{l_x \in p}$$

* assign rule:
$$ \frac{*p \supseteq q, l_r \in p, l_x \in q}{l_x \in r} $$

* dereference rule:
$$ \frac{p \supseteq *q, l_r \in q, l_x \in r}{l_x \in p} $$

* malloc rule:
$$ \frac{}{p = malloc_n() \rightarrow l_n \in p} $$

Note:
* If Andersen’s algorithm says that the set $p$ points to only one location $l_z$, we have `must-point-to` information.
* If the set $p$ contains more than one location, we have only `may-point-to` information.

For malloc:
* we must assume that if some variable $p$ only points to one abstract malloc $d$ location $l_n$, that is still `may-alias` information and not `must-alias information`.

### efficiency

All constraints can be generated in a linear $O(n)$ pass over the program, and each of the $O(n)$ variables defined in the program could potentially point to $O(n)$ other variables.

Therefore, the solution size is $O(n^2)$.

And the run time of `Anderson` algorithm is $O(n^3)$.