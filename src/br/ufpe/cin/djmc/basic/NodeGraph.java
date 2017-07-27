package br.ufpe.cin.djmc.basic;

import uk.ac.ox.cs.fdr.Node;

public class NodeGraph {
	
	char id;
	Node node;
	
	public char getId() {
		return id;
	}
	
	public void setId(char id) {
		this.id = id;
	}
	
	public Node getNode() {
		return node;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
}
