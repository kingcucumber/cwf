package com.demo.rest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.demo.message.StartMessage;
import com.demo.rest.dao.RestDao;
import com.demo.rest.domain.Action;
import com.demo.rest.domain.Actor;
import com.demo.rest.domain.Event;
import com.demo.rest.domain.Workflow;
import com.demo.rest.utils.RestUtils;
import com.demo.rest.utils.StatusUtils;

@Controller
public class WorkflowService {

	private Log logger = LogFactory.getLog(WorkflowService.class);

	ApplicationContext ctx = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml" });

	@RequestMapping(value = "/services/startWorkflowService", method = RequestMethod.POST)
	public void startWorkflow(@RequestBody StartMessage content,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		System.out.println(content.toString());
		try {
			logger.info("Workflow service has been started .........");

			String runID = RestUtils.getRandomString(20);
			JSONObject workflowProperties = new JSONObject();
			Workflow workflow = new Workflow();
			workflow.setWorkflowID(content.getWorkflowID());
			workflow.setDomain(content.getDomain());

			// get the detail properties of the workflow
			workflowProperties.put("workflowID", content.getWorkflowID());
			workflowProperties.put("domain", content.getDomain());
			workflowProperties.put("runID", runID);
			workflowProperties.put("message", "workflow service start!!");

			// set the other properties of the workflow

			workflow.setWorkflowType("test");
			workflow.setVersion(0.1f);
			workflow.setDescription("this is for test!");
			workflow.setRunID(runID);
			workflow.setStatus(StatusUtils.WORKFLOW_PENDING_STATUS);

			// store the workflow properties into the db server.
			RestDao restDao = (RestDao) ctx.getBean("restDaoImpl");
			restDao.saveWorkflow(workflow);
			logger.info("store the workflow into the database!");

			// store the actions into the db server.
			JSONArray jsnAction = content.getJsnAction();
			for (int i = 0; i < jsnAction.length(); i++) {
				JSONObject obj = jsnAction.getJSONObject(i);
				Action action = new Action();
				action.setMethod(obj.getString("method"));
				action.setName(obj.getString("name"));
				action.setType(obj.getString("type"));
				action.setParameters(obj.getString("parameters"));
				restDao.saveAction(action);
			}

			// get the start execution event
			Event event = new Event();
			event.setEventID(RestUtils.getRandomString(20));
			event.setTimestamp(System.currentTimeMillis());
			event.setType("WorkflowExecutionStarted");

			// add the event into the history
			JSONObject jsnObj = new JSONObject(event);
			JSONObject lastEvent = new JSONObject();
			lastEvent.put("lastEvent", event.getType());
			ArrayList<JSONObject> history = new ArrayList<JSONObject>();
			history.add(jsnObj);
			history.add(lastEvent);
			workflow.setHistory(history);

			// in response to the client (starter).
			System.out.println(history.toString());
			response.getWriter().write(workflowProperties.toString());
			response.setStatus(HttpStatus.OK.value());
		} catch (IOException e) {
			// new RuntimeException("the start procedure failed!");
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error("the start procedure failed!" + e.getMessage());
			response.getWriter().write("the start procedure failed!");
		}

	}

