package djmc;

import java.io.PrintStream;

import uk.ac.ox.cs.fdr.Assertion;
import uk.ac.ox.cs.fdr.DeadlockFreeAssertion;
import uk.ac.ox.cs.fdr.InputFileError;
import uk.ac.ox.cs.fdr.Machine;
import uk.ac.ox.cs.fdr.Node;
import uk.ac.ox.cs.fdr.Session;
import uk.ac.ox.cs.fdr.Transition;

public class MachineGraph {
	
	PrintStream out = System.out;
	Machine machine = null;
	Node root = null;
	
	public MachineGraph() {}

	try
	{

		Session session = new Session();
		session.loadFile("test.csp");

		for (Assertion assertion : session.assertions()) {

			assertion.execute(null);
			out.println(assertion.toString() + " " + (assertion.passed() ? "Passed" : "Failed"));

			if (assertion instanceof DeadlockFreeAssertion) {

				machine = ((DeadlockFreeAssertion) assertion).machine();
				root = machine.rootNode();
				
				for(Transition node : machine.transitions(root)) {
					//getTransitions();
				}

			}
		}

	}catch(
	InputFileError error)
	{
		out.println(error);
	}
}
