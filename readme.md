This is the lab1 of the course `Software analysis` in PKU

# Pointer Analysis

## Algorithm
* Direction: `Forward Analysis`
* Semi-lattice element: `<variable, potential source>`
* Meet operator: `Union`
* Functions:
    * `New`: record the current `AllocId`
    * `Test`: record the `TestId` and the potential source of the tested variable.
    * `Assignment` of `a = b`: `gen` is the potential source of `b`, `kill` is the potential source of `a`.

## Structure

Here is the structure of files and directories:
```
MyPointerAnalysis: base directory
----class                               class file
    ----core                            class files of core package
    ----tests                           class files of tests
----doc                                 documents
    ----note.md                         note of pointer analysis
    ----pointer.pdf                     the document of pointer.pdf
----sootInput                           input files for soot
    ----benchmark                       java files for benchmark
    ----**.java                         java files for testing
----sootOutput                          output files for soot
    ----benchmark                       class files for benchmark
    ----test                            class files for testing
----src/main/java                       java source file
    ----Anderson.java                   data structure using Anderson analysis
    ----AndersonPointerAnalysis.java    forward analysis
    ----AnswerPointer.java              output the answer to result.txt
    ----FieldInfo.java                  data structure storing the info of field
    ----LocalInfo.java                  data structure storing the info of local variable
    ----MyPointerAnalysis.java          Main class
    ----WholeProgramTransformer.java    an engine to start the analysis

```