	@RequestMapping(value = "/services/registerDecider", method = RequestMethod.POST)
	public void registerDecider(@RequestBody Actor content,
			HttpServletRequest request, HttpServletResponse response) {
		Workflow wf = null;
		String workflowID = content.getWorkflowID();
		String domain = content.getDomain();

		RestDao restDao = (RestDao) ctx.getBean("restDaoImpl");
		wf = restDao.getWorkflow(workflowID, domain);
		if (wf == null) {
			try {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				logger.error("can't find the workflowID:" + workflowID
						+ " and the domain :" + domain);
				response.getWriter()
						.write("the WorkflowID or domain you registered doesn't exist,please check the String spelling!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			content.setStatus(StatusUtils.ACTOR_CHECKED_STATUS);
			restDao.saveActor(content);
			try {
				response.setStatus(HttpStatus.OK.value());
				logger.info("store the actor information into the database!");
				response.getWriter().write("register the decider success!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/services/pollDecisionTask", method = RequestMethod.GET)
	public void pollDecisionTask(@RequestBody Map content,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info("request comes from the decider for the decision task!");
		String workflowID, domain;
		RestDao restDao = (RestDao) ctx.getBean("restDaoImpl");
		Workflow wf = null;
		workflowID = (String) content.get("workflowID");
		domain = (String) content.get("domain");

		wf = restDao.getWorkflow(workflowID, domain);
		try {
			if (wf == null) {

				response.setStatus(HttpStatus.NOT_FOUND.value());
				logger.error("can't find the workflowID:" + workflowID
						+ " and the domain :" + domain);
				response.getWriter()
						.write("the WorkflowID or domain you registered doesn't exist,please check the String spelling!");

			} else {
				logger.info("let the decider to decide what's the next!");
				JSONObject jsnObj = new JSONObject(wf.getHistory());
				response.getWriter().write(jsnObj.toString());
				response.setStatus(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/services/scheduleTask", method = RequestMethod.POST)
	public void scheduleTask(@RequestBody Map content,
			HttpServletRequest request, HttpServletResponse response) {
		Workflow wf = null;
		String activity = (String) content.get("activity");
		String decision = (String) content.get("decision");

		String workflowID = (String) content.get("workflowID");
		String domain = (String) content.get("domain");

		RestDao restDao = (RestDao) ctx.getBean("restDaoImpl");
		wf = restDao.getWorkflow(workflowID, domain);
		try {
			if (wf == null) {

				response.setStatus(HttpStatus.NOT_FOUND.value());
				logger.error("can't find the workflowID:" + workflowID
						+ " and the domain :" + domain);
				response.getWriter()
						.write("the WorkflowID or domain you registered doesn't exist,please check the String spelling!");

			} else {
				logger.info("get the decision for the decider to add the decisionTaskList and update the history!");
				wf.getDecisionTaskList().put(decision, "Finished");
				wf.getActivityTaskList().put(activity, "Unallocated");
				response.setStatus(HttpStatus.OK.value());
				response.getWriter().write("the decision has been completed!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/services/registerWorker", method = RequestMethod.POST)
	public void registerWorker(@RequestBody Actor content,
			HttpServletRequest request, HttpServletResponse response) {
		Workflow wf = null;
		String workflowID = content.getWorkflowID();
		String domain = content.getDomain();

		RestDao restDao = (RestDao) ctx.getBean("restDaoImpl");
		wf = restDao.getWorkflow(workflowID, domain);
		if (wf == null) {
			try {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				logger.error("can't find the workflowID:" + workflowID
						+ " and the domain :" + domain);
				response.getWriter()
						.write("the WorkflowID or domain you registered doesn't exist,please check the String spelling!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			content.setStatus(StatusUtils.ACTOR_CHECKED_STATUS);
			restDao.saveActor(content);
			try {
				response.setStatus(HttpStatus.OK.value());
				logger.info("store the actor information into the database!");
				response.getWriter().write("register the decider success!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@RequestMapping(value = "/services/pollActivityTask", method = RequestMethod.GET)
	public void pollActivityTask(@RequestBody Map content,
			HttpServletRequest request, HttpServletResponse response) {
		Workflow wf = null;
		String workflowID = (String) content.get("workflowID");
		String domain = (String) content.get("domain");

		RestDao restDao = (RestDao) ctx.getBean("restDaoImpl");
		wf = restDao.getWorkflow(workflowID, domain);
		try {
			if (wf == null) {

				response.setStatus(HttpStatus.NOT_FOUND.value());
				logger.error("can't find the workflowID:" + workflowID
						+ " and the domain :" + domain);
				response.getWriter()
						.write("the WorkflowID or domain you registered doesn't exist,please check the String spelling!");

			} else {
				HashMap<String, String> hm = wf.getActivityTaskList();
				List<String> activity = new ArrayList<String>();

				if (hm == null) {
					logger.error("Activity TaskList is NULL!");
					throw new RuntimeException(
							"the activity tasklist is null!the workflow must go wrong!");
				} else if (hm.size() == 0) {
					logger.info("Activity TaskList is empty !");
					response.setStatus(HttpStatus.OK.value());
					response.getWriter().write(
							"there's not activity at this moment!");
				} else {
					String value = "Unallocated";
					Set<String> keySet = hm.keySet();
					for (String key : keySet) {
						if (value.equals(hm.get(key))) {
							activity.add(key);
						}
					}
					if (activity.size() == 0) {
						response.setStatus(HttpStatus.OK.value());
						response.getWriter().write(
								"there aren't available acitivities!");
					} else {
						String actionName = activity.get(0);
						Action action = restDao.getAction(actionName);
						JSONObject jsnObj = new JSONObject(action);
						response.getWriter().write(jsnObj.toString());
						response.setStatus(HttpStatus.OK.value());
						hm.put(actionName, "Allocated");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/services/updateState", method = RequestMethod.POST)
	public void updateStatus(HttpServletRequest request,
			HttpServletResponse response) {

	}

	// ///////////////////////////////////////////////////////////////////////////////

	@RequestMapping(value = "/services/getDecisionTask", method = RequestMethod.GET)
	public void getDecisionTask(HttpServletRequest request,
			HttpServletResponse response, Model model) throws Exception {

		try {
			logger.info("get decision task from workflow!");
			response.setStatus(HttpStatus.OK.value());
			response.getWriter().write("You have a decision task!");
		} catch (IOException e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error("the server can't send the decision!" + e.getMessage());
			response.getWriter().write(
					"the server can't send the decision to you!");
		}
	}

	@RequestMapping(value = "/services/stopWorkflowService", method = RequestMethod.GET)
	public void stopWorkflow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		try {
			logger.info("Workflow service has been stopped.......");
			response.setStatus(HttpStatus.OK.value());
			response.getWriter().write("workflow service stop!!");
		} catch (Exception e) {
			// new RuntimeException("the stop procedure failed!");
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error("the stop procedure failed!" + e.getMessage());
			response.getWriter().write("the stop procedure failed!");
		}
	}

}
