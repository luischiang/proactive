package org.sdnhub.proactive;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.flowprogrammer.Flow;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProgramFlow {

	private Node node;
	private ListMatch matchs;
	private ListAction actions;
	private Flow flow;
	
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public ListMatch getMatchs() {
		return matchs;
	}
	public void setMatchs(ListMatch matchs) {
		this.matchs = matchs;
	}
	public ListAction getActions() {
		return actions;
	}
	public void setActions(ListAction actions) {
		this.actions = actions;
	}
	public Flow getFlow() {
		return flow;
	}
	public void setFlow(Flow flow) {
		this.flow = flow;
	}
	
	public Flow program(){
		
		Flow newflow;
		
		newflow = flow;
		
		newflow.setActions(actions.joinActionList(this.node));
		newflow.setMatch(matchs.joinMatchFields(this.node));
	
		return newflow;
	}
	
}
