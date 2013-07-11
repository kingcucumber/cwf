package com.demo.rest.dao;

import com.demo.rest.domain.Action;
import com.demo.rest.domain.Actor;
import com.demo.rest.domain.Workflow;

public interface RestDao {

	public void saveWorkflow(Workflow workflow);

	public void saveActor(Actor actor);

	public Workflow getWorkflow(String workflowID, String domain);

	public void deleteWorkflow(String workflowID);

	public void update(String sql);
	
	public void saveAction(Action action);
	
	public Action getAction(String name);
	
}
