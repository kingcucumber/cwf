package com.demo.message;

import org.json.JSONArray;

public class StartMessage {

	private String workflowID;
	private String domain;
	private JSONArray jsnAction;

	public String getWorkflowID() {
		return workflowID;
	}

	public void setWorkflowID(String workflowID) {
		this.workflowID = workflowID;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public JSONArray getJsnAction() {
		return jsnAction;
	}

	public void setJsnAction(JSONArray jsnAction) {
		this.jsnAction = jsnAction;
	}

}
