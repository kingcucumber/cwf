package com.demo.rest.domain;

public class Actor {

	public enum actorType {
		worker, decider
	};

	private actorType type;
	private String workflowID;
	private String name;
	private String domain;
	private float version;
	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getWorkflowID() {
		return workflowID;
	}

	public void setWorkflowID(String workflowID) {
		this.workflowID = workflowID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public float getVersion() {
		return version;
	}

	public void setVersion(float version) {
		this.version = version;
	}

	public actorType getType() {
		return type;
	}

	public void setType(actorType type) {
		this.type = type;
	}

}
