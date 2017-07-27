package djmc;

import uk.ac.ox.cs.fdr.Event;
import uk.ac.ox.cs.fdr.Node;

public class Action {
	
	Node from;
	Node to;
	Event event;
	
	public Action(Node node, Event event, Node destination) {
		this.from = node;
		this.event = event;
		this.to = destination;
		
	}

	public Node getFrom() {
		return from;
	}

	public void setFrom(Node from) {
		this.from = from;
	}

	public Node getTo() {
		return to;
	}

	public void setTo(Node to) {
		this.to = to;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
	
	
}
