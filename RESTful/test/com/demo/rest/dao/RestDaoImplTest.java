package com.demo.rest.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.demo.rest.domain.Action;
import com.demo.rest.domain.Workflow;

public class RestDaoImplTest {

	private Workflow workflow;
	RestDao restDao;

	@Before
	public void init() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
		restDao = (RestDao) ctx.getBean("restDaoImpl");

		workflow = new Workflow();
		workflow.setWorkflowID("1111");
		workflow.setDomain("test");
		workflow.setWorkflowType("test");
		workflow.setRunID("2222");
		workflow.setDescription("just for test");
		workflow.setVersion(0.1f);
	}

	@Test
	public void saveWorkflowTest() {
		restDao.saveWorkflow(workflow);
	}

	@Test
	public void getWorkflowTest() {
		Workflow result = restDao.getWorkflow("1111", "test");
		System.out.println("workflowID:" + result.getWorkflowID() + "\ndomain:"
				+ result.getDomain() + "\nworkflowType:"
				+ result.getWorkflowType() + "\nrunID:" + result.getRunID()
				+ "\nversion:" + result.getVersion() + "\ndescription:"
				+ result.getDescription());

	}

	@Test
	public void updateTest() {
		String sql = "update properties set version = version + 1.0 where workflowID = '1111'";
		restDao.update(sql);
	}

	@Test
	public void deleteWorkflowTest() {
		String id = "1111";
		restDao.deleteWorkflow(id);
	}

	@Test
	public void saveActionTest() {
		Action action = new Action();
		action.setName("test");
		action.setParameters("void");
		action.setType("action");
		action.setMethod("System.out.println");
		restDao.saveAction(action);
	}

	@Test
	public void getActionTest() {
		Action action = restDao.getAction("test");
		System.out.println("action's name:" + action.getName()
				+ "\naction's type:" + action.getType() + "\naction's method:"
				+ action.getMethod() + "\naction's parameters:"
				+ action.getParameters());
	}
}
