package com.demo.rest.dao;

import org.junit.Test;

import com.demo.rest.domain.Workflow;

public class ObjectDaoTest {

	@Test
	public void createTest() {
		Object[] obj = new Object[] { "113456789", "demo", "cwf.cisco",
				"abcdefghijkl", 0, "workflow" };
		ObjectDao
				.create("insert into properties (workflowID,domain,workflowType,runID,version,description) values (?,?,?,?,?,?) ",
						obj);
	}

	@Test
	public void getObjectTest() {
		String sql = "select * from properties where workflowID = 113456789";

		Workflow workflow = (Workflow) ObjectDao.getObject(sql, Workflow.class);

		System.out.println("workflowID:" + workflow.getWorkflowID()
				+ "\ndomain:" + workflow.getDomain() + "\nworkflowType:"
				+ workflow.getWorkflowType() + "\nrunID:" + workflow.getRunID()
				+ "\nversion:" + workflow.getVersion() + "\ndescription:"
				+ workflow.getDescription());
	}

	@Test
	public void update() {
		String sql1 = "update properties set version = version + 1 where workflowID = '113456789'";

		String sql2 = "select * from properties where workflowID = 113456789";
		ObjectDao.update(sql1);
		Workflow workflow = (Workflow) ObjectDao.getObject(sql2, Workflow.class);
		System.out.println("workflow's version:" + workflow.getVersion());	
	}
	
	@Test
	public void delete(){
		String sql = "delete from properties where workflowID = '123456789'";
		ObjectDao.delete(sql);
	}
}
