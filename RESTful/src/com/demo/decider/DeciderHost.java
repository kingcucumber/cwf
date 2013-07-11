package com.demo.decider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.demo.rest.domain.Actor;
import com.demo.rest.domain.Actor.actorType;
import com.demo.rest.utils.RestUtils;
import com.demo.rest.utils.StatusUtils;

public class DeciderHost {
	private Actor decider;

	public DeciderHost() {
		this.decider = new Actor();
		this.decider.setDomain("com.cisco");
		this.decider.setName("testDecider");
		this.decider.setType(actorType.decider);
		this.decider.setVersion(0.1f);
		this.decider.setWorkflowID("123456789");
		this.decider.setStatus(0);
	}

	public void pollDecisionTask(HttpClient hc, HttpPost hp) {
		// encapsulation the request body with workflowID and domain,also
		// taskToken.
		JSONObject jsnObj = new JSONObject();
		HttpResponse response = null;
		try {
			jsnObj.put("workflowID", this.decider.getWorkflowID());
			jsnObj.put("domain", this.decider.getWorkflowID());

			StringEntity content = new StringEntity(jsnObj.toString());
			content.setContentType("application/json");
			hp.setEntity(content);
			response = hc.execute(hp);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			// deal with the response body ,and decide what's the next.
			ArrayList<JSONObject> history = (ArrayList<JSONObject>) response
					.getEntity();
			Iterator it = history.iterator();
			String url = "http://localhost:8080/RESTful/services/scheduleTask";

			while (it.hasNext()) {

				JSONObject lastEvent = (JSONObject) it.next();

				if (lastEvent.getString("lastEvent") == null) {
					throw new RuntimeException(
							"can't find the last event,so it couldn't decide what's the next!");
				} else if (lastEvent.getString("lastEvent") == "WorkflowExecutionStarted") {
					// ScheduleVerifyOrderActivity
					jsnObj.put("decision", "scheduleVerify");
					jsnObj.put("activity", "VerifyOrderActivity");

				} else if (lastEvent.getString("lastEvent") == "CompleteVerifyOrderActivity") {
					// ScheduleChargeCreditCardActivity
					jsnObj.put("decision", "scheduleCharge");
					jsnObj.put("activity", "ChargeCreditCardActivity");

				} else if (lastEvent.getString("lastEvent") == "CompleteChargeCreditCardActivity") {
					// ScheduleCompleteShipOrderActivity
					jsnObj.put("decision", "scheduleShip");
					jsnObj.put("activity", "ShipOrderActivity");

				} else if (lastEvent.getString("lastEvent") == "CompleteShipOrderActivity") {
					// ScheduleRecordOrderCompletion
					jsnObj.put("decision", "scheduleRecord");
					jsnObj.put("activity", "RecordOrderActivity");

				} else if (lastEvent.getString("lastEvent") == "CompleteRecordOrderCompletion") {
					// CloseWorkflow
					url = "http://localhost:8080/RESTful/services/stopWorkflowService";

				}
			}
			// send the decision.

			content = new StringEntity(jsnObj.toString());
			content.setContentType("application/json");
			URI uri = new URI(url);
			hp.setURI(uri);
			hp.setEntity(content);
			response = hc.execute(hp);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void registerDecider(HttpClient hc, HttpPost hp) {

		try {
			JSONObject json = new JSONObject();

			json.put("domain", this.decider.getDomain());
			json.put("name", this.decider.getName());
			json.put("type", this.decider.getType());
			json.put("version", this.decider.getVersion());
			json.put("workflowID", this.decider.getWorkflowID());
			json.put("status", this.decider.getStatus());

			StringEntity content = new StringEntity(json.toString());
			content.setContentType("application/json");
			hp.setEntity(content);
			HttpResponse response;
			response = hc.execute(hp);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		String registerDeciderUrl = "http://localhost:8080/RESTful/services/registerDecider";
		String pollDecisionTaskUrl = "http://localhost:8080/RESTful/services/pollDecisionTask";

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(registerDeciderUrl);
		HttpPost httpPost2 = new HttpPost(pollDecisionTaskUrl);

		DeciderHost client = new DeciderHost();
		System.out.println("httpClient:" + httpClient + "\nhttpPost:"
				+ httpPost);

		client.registerDecider(httpClient, httpPost);
		System.out.println("client:" + client.toString());

		client.pollDecisionTask(httpClient, httpPost2);
	}
}
