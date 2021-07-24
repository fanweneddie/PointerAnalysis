package core;

/*
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import soot.Local;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.SootField;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewExpr;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;
import soot.toolkits.graph.*;
*/

import java.util.*;
import soot.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.FlowSet;

// this class deals with the nodes(statements) in control flow graph
// and uses a iteration algorithm for dataflow analysis 
public class WholeProgramTransformer extends SceneTransformer {
	

	// function for dataflow analysis
	@Override
	protected void internalTransform(String arg0, Map<String, String> arg1) {
		// stores the result of each test
		//final TreeMap<Integer, Local> queries = new TreeMap<Integer, Local>();
		
		// the statements got from Jimple
		//ReachableMethods reachableMethods = Scene.v().getReachableMethods();
		//QueueReader<MethodOrMethodContext> qr = reachableMethods.listener();
		
		// Get Main Method
		SootMethod sMethod = Scene.v().getMainMethod();

		// Create graph based on the method
		UnitGraph graph = new BriefUnitGraph(sMethod.getActiveBody());

		// use Forward flow analysis framework
		AndersonPointerAnalysis apa = new AndersonPointerAnalysis(graph);

		// run flow analysis
		apa.run();
		
		// output the result
		HashMap<Integer, HashSet<Integer>> queries = apa.getQueries();
		String answer = "";
		for (HashMap.Entry<Integer, HashSet<Integer>> entry : queries.entrySet()) {
			Integer testId = entry.getKey();
			HashSet<Integer> sourceSet = entry.getValue();
			answer += testId.toString() + ":";
			for (Integer source : sourceSet) {
				answer += " " + source;
			}
			answer += "\n";
		}
		AnswerPrinter.printAnswer(answer);
	}

}
