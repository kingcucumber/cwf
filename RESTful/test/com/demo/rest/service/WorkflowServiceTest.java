package com.demo.rest.service;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.demo.rest.domain.Workflow;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({ "classpath:applicationContext.xml",
//		"file:WebRoot/WEB-INF/rest-servlet.xml" })
public class WorkflowServiceTest {
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private WorkflowService wfs;

	@Before
	public void setup() {
		this.wfs = new WorkflowService();
		Assert.assertNotNull(this.wfs);
	}

	@Test
	public void testStartWorkflow() throws Exception {

		String expectedMessage = "workflow service start!!";
		request = new MockHttpServletRequest("GET",
				"/services/startWorkflowService");
		response = new MockHttpServletResponse();
		JSONObject jsn = new JSONObject();
		jsn.put("workflowID", "1234789");
		jsn.put("domain","demo");
		Assert.assertNotNull(response);
		Assert.assertNotNull(request);
//		wfs.startWorkflow(jsn, request, response);
		String message = response.getContentAsString();
		System.out.println(message);
		Assert.assertEquals(expectedMessage, message);
	}

	@Test
	public void testGetDecisionTask() throws Exception {
		String expectedMessage = "You have a decision task!";
		request = new MockHttpServletRequest("GET", "/services/getDecisionTask");
		response = new MockHttpServletResponse();
		Assert.assertNotNull(request);
		Assert.assertNotNull(response);
		wfs.getDecisionTask(request, response, null);
		String message = response.getContentAsString();
		Assert.assertEquals(expectedMessage, message);
	}

	@Test
	public void testStopWorkflowService() throws Exception {
		String expectedMessage = "workflow service stop!!";
		request = new MockHttpServletRequest("GET",
				"/services/stopWorkflowService");
		response = new MockHttpServletResponse();
		Assert.assertNotNull(request);
		Assert.assertNotNull(response);
		wfs.stopWorkflow(request, response);
		String message = response.getContentAsString();
		Assert.assertEquals(expectedMessage, message);
	}
}
