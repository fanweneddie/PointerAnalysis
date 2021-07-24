package core;

import java.io.File;

import soot.PackManager;
import soot.Transform;

public class MyPointerAnalysis {

	// show the usage of the command
	private static void printUsageAndExit() {
		System.out.println("Usage: java -jar analyzer.jar <inputPath> <org.package.mainClass>");
		System.exit(-1);
	}

	// args[0] = "/root/workspace/code"
	// args[1] = "test.Hello"
	public static void main(String[] args) {
		// invalid number of input arguments
		if (args.length < 2) 
			printUsageAndExit();
		// args[0] is the inputpath
		File path = new File(args[0]);
		if (!path.isDirectory()) 
			printUsageAndExit();
		String class_path = args[0] 
				+ File.pathSeparator + args[0] + File.separator + "rt.jar"
				+ File.pathSeparator + args[0] + File.separator + "jce.jar";	
		// args[1] is the mainclass for analysis
		String main_class = args[1];

		System.out.println("MyPointerAnalysis: main class is " + main_class);
		
		// add a phase to transformer pack by call Pack.add
		PackManager.v().getPack("wjtp").add(
			new Transform("wjtp.mypta", new WholeProgramTransformer()));
		// Give control to Soot to process all options,
    	// and the InternalTransform will get called.
		soot.Main.main(new String[] {
			// whole program mode
			"-w",
			// enable a phase to build a call graph
			"-p", "cg.spark", "enabled:true",
			// enable a phase of "wjtp.mypta"
			"-p", "wjtp.mypta", "enabled:true",
			"-soot-class-path", class_path,
			// generate Jimple IR
			"-f", "J",
			main_class				
		});
	}

}
