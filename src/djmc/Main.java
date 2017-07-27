package djmc;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.graphviz.Edge;
import org.kohsuke.graphviz.Graph;
import org.kohsuke.graphviz.GraphObject;

import uk.ac.ox.cs.fdr.Assertion;
import uk.ac.ox.cs.fdr.DeadlockFreeAssertion;
import uk.ac.ox.cs.fdr.Event;
import uk.ac.ox.cs.fdr.InputFileError;
import uk.ac.ox.cs.fdr.Machine;
import uk.ac.ox.cs.fdr.Node;
import uk.ac.ox.cs.fdr.NodePath;
import uk.ac.ox.cs.fdr.Session;
import uk.ac.ox.cs.fdr.Transition;
import uk.ac.ox.cs.fdr.TransitionList;
import uk.ac.ox.cs.fdr.fdr;

public class Main {

	public static void main(String[] args) {

		PrintStream out = System.out;
		Machine machine;
		Node root;
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Action> actions = new ArrayList<>();

		try {

			Session session = new Session();
			session.loadFile("calculator.csp");

			for (Assertion assertion : session.assertions()) {

				assertion.execute(null);
				System.out.println(assertion.toString() + " " + (assertion.passed() ? "Passed" : "Failed"));

				if (assertion instanceof DeadlockFreeAssertion) {
					machine = ((DeadlockFreeAssertion) assertion).machine();
					root = machine.rootNode();

					for (Transition node : machine.transitions(root)) {
						describeTransitions(out, machine, session, root, node, nodes, actions, true);
					}

					createGraph(machine, session, actions);

				}
			}

			out.println();

		} catch (InputFileError error) {
			System.out.println(error);
		}

		fdr.libraryExit();
	}

	private static void createGraph(Machine machine, Session session, ArrayList<Action> actions) {

		Graph g = new Graph();
		g.id(session.machineName(machine).toString());

		for (Action action : actions) {
			Edge e = new Edge(new org.kohsuke.graphviz.Node().id(action.from),
					new org.kohsuke.graphviz.Node().id(action.to));
			g.edge(e);
		}

		g.writeTo(System.out);
	}

	private static void describeTransitions(PrintStream out, Machine machine, Session session, Node node,
			Transition root, ArrayList<Node> nodes, ArrayList<Action> actions, boolean recurse) {

		Event event = session.uncompileEvent(root.event());
		Node destination = root.destination();

		// out.println(session.uncompileEvent(root.event()) + " -> " +
		// root.destination());

		Action action = new Action(node, event, destination);

		actions.add(action);

		TransitionList childList = machine.transitions(root.destination());

		if (childList.isEmpty()) {
			recurse = false;
		}

		if (nodes.contains(destination)) {
			recurse = false;
		}

		if (!nodes.contains(machine.rootNode())) {
			nodes.add(machine.rootNode());
		}

		if (!nodes.contains(root.destination())) {
			nodes.add(root.destination());
		}

		if (recurse) {
			for (Transition child : machine.transitions(root.destination())) {
				describeTransitions(out, machine, session, root.destination(), child, nodes, actions, true);
			}
		}

	}

}
