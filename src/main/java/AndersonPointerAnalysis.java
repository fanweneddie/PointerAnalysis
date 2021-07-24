package core;

import soot.*;
import soot.util.*;
import soot.jimple.*;
import java.util.*;

import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

// the class to hold pointer analyis
// which leverages the framework of ForwardFlowAnalysis
public class AndersonPointerAnalysis extends ForwardFlowAnalysis<Unit, Anderson> {
    // stores the testing queries and their results
    private final HashMap<Integer, HashSet<Integer>> queries 
                = new HashMap<Integer, HashSet<Integer>>();
    // ID number for allocation, -1 represents that it is invalid
	private static int allocId = -1;
	// constant strings of method name
    private static String BENCH_ALLOC = 
        "<benchmark.internal.BenchmarkN: void alloc(int)>";
    private static String BENCH_TEST = 
        "<benchmark.internal.BenchmarkN: void test(int,java.lang.Object)>";

    // Constructor for class
    // create use and def for each statement
    AndersonPointerAnalysis(UnitGraph g)
    {
        super(g);
    }

    // overwrite newInitialFlow
    // return an empty anderson as IN set
    @Override
    protected Anderson newInitialFlow() {
        Anderson anderson = new Anderson();
        return anderson;
    }

    // overwrite flowThrough
    // out[n] = gen[n] Union (in[n] - kill[n])
    // where gen is the point-to-set of the right value of the assignent
    // and kill is the point-to-set of the left value of the assignment
    @Override
    protected void flowThrough(Anderson in, Unit node, Anderson out) {
        Anderson gen = new Anderson();
        Anderson newIn = new Anderson();
        try{
            newIn = (Anderson)(in.clone());
        } catch(CloneNotSupportedException e){
            e.printStackTrace();
        }

        // for alloc and test function
        if (node instanceof InvokeStmt) {
            InvokeExpr ie = ((InvokeStmt) node).getInvokeExpr();
			String method = ie.getMethod().toString();
			// 1.1 the invoke statement is BenchmarkN.alloc(id), save the allocId
			if (method.equals(BENCH_ALLOC)) {
				// save it temporarily
                allocId = ((IntConstant)ie.getArgs().get(0)).value;
			}
			// 1.2 the invoke statement is BenchmarkN.test(), save the result of this test
			if (method.equals(BENCH_TEST)) {
				System.out.println(method);
				// the variable to be tested
				Value v = ie.getArgs().get(1);
				// the ID number of test
				int id = ((IntConstant)ie.getArgs().get(0)).value;
				// append the potential source set into queries
                HashSet<Integer> sourceSet = in.GetSourseSet(v);
                queries.put(id, sourceSet);
			}
        }

        // 2. for assignment statement
		if (node instanceof DefinitionStmt) {

			Value leftOperand = ((DefinitionStmt)node).getLeftOp();
			Value rightOperand = ((DefinitionStmt)node).getRightOp();
						
			// 2.1 the init(new) statement
			// make the new constraint for the left one.
			if (rightOperand instanceof NewExpr) {
                if (leftOperand instanceof Local) {
                    gen.AddNewLocal((Local)leftOperand, Integer.valueOf(allocId));
                }
                else if (leftOperand instanceof InstanceFieldRef) {
                    Value leftBase = ((InstanceFieldRef)(leftOperand)).getBase();
                    SootField leftField = ((InstanceFieldRef)(leftOperand)).getField();
                    gen.AddNewField((Local)leftBase, leftField, Integer.valueOf(allocId));
                }
            }
			// 2.2 the assignment of a = b
			if (leftOperand instanceof Local && rightOperand instanceof Local) {
                
                newIn.DeleteLocal((Local)leftOperand);
                gen.AssignLocalToLocal((Local)leftOperand, in, (Local)rightOperand);
                
			}
			// 2.3 the assignment of a.f = b
			if (leftOperand instanceof InstanceFieldRef && rightOperand instanceof Local) {

                Value leftBase = ((InstanceFieldRef)(leftOperand)).getBase();
                SootField leftField = ((InstanceFieldRef)(leftOperand)).getField();
                newIn.DeleteField((Local)leftBase, leftField);
                gen.AssignLocalToField((Local)leftBase, leftField, in, (Local)rightOperand);
			}
			// 2.4 the assignment of a = b.f
			if (leftOperand instanceof Local && rightOperand instanceof InstanceFieldRef) {
                
                Value rightBase = ((InstanceFieldRef)(rightOperand)).getBase();
                SootField rightField = ((InstanceFieldRef)(rightOperand)).getField();
                newIn.DeleteLocal((Local)leftOperand);
                gen.AssignFieldToLocal((Local)leftOperand, in, (Local)rightBase, rightField);
            }
            // 2.5 the assignment of a.f = b.f
            if (leftOperand instanceof InstanceFieldRef && rightOperand instanceof InstanceFieldRef) {
                
                Value leftBase = ((InstanceFieldRef)(leftOperand)).getBase();
                SootField leftField = ((InstanceFieldRef)(leftOperand)).getField();
                Value rightBase = ((InstanceFieldRef)(rightOperand)).getBase();
                SootField rightField = ((InstanceFieldRef)(rightOperand)).getField();
                newIn.DeleteField((Local)leftBase, leftField);
                gen.AssignFieldToField((Local)leftBase, leftField, in, (Local)rightBase, rightField);
            }
        }

        out.UnionMerge(newIn, gen);
    }
    

    // overwrite copy
    // copy source to dest
    @Override
    protected void copy(Anderson source, Anderson dest) {
        try {
            source = dest.clone(); 
        } catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    // overwrite merge as a meet function
    // we compute union as meet function
    @Override
    protected void merge(Anderson in1, Anderson in2, Anderson out) {
        out.UnionMerge(in1, in2);
    }

    // leverage the forward analysis framework
    // to do the pointer analysis
    public void run() {
        doAnalysis();
    }

    // return queries for test result output
    public HashMap<Integer, HashSet<Integer>> getQueries() {
        return queries;
    }
}