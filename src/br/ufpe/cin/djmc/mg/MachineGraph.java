package br.ufpe.cin.djmc.mg;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.graphviz.Edge;
import org.kohsuke.graphviz.Graph;
import org.kohsuke.graphviz.GraphObject;

import br.ufpe.cin.djmc.basic.EdgeGraph;
import br.ufpe.cin.djmc.basic.NodeGraph;
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
	static int nodeID = 0;
	static int destinationID = 0;
	static Machine machine;
	static Map<Integer, NodeGraph> nodegraphs = new HashMap<>();

	public static void main(String[] args) {

		PrintStream out = System.out;
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
					Node node = machine.rootNode();

					for (Transition transition : machine.transitions(node)) {
						describeTransitions(out, machine, session, node, transition, nodes, actions, true);
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

	private static void describeTransitions(PrintStream out, Machine machine, Session session, Node node,
			Transition transition, ArrayList<Node> nodes, ArrayList<Action> actions, boolean recurse) {
		
		//EdgeGraph event = new EdgeGraph(session.uncompileEvent(transition.event()));
		
		Event event = session.uncompileEvent(transition.event());
		Node destination = transition.destination();

		nodeID = nodeID + 1;
		destinationID = nodeID + 1;

		if (node.equals(machine.rootNode())) {
			nodeID = 1;
			destinationID = nodeID + 1;
		}

		out.println(node + " -> " + event + " -> " + destination);

		TransitionList childList = machine.transitions(destination);

		if (childList.isEmpty()) {
			recurse = false;
		}

		if (nodes.contains(destination)) {
			recurse = false;
		}

		if (!nodes.contains(machine.rootNode())) {
			nodes.add(machine.rootNode());
			nodegraphs.put(nodeID, new NodeGraph(nodeID, machine.rootNode()));
		}
		
		if (!nodes.contains(destination)) {
			nodes.add(destination);
			nodegraphs.put(destinationID, new NodeGraph(destinationID, destination));
		}
		
		if (recurse) {
			for (Transition child : machine.transitions(destination)) {
				describeTransitions(out, machine, session, destination, child, nodes, actions, true);
			}
		}

	}

	private static void createGraph(Machine machine, Session session, ArrayList<Action> actions) {

		Graph g = new Graph();
		g.id(session.machineName(machine).toString());

		for (Action action : actions) {
					
			Edge e = new Edge(new org.kohsuke.graphviz.Node().id(String.valueOf(action.getFrom().getId())),
					new org.kohsuke.graphviz.Node().id(String.valueOf(action.getTo().getId())));
			g.edge(e);
		}

		g.writeTo(System.out);
	}

}
