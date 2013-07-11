package com.demo.rest.domain;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

public class Workflow {
	private String workflowID;
	private String domain;
	private float version;
	private String runID;
	private String workflowType;
	private String description;
	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	// key:decision name,value:status
	HashMap<String, String> DecisionTaskList;

	// key:activity name,value:status
	HashMap<String, String> ActivityTaskList;

	// consist of json object,which is event constant.
	ArrayList<JSONObject> history;

	private Log logger = LogFactory.getLog(Workflow.class);

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

	public float getVersion() {
		return version;
	}

	public void setVersion(float version) {
		this.version = version;
	}

	public String getRunID() {
		return runID;
	}

	public void setRunID(String runID) {
		this.runID = runID;
	}

	public HashMap<String, String> getDecisionTaskList() {
		return DecisionTaskList;
	}

	public void setDecisionTaskList(HashMap<String, String> decisionTaskList) {
		DecisionTaskList = decisionTaskList;
	}

	public HashMap<String, String> getActivityTaskList() {
		return ActivityTaskList;
	}

	public void setActivityTaskList(HashMap<String, String> activityTaskList) {
		ActivityTaskList = activityTaskList;
	}

	public ArrayList<JSONObject> getHistory() {
		return history;
	}

	public void setHistory(ArrayList<JSONObject> history) {
		this.history = history;
	}

	public String getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void initialize() {
		logger.info("initialize the Decision Task List!");
		this.DecisionTaskList = new HashMap<String, String>();

		logger.info("initialize the Activity Task List!");
		this.ActivityTaskList = new HashMap<String, String>();

		// history is consist of several events with the format of jsonobjects
		logger.info("initialize the History!");
		this.history = new ArrayList<JSONObject>();
	}

	public Workflow() {
		initialize();
	}
}
