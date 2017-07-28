package br.ufpe.cin.djmc.basic;

import uk.ac.ox.cs.fdr.Event;

public class EdgeGraph {

	Event e;

	public EdgeGraph(Event e) {
		super();
		this.e = e;
	}

	public Event getE() {
		return e;
	}

	public void setE(Event e) {
		this.e = e;
	}

}
