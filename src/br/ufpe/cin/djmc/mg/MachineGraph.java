package br.ufpe.cin.djmc.mg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.kohsuke.graphviz.Edge;
import org.kohsuke.graphviz.Graph;

import br.ufpe.cin.djmc.basic.NodeGraph;
import uk.ac.ox.cs.fdr.Assertion;
import uk.ac.ox.cs.fdr.DeadlockFreeAssertion;
import uk.ac.ox.cs.fdr.Event;
import uk.ac.ox.cs.fdr.InputFileError;
import uk.ac.ox.cs.fdr.Machine;
import uk.ac.ox.cs.fdr.Node;
import uk.ac.ox.cs.fdr.Session;
import uk.ac.ox.cs.fdr.Transition;
import uk.ac.ox.cs.fdr.TransitionList;
import uk.ac.ox.cs.fdr.fdr;

public class MachineGraph {
	static int nodeID = 0;
	static int destinationID = 0;
	static Machine machine;
	static ArrayList<Action> actions = new ArrayList<>();
	static ArrayList<NodeGraph> nodegraphs = new ArrayList<>();

	public static void main(String[] args) {

		PrintStream out = System.out;
		ArrayList<Node> nodes = new ArrayList<Node>();
		

		try {

			Session session = new Session();
			session.loadFile("ClientServerConf.csp");
			//session.loadFile("test.csp");

			for (Assertion assertion : session.assertions()) {

				assertion.execute(null);
				System.out.println(assertion.toString() + " " + (assertion.passed() ? "Passed" : "Failed"));

				if (assertion instanceof DeadlockFreeAssertion) {
					machine = ((DeadlockFreeAssertion) assertion).machine();
					Node node = machine.rootNode();

					for (Transition transition : machine.transitions(node)) {
						describeTransitions(out, machine, session, node, transition, nodes, true);
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
			Transition transition, ArrayList<Node> nodes, boolean recurse) {
		
		Event event = session.uncompileEvent(transition.event());
		Node destination = transition.destination();

		nodeID = nodeID + 1;
		destinationID = nodeID + 1;

		if (node.equals(machine.rootNode())) {
			nodeID = 1;
		}
		
		if (destination.equals(machine.rootNode())) {
			destinationID = 1;
		}

		//out.println(node + " -> " + event + " -> " + destination);

		TransitionList childList = machine.transitions(destination);

		if (childList.isEmpty()) {
			recurse = false;
		}

		if (nodes.contains(destination)) {
			recurse = false;
		}

/*		if (!nodes.contains(machine.rootNode())) {
			nodes.add(machine.rootNode());
			nodegraphs.put(nodeID, new NodeGraph(nodeID, machine.rootNode()));
		}*/
		
		NodeGraph src = new NodeGraph(nodeID, node);
		if (!nodes.contains(node)) {
			nodes.add(node);
			nodegraphs.add(src);
		}
		
		NodeGraph dest = new NodeGraph(destinationID, destination);
		if (!nodes.contains(destination)) {
			nodes.add(destination);
			nodegraphs.add(dest);
		}
		
		for(NodeGraph nodegraph: nodegraphs) {
			if(nodegraph.getNode().equals(node)) {
				src = nodegraph;
			}
			
			if(nodegraph.getNode().equals(destination)) {
				dest = nodegraph;
			}
		}
		
		Action action = new Action(src, event, dest);
		actions.add(action);
		
		if (recurse) {
			for (Transition child : machine.transitions(destination)) {
				describeTransitions(out, machine, session, destination, child, nodes, true);
			}
		}

	}

	private static void createGraph(Machine machine, Session session, ArrayList<Action> actions) {

		Graph g = new Graph();
		g.id(session.machineName(machine).toString());

		for (Action action : actions) {
			
			String event = action.getEvent().toString();
					
			Edge e = new Edge(new org.kohsuke.graphviz.Node().id(String.valueOf(action.getFrom().getId())),
					new org.kohsuke.graphviz.Node().id(String.valueOf(action.getTo().getId()) + " [ label=\"" + event + "\"];"));
			g.edge(e);
		}
		
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(session.machineName(machine).toString()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
		g.writeTo(out);
	}

}
