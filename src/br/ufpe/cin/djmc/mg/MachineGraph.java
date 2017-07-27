package br.ufpe.cin.djmc.mg;

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

public class MachineGraph {

	public static void main(String[] args) {

		PrintStream out = System.out;
		Machine machine;
		Node node;
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Action> actions = new ArrayList<>();

		try {

			Session session = new Session();
			session.loadFile("ClientServerConf.csp");

			for (Assertion assertion : session.assertions()) {

				assertion.execute(null);
				System.out.println(assertion.toString() + " " + (assertion.passed() ? "Passed" : "Failed"));

				if (assertion instanceof DeadlockFreeAssertion) {
					machine = ((DeadlockFreeAssertion) assertion).machine();
					node = machine.rootNode();

					for (Transition transition : machine.transitions(node)) {
						describeTransitions(out, machine, session, node, transition, nodes, actions, true);
					}

	//				createGraph(machine, session, actions);

				}
			}

			out.println();

		} catch (InputFileError error) {
			System.out.println(error);
		}

		fdr.libraryExit();
	}

	private static void describeTransitions(PrintStream out, Machine machine, Session session, Node node,
			Transition transition, ArrayList<Node> nodes, ArrayList<Action> actions, boolean recurse) {

		Event event = session.uncompileEvent(transition.event());
		Node destination = transition.destination();

		out.println(node + " -> " +  event + " -> " + destination + "<>");

		Action action = new Action(node, event, destination);

		actions.add(action);

		TransitionList childList = machine.transitions(transition.destination());

		if (childList.isEmpty()) {
			recurse = false;
		}

		if (nodes.contains(destination)) {
			recurse = false;
		}

		if (!nodes.contains(machine.rootNode())) {
			nodes.add(machine.rootNode());
		}

		if (!nodes.contains(transition.destination())) {
			nodes.add(transition.destination());
		}

		if (recurse) {
			for (Transition child : machine.transitions(transition.destination())) {
				describeTransitions(out, machine, session, transition.destination(), child, nodes, actions, true);
			}
		}

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

}